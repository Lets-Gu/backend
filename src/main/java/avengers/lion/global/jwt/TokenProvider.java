package avengers.lion.global.jwt;

import avengers.lion.auth.domain.KakaoMemberDetails;
import avengers.lion.auth.repository.RefreshTokenRepository;
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
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.access-token-validity-in-seconds}")
    private long accessTokenValiditySeconds;
    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValiditySeconds;

    private Key secretkey;

    @PostConstruct
    public void initKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.secretkey = Keys.hmacShaKeyFor(keyBytes);
    }

    /*
    Access, Refresh token 생성
     */
    public TokenDto createToken(Long memberId, String role){
        long now = (new Date()).getTime();

        Date accessValidity = new Date(now + this.accessTokenValiditySeconds * 1000);
        Date refreshValidity = new Date(now + this.refreshTokenValiditySeconds * 1000);

        // 액세스 토큰 생성
        String accessToken = Jwts.builder()
                .addClaims(Map.of(AUTH_MEMBER_ID, memberId))
                .addClaims(Map.of(AUTH_KEY, role))
                .signWith(secretkey, SignatureAlgorithm.HS256)
                .setExpiration(accessValidity)
                .compact();

        // 리프레시 토큰 생성
        String refreshToken = Jwts.builder()
                .addClaims(Map.of(AUTH_MEMBER_ID, memberId))
                .addClaims(Map.of(AUTH_KEY, role))
                .signWith(secretkey, SignatureAlgorithm.HS256)
                .setExpiration(refreshValidity)
                .compact();
        refreshTokenRepository.saveRefreshToken(memberId, refreshToken);
        return new TokenDto(accessToken, refreshToken);
    }

    /*
    토큰이 유효한 지 검사
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretkey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            return false;
        } catch (UnsupportedJwtException e) {
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /*
    토큰이 만료되었는지 검사
     */
    public boolean validateExpire(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretkey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
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
        KakaoMemberDetails principal = new KakaoMemberDetails(
                null,
                Long.valueOf(claims.get(AUTH_MEMBER_ID).toString()),
                simpleGrantedAuthorities, Map.of());
        /*
        principal : 인증된 사용자 자체
        credentials : 사용자 인증을 증명하는 비밀값, jwt에서는 토큰
        authorities : 사용자가 가진 권한 목록
         */
        return new UsernamePasswordAuthenticationToken(principal, token, simpleGrantedAuthorities);
    }
}
