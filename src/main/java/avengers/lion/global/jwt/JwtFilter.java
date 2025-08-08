package avengers.lion.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException{

        String accessToken = null;
        String authHeader = getTokenFromHeader(request, AUTHORIZATION_HEADER);
        
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            accessToken = authHeader.substring(BEARER_PREFIX.length());
        }
        

        if(accessToken != null && tokenProvider.validateToken(accessToken)){
            SecurityContextHolder.getContext().setAuthentication(tokenProvider.getAuthentication(accessToken));
        } else {

        }

        filterChain.doFilter(request, response);
    }

    /*
    요청 헤더에서 값 추출
     */
    public String getTokenFromHeader(HttpServletRequest request, String headerName){
        return request.getHeader(headerName);
    }

}
