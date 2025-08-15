package avengers.lion.auth.service;

import avengers.lion.auth.domain.CustomUserDetails;
import avengers.lion.auth.dto.RegisterResponse;
import avengers.lion.auth.dto.LoginRequest;
import avengers.lion.auth.dto.LoginResponse;
import avengers.lion.auth.dto.LoginWithTokenResponse;
import avengers.lion.auth.dto.RegisterRequest;
import avengers.lion.global.exception.BusinessException;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.global.jwt.TokenProvider;
import avengers.lion.member.domain.Member;
import avengers.lion.member.domain.MemberRole;
import avengers.lion.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private static final List<String> PROFILE_IMAGES = List.of(
            "https://res.cloudinary.com/do5bj2fie/image/upload/v1755264379/6.%ED%86%A0%EB%81%BC_y5lqzd.png",
            "https://res.cloudinary.com/do5bj2fie/image/upload/v1755264379/5.%EC%98%A4%EB%A6%AC_ybspmo.png",
            "https://res.cloudinary.com/do5bj2fie/image/upload/v1755264378/4.%EA%B3%A0%EC%96%91%EC%9D%B4_xlhylh.png",
            "https://res.cloudinary.com/do5bj2fie/image/upload/v1755264378/3.%EA%B3%A0%EC%8A%B4%EB%8F%84%EC%B9%98_l8mcp9.png",
            "https://res.cloudinary.com/do5bj2fie/image/upload/v1755264378/1.%EA%B0%95%EC%95%84%EC%A7%80_jtg9re.png",
            "https://res.cloudinary.com/do5bj2fie/image/upload/v1755264378/2.%EA%B1%B0%EB%B6%81%EC%9D%B4_szcdbc.png"
    );


    public void signUp(RegisterRequest request) {
        // 이메일 중복 검사
        if (memberRepository.existsByEmail(request.email())) {
            throw new BusinessException(ExceptionType.EMAIL_ALREADY_EXISTS);
        }

        // 회원 생성
        Member member = Member.builder()
                .email(request.email())
                .nickname(request.nickname())
                .password(passwordEncoder.encode(request.password()))
                .role(MemberRole.ROLE_USER)
                .profileImageUrl(getRandomProfileImage())
                .build();

        memberRepository.save(member);
    }

    public LoginResponse login(LoginRequest request) {
        // 인증
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Member member = userDetails.getMember();

            return new LoginResponse(
                    member.getId(),
                    member.getEmail(),
                    member.getNickname(),
                    member.getProfileImageUrl()
            );
        } catch(BusinessException ex){
            throw new BusinessException(ExceptionType.INVALID_PASSWORD);
        }

    }

    public String generateAccessToken(Member member) {
        return tokenProvider.createAccessToken(member.getId(), member.getRole().name());
    }

    @Transactional
    public LoginWithTokenResponse loginWithToken(LoginRequest request) {
        // 사용자 인증
        LoginResponse loginResponse = login(request);
        
        // 사용자 정보로 토큰 생성
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ExceptionType.MEMBER_NOT_FOUND));
        
        String accessToken = generateAccessToken(member);

        return new LoginWithTokenResponse(loginResponse, accessToken);
    }
    private String getRandomProfileImage(){
        Random random = new Random();
        return PROFILE_IMAGES.get(random.nextInt(PROFILE_IMAGES.size()));
    }
}