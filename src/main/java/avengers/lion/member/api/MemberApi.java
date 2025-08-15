package avengers.lion.member.api;

import avengers.lion.global.config.swagger.SwaggerApiFailedResponse;
import avengers.lion.global.config.swagger.SwaggerApiResponses;
import avengers.lion.global.config.swagger.SwaggerApiSuccessResponse;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.global.response.ResponseBody;
import avengers.lion.member.dto.MyProfileResponse;
import avengers.lion.member.dto.UpdateNicknameRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;

@Tag(name = "회원 API", description = "마이프로필 조회/수정 API")
public interface MemberApi {

    @Operation(
            summary = "내 프로필 조회",
            description = """
                로그인된 사용자의 프로필 정보를 조회합니다.<br>
                닉네임, 이메일, 프로필 이미지 URL, 포인트 등 개인 정보를 반환합니다.
                """,
            security = { @SecurityRequirement(name = "JWT") }
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
                            description = "접근 권한이 없습니다."
                    )
            }
    )
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/api/v1/members/my-profile")
    ResponseEntity<ResponseBody<MyProfileResponse>> getMyProfile(
            @AuthenticationPrincipal Long memberId
    );

    @Operation(
            summary = "내 프로필 수정(닉네임)",
            description = """
                로그인된 사용자의 닉네임을 수정합니다.<br>
                닉네임 중복 여부를 검사하며, 성공 시 본문 없이 성공 응답을 반환합니다.
                """,
            security = { @SecurityRequirement(name = "JWT") }
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = Void.class,
                    description = "닉네임이 성공적으로 변경되었습니다."
            ),
            errors = {
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.NICKNAME_ALREADY_EXISTS,
                            description = "이미 사용 중인 닉네임입니다."
                    ),
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.MEMBER_NOT_FOUND,
                            description = "존재하지 않는 사용자입니다."
                    ),
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.ACCESS_DENIED,
                            description = "접근 권한이 없습니다."
                    )
            }
    )
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PutMapping("/api/v1/members/my-profile")
    ResponseEntity<ResponseBody<Void>> updateMyProfile(
            @AuthenticationPrincipal Long memberId,
            @RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UpdateNicknameRequest.class),
                            examples = @ExampleObject(
                                    name = "닉네임 변경 예시",
                                    value = """
                                            { "nickname": "letsgu_user01" }
                                            """
                            )
                    )
            )
            UpdateNicknameRequest updateNicknameRequest
    );
}
