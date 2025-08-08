package avengers.lion.global.jwt;

import avengers.lion.auth.domain.CustomUserDetails;
import avengers.lion.member.domain.Member;
import avengers.lion.member.domain.MemberRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;


/*
토큰 생성, 검증 클래스
 */
@Component
@RequiredArgsConstructor
public class TokenProvider {

    private static final String AUTH_KEY = "AUTHORITY";
    private static final String AUTH_MEMBER_ID = "MEMBER_ID";

    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.access-token-validity-in-seconds}")
    private long accessTokenValiditySeconds;

    private Key secretkey;

    @PostConstruct
    public void initKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.secretkey = Keys.hmacShaKeyFor(keyBytes);
    }

    /*
    Access token 생성
     */
    public String createAccessToken(Long memberId, String role){
        long now = (new Date()).getTime();
        Date accessValidity = new Date(now + this.accessTokenValiditySeconds * 1000);

        return Jwts.builder()
                .addClaims(Map.of(AUTH_MEMBER_ID, memberId))
                .addClaims(Map.of(AUTH_KEY, role))
                .signWith(secretkey, SignatureAlgorithm.HS256)
                .setExpiration(accessValidity)
                .compact();
    }


    /*
    토큰이 유효한지 검사 (만료 포함)
     */
    public boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretkey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            return false;
        } catch (ExpiredJwtException e) {
            return false;
        } catch (UnsupportedJwtException e) {
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /*
    클라이언트가 보낸 JWT 토큰을 스프링 시큐리티가 이해하는 인증 객체로 변환
     */
    public Authentication getAuthentication(String token) {
        // 토큰 서명을 검증
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretkey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        // 토큰의 권한을 리스트로 변환
        GrantedAuthority authority = new SimpleGrantedAuthority(claims.get(AUTH_KEY).toString());
        List<GrantedAuthority> simpleGrantedAuthorities = Collections.singletonList(authority);
        // 사용자를 나타내는 principal 객체 생성
        Long memberId = Long.valueOf(claims.get(AUTH_MEMBER_ID).toString());
        MemberRole memberRole = MemberRole.valueOf(claims.get(AUTH_KEY).toString());
        
        // Member 객체를 생성 (인증용 최소 정보만 포함)
        Member member = Member.builder()
                .email("user@example.com") // 실제로는 토큰에서 추출하거나 별도 조회 필요
                .nickname("User")
                .password("")
                .role(memberRole)
                .build();
        
        // Reflection을 사용해 ID 설정 (임시 방법)
        try {
            java.lang.reflect.Field idField = Member.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(member, memberId);
        } catch (Exception e) {
            // Reflection 실패시 기본값 사용
        }
        
        CustomUserDetails principal = new CustomUserDetails(member);
        /*
        principal : 인증된 사용자 자체
        credentials : 사용자 인증을 증명하는 비밀값, jwt에서는 토큰
        authorities : 사용자가 가진 권한 목록
         */
        return new UsernamePasswordAuthenticationToken(principal, token, simpleGrantedAuthorities);
    }
}
