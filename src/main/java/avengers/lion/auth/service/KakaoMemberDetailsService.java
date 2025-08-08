package avengers.lion.auth.service;


import avengers.lion.auth.domain.KakaoMemberDetails;
import avengers.lion.auth.domain.KakaoUserInfo;
import avengers.lion.member.domain.Member;
import avengers.lion.member.domain.MemberRole;
import avengers.lion.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
/*
OAuth2 사용자 정보 처리
 */
@Slf4j
public class KakaoMemberDetailsService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("oAuth2User : {}", oAuth2User);
        // request 에서 사용자 정보가 담긴 객체를 가져옴
        KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(oAuth2User.getAttributes());

        Member member = memberRepository.findByEmail(kakaoUserInfo.getEmail())
                .orElseGet(()->
                        memberRepository.save(
                                Member.builder()
                                        .email(kakaoUserInfo.getEmail())
                                        .nickname(kakaoUserInfo.getNickname())
                                        .profileImageUrl(kakaoUserInfo.getProfileImageUrl())
                                        .role(MemberRole.ROLE_USER)
                                        .build()
                        ));
        log.info("member : {}", member);
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(member.getRole().name());
        return new KakaoMemberDetails(kakaoUserInfo.getEmail(),member.getId(),
                Collections.singletonList(authority),
                oAuth2User.getAttributes());
    }
}
