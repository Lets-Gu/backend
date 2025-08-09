package avengers.lion.review.api;

import avengers.lion.global.config.swagger.SwaggerApiFailedResponse;
import avengers.lion.global.config.swagger.SwaggerApiResponses;
import avengers.lion.global.config.swagger.SwaggerApiSuccessResponse;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.global.response.ResponseBody;
import avengers.lion.review.dto.ReviewResponse;
import avengers.lion.review.dto.WriteReviewRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Tag(name = "리뷰 API", description = "리뷰 초기 화면, 작성 가능한 리뷰 작성 API")
public interface ReviewApi {

    @Operation(
            summary = "리뷰 초기 페이지 데이터 조회",
            description = """
                    리뷰 페이지 최초 진입 시 필요한 데이터를 한 번에 조회합니다.<br>
                    - 작성 가능한 리뷰 개수 및 목록<br>
                    - 작성한 리뷰 개수 및 목록
                    """,
            security = { @SecurityRequirement(name = "JWT") }
    )
    @ApiResponse(content = @Content(schema = @Schema(implementation = ReviewResponse.class)))
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = ReviewResponse.class,
                    description = "리뷰 초기 페이지 데이터 조회가 성공적으로 완료되었습니다."
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
    ResponseEntity<ResponseBody<ReviewResponse>> getReviewInitialPage(
            @AuthenticationPrincipal Long memberId
    );

    @Operation(
            summary = "작성 가능한 리뷰 작성",
            description = """
                    미작성(INACTIVE) 상태의 완료 미션에 대해 리뷰를 작성합니다.<br>
                    작성이 완료되면 해당 완료 미션의 리뷰 상태가 ACTIVE로 변경됩니다.
                    """,
            security = { @SecurityRequirement(name = "JWT") }
    )
    @ApiResponse(content = @Content(schema = @Schema(implementation = Void.class)))
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = Void.class,
                    description = "리뷰 작성이 성공적으로 완료되었습니다."
            ),
            errors = {
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.MEMBER_NOT_FOUND,
                            description = "존재하지 않는 사용자입니다."
                    ),
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.ACCESS_DENIED,
                            description = "본인의 완료된 미션에 대해서만 리뷰를 작성할 수 있습니다."
                    ),
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.COMPLETED_MISSION_NOT_FOUND,
                            description = "해당 완료 미션이 존재하지 않습니다."
                    ),
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.REVIEW_ALREADY_EXISTS,
                            description = "이미 리뷰가 작성된 미션입니다."
                    )
            }
    )
    @PreAuthorize("hasAuthority('ROLE_USER')")
    ResponseEntity<ResponseBody<Void>> writeUnWrittenReview(
            @AuthenticationPrincipal Long memberId,
            @Valid WriteReviewRequest writeReviewRequest
    );
}
