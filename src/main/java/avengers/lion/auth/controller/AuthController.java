package avengers.lion.auth.controller;

import avengers.lion.auth.api.AuthApi;
import avengers.lion.auth.dto.LoginRequest;
import avengers.lion.auth.dto.LoginResponse;
import avengers.lion.auth.dto.LoginWithTokenResponse;
import avengers.lion.auth.dto.RegisterRequest;
import avengers.lion.auth.service.AuthService;
import avengers.lion.global.response.ResponseBody;
import avengers.lion.global.response.ResponseUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseBody<Void>> signUp(@Valid @RequestBody RegisterRequest request) {
        authService.signUp(request);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse());
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseBody<LoginResponse>> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        // 비즈니스 로직은 서비스에서 처리
        LoginWithTokenResponse result = authService.loginWithToken(request);
        // 토큰을 표준 Authorization 헤더에 설정
        response.setHeader("Access-Token", result.accessToken());
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(result.loginResponse()));
    }
}