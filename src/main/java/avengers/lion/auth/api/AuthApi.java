package avengers.lion.auth.api;

import avengers.lion.auth.dto.RegisterRequest;
import avengers.lion.auth.dto.LoginRequest;
import avengers.lion.auth.dto.LoginResponse;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.global.response.ResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import avengers.lion.global.config.swagger.SwaggerApiFailedResponse;
import avengers.lion.global.config.swagger.SwaggerApiResponses;
import avengers.lion.global.config.swagger.SwaggerApiSuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;

@Tag(name = "인증 API", description = "회원가입 및 로그인 관련 API")
@Validated
public interface AuthApi {

    @Operation(
            summary = "회원가입",
            description = """
            이메일, 닉네임, 비밀번호, 프로필 이미지 URL을 제공하여 새로운 사용자를 등록합니다.
            이메일 중복 시 예외가 발생합니다.
            """
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    description = "회원가입이 성공적으로 완료되었습니다."
            ),
            errors = {
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.EMAIL_ALREADY_EXISTS,
                            description = "이미 사용 중인 이메일입니다."
                    )
            }
    )
    @PostMapping("/api/v1/auth/signup")
    ResponseEntity<ResponseBody<Void>> signUp(
            @Valid @org.springframework.web.bind.annotation.RequestBody RegisterRequest request
    );

    @Operation(
            summary = "로그인",
            description = """
            이메일과 비밀번호로 인증을 수행하고, 성공 시 Authorization 헤더에 Bearer 토큰을 설정하여 반환합니다.
            로그인 성공 시 사용자 정보(LoginResponse)와 발급된 토큰을 함께 제공합니다.
            """
    )
    @ApiResponse(content = @Content(schema = @Schema(implementation = LoginResponse.class)),
                 headers ={
                    @io.swagger.v3.oas.annotations.headers.Header(
                            name = "Authorization",
                            description = "로그인 성공 시 발급되는 JWT 액세스 토큰 (Bearer {token} 형식)",
                            schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                    )
                 })
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = LoginResponse.class,
                    description = "로그인에 성공했습니다. 응답 본문에 사용자 정보를, `Authorization` 헤더에 Bearer JWT를 포함합니다."
            ),
            errors = {
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.MEMBER_NOT_FOUND,
                            description = "존재하지 않는 사용자입니다."
                    ),
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.INVALID_PASSWORD,
                            description = "비밀번호가 올바르지 않습니다."
                    )
            }
    )
    @PostMapping("/api/v1/auth/login")
    ResponseEntity<ResponseBody<LoginResponse>> login(
            @Valid @org.springframework.web.bind.annotation.RequestBody LoginRequest request,
            HttpServletResponse response
    );
}
