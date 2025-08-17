package avengers.lion.global.jwt;

import avengers.lion.global.exception.BusinessException;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.global.response.ResponseBody;
import avengers.lion.global.response.ResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class TokenExceptionHandlerFilter extends OncePerRequestFilter { // OncePerRequestFilter : 한 요청당 필터가 딱 한 번만 실행되도록 보장하는 추상 클래스

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            filterChain.doFilter(request, response);

        }catch(BusinessException e){
            handleBusinessException(request,response,e);
        }

    }
    private void handleBusinessException(HttpServletRequest request, HttpServletResponse response, BusinessException e) throws IOException {
        ExceptionType exceptionType = e.getExceptionType();
        response.setStatus(exceptionType.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8"); // HttpServletResponse: ISO-8859-1 인코딩 사용하기 때문에 한글 출력을 위해 UTF-8 설정
        ResponseBody<Void> body= ResponseUtil.createFailureResponse(exceptionType);
        writeErrorResponse(response,body);
    }
    private void writeErrorResponse(HttpServletResponse response, ResponseBody<Void> body) throws IOException{
        ObjectMapper objectMapper = new ObjectMapper(); // Jackson ObjectMapper 인스턴스 생성
        String json = objectMapper.writeValueAsString(body);  // ResponseBody 객체를 JSON 문자열로 직렬화

        try (PrintWriter writer = response.getWriter()) { // PrintWriter 자원을 안전하게 닫기 위해 try-with-resource 사용
            writer.write(json);
            writer.flush();
        }
    }
}