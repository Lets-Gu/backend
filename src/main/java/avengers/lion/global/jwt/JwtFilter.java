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

    private static final String ACCESS_HEADER = "Authorization";

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException{

        String accessToken = getTokenFromHeader(request, ACCESS_HEADER);

        if(tokenProvider.validateToken(accessToken) && tokenProvider.validateExpire(accessToken)){
            SecurityContextHolder.getContext().setAuthentication(tokenProvider.getAuthentication(accessToken));
        }

        filterChain.doFilter(request, response);
    }

    /*
    Authorization 헤더에서 토큰 추출
     */
    public String getTokenFromHeader(HttpServletRequest request, String headerName){
        String header = request.getHeader(headerName);
        if(header == null || !header.startsWith("Bearer ")){
            return null;
        }
        return header.substring(7);
    }

}
