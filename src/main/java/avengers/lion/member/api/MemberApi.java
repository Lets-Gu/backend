package avengers.lion.member.api;

import avengers.lion.global.exception.ExceptionType;
import avengers.lion.global.response.ResponseBody;
import avengers.lion.member.dto.MyProfileResponse;
import avengers.lion.wallet.dto.ConsumedItemResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import avengers.lion.global.config.swagger.SwaggerApiFailedResponse;
import avengers.lion.global.config.swagger.SwaggerApiResponses;
import avengers.lion.global.config.swagger.SwaggerApiSuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "회원 API", description = "회원 정보 조회 API")
public interface MemberApi {

    @Operation(
            summary = "내 프로필 조회",
            description = """
            로그인된 사용자의 프로필 정보를 조회합니다.<br>
            닉네임, 이메일, 프로필 이미지 URL 등 개인 정보를 반환합니다.
            """, security = { @SecurityRequirement(name = "JWT") }
    )
    @ApiResponse(content = @Content(schema = @Schema(implementation = MyProfileResponse.class)))
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = MyProfileResponse.class,
                    description = "내 프로필 조회가 성공적으로 완료되었습니다."
            ),
            errors = {
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.MEMBER_NOT_FOUND,
                            description = "존재하지 않는 사용자입니다."
                    ),
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.ACCESS_DENIED,
                            description = "USER 권한이 필요합니다."
                    )
            }
    )
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/api/v1/members/my-profile")
    ResponseEntity<ResponseBody<MyProfileResponse>> getMyProfile(
            @AuthenticationPrincipal Long memberId
    );
}
