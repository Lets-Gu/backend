package avengers.lion.global.config.security;

import avengers.lion.auth.service.KakaoMemberDetailsService;
import avengers.lion.global.OAuth2SuccessHandler;
import avengers.lion.global.jwt.JwtAccessDeniedHandler;
import avengers.lion.global.jwt.JwtAuthenticationFailEntryPoint;
import avengers.lion.global.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final KakaoMemberDetailsService KakaoMemberDetailsService;
    private final JwtFilter jwtFilter;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationFailEntryPoint jwtAuthenticationFailEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, OAuth2SuccessHandler oAuth2SuccessHandler) throws Exception {
        http
                // CSRF 비활성화
                .csrf(csrf -> csrf.disable())

                // 세션 사용 x
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // URL 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 로그인, 콜백, 퍼블릭 API 허용
                        .requestMatchers("/", "/oauth2/**", "/login", "/error").permitAll()
                        .anyRequest().authenticated()
                )
                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authz -> authz
                                .baseUri("/oauth2/authorize"))
                        .redirectionEndpoint(redir -> redir
                                .baseUri("/oauth2/callback/*"))
                        .userInfoEndpoint(ui -> ui
                                .userService(KakaoMemberDetailsService))
                        // 카카오 로그인에 성공하면, 실행
                        .successHandler(oAuth2SuccessHandler)
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                ).addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                // JWT 예외 처리 핸들러 설정
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                        .authenticationEntryPoint(jwtAuthenticationFailEntryPoint)
                );

        return http.build();
    }
}