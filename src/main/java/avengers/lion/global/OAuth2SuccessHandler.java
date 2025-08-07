package avengers.lion.global;

import avengers.lion.auth.domain.KakaoUserInfo;
import avengers.lion.global.exception.BusinessException;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.global.jwt.TokenDto;
import avengers.lion.global.jwt.TokenProvider;
import avengers.lion.global.response.SuccessResponseBody;
import avengers.lion.member.Member;
import avengers.lion.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor

/*
카카오 로그인 성공 후 실행되는 커스텀 핸들러
우리 서비스 전용 JWT를 발급하고, 클라이언트를 accessToken, refreshToken을 담은 URL로 리다이렉트 하는 역할
SimpleUrl~~ : 스프링 시큐리티의 기본 성공 핸들러
 */
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException{
        // 카카오에서 내려준 사용자 정보를 꺼냄
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        // 파싱하기 위한 래퍼 클래스
        KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(oAuth2User.getAttributes());

        // DB에 이메일로 가입된 유저가 있는지 확인
        Member member = memberRepository.findByEmail(kakaoUserInfo.getEmail())
                .orElseThrow(()->new BusinessException(ExceptionType.MEMBER_NOT_FOUND));

        // 사용자 식별 정보를 이용해 토큰 발급
        TokenDto tokenDto = tokenProvider.createToken(member.getId(), member.getRole().name());

        // JSON 응답으로 토큰 반환
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        SuccessResponseBody<Void> successResponse = new SuccessResponseBody<>();
        response.setHeader("Access-Token",tokenDto.accessToken());
        response.setHeader("Refresh-Token",tokenDto.refreshToken());
        log.info("로그인성공");
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(successResponse);
            response.getWriter().write(jsonResponse);
        } catch(IOException e){
            throw new BusinessException(ExceptionType.UNEXPECTED_SERVER_ERROR);
        }
    }
}
