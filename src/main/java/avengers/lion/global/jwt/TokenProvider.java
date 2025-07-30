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
    private long accessTokenValidityMilliSeconds;
    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValidityMilliSeconds;

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

        Date accessValidity = new Date(now + this.accessTokenValidityMilliSeconds);
        Date refreshValidity = new Date(now + this.refreshTokenValidityMilliSeconds);

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
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    // token으로부터 Authentication 객체를 만들어 리턴하는 메소드
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        List<String> authorities = Arrays.asList(claims.get(AUTH_KEY)
                .toString()
                .split(","));

        GrantedAuthority authority = new SimpleGrantedAuthority(claims.get(AUTH_KEY).toString());
        List<GrantedAuthority> simpleGrantedAuthorities = Collections.singletonList(authority);

        KakaoMemberDetails principal = new KakaoMemberDetails(
                (String) claims.get(AUTH_EMAIL),
                simpleGrantedAuthorities, Map.of());

        return new UsernamePasswordAuthenticationToken(principal, token, simpleGrantedAuthorities);
    }
}
