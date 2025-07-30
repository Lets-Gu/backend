package avengers.lion.auth.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/*
인증 객체인 Authentication 객체안에 사용자 정보를 담기 위한 클래스
OAuth2User 인터페이스의 구현체로 작성해야 Authentication 객체 안에 담을 수 있음
 */
@RequiredArgsConstructor
public class KakaoMemberDetails implements OAuth2User {

    private final String email;
    @Getter
    private final Long memberId;
    private final List<? extends GrantedAuthority> authorities;
    private final Map<String, Object> attributes;

    @Override
    public String getName() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
