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

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

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
                .profileImageUrl(request.profileImageUrl())
                .build();

        Member savedMember = memberRepository.save(member);
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
}