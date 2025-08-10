package avengers.lion.review.api;

import avengers.lion.global.config.swagger.SwaggerApiFailedResponse;
import avengers.lion.global.config.swagger.SwaggerApiResponses;
import avengers.lion.global.config.swagger.SwaggerApiSuccessResponse;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.global.response.ResponseBody;
import avengers.lion.review.dto.ReviewResponse;
import avengers.lion.review.dto.WriteReviewRequest;
import avengers.lion.review.dto.WrittenReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@Tag(name = "리뷰 API", description = "리뷰 초기 화면, 작성 가능한 리뷰 작성, 작성한 리뷰 상세 조회")
public interface ReviewApi {

    // -------------------------
    // 1) 초기 페이지 데이터 조회
    // -------------------------
    @Operation(
            summary = "리뷰 초기 페이지 데이터 조회",
            description = """
                    리뷰 페이지 최초 진입 시 필요한 데이터를 한 번에 조회합니다.<br>
                    - 작성 가능한 리뷰 개수 및 목록<br>
                    - 작성한 리뷰 개수 및 목록<br><br>
                    <b>사용 방법</b><br>
                    GET /api/v1/reviews<br>
                    Authorization: Bearer {JWT}<br>
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
                    @SwaggerApiFailedResponse(value = ExceptionType.MEMBER_NOT_FOUND, description = "존재하지 않는 사용자입니다."),
                    @SwaggerApiFailedResponse(value = ExceptionType.ACCESS_DENIED, description = "USER 권한이 필요합니다.")
            }
    )
    @PreAuthorize("hasAuthority('ROLE_USER')")
    ResponseEntity<ResponseBody<ReviewResponse>> getReviewInitialPage(
            @AuthenticationPrincipal Long memberId
    );

    // -------------------------
    // 2) 작성 가능한 리뷰 작성
    // -------------------------
    @Operation(
            summary = "작성 가능한 리뷰 작성",
            description = """
                    미작성(INACTIVE) 상태의 완료 미션에 대해 리뷰를 작성합니다.<br>
                    작성이 완료되면 해당 완료 미션의 리뷰 상태가 ACTIVE로 변경됩니다.<br><br>
                    <b>사용 방법</b><br>
                    POST /api/v1/reviews<br>
                    Authorization: Bearer {JWT}<br>
                    Content-Type: application/json
                    """,
            security = { @SecurityRequirement(name = "JWT") }
    )
    @RequestBody(
            required = true,
            content = @Content(
                    schema = @Schema(implementation = WriteReviewRequest.class),
                    examples = @ExampleObject(name = "request", value = """
                    {
                      "completedMissionId": 2,
                      "content": "정말 재밌었어요! 다음에 또 올게요 :)"
                    }
                    """)
            )
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    description = "리뷰 작성이 성공적으로 완료되었습니다."
            ),
            errors = {
                    @SwaggerApiFailedResponse(value = ExceptionType.MEMBER_NOT_FOUND, description = "존재하지 않는 사용자입니다."),
                    @SwaggerApiFailedResponse(value = ExceptionType.ACCESS_DENIED, description = "본인의 완료된 미션에 대해서만 리뷰를 작성할 수 있습니다."),
                    @SwaggerApiFailedResponse(value = ExceptionType.COMPLETED_MISSION_NOT_FOUND, description = "해당 완료 미션이 존재하지 않습니다."),
                    @SwaggerApiFailedResponse(value = ExceptionType.REVIEW_ALREADY_EXISTS, description = "이미 리뷰가 작성된 미션입니다.")
            }
    )
    @PreAuthorize("hasAuthority('ROLE_USER')")
    ResponseEntity<ResponseBody<Void>> writeUnWrittenReview(
            @AuthenticationPrincipal Long memberId,
            @Valid WriteReviewRequest writeReviewRequest
    );

    // -------------------------
    // 3) 작성한 리뷰 상세 조회 (정렬 지원)
    // -------------------------
    @Operation(
            summary = "작성한 리뷰 상세 조회",
            description = """
                    내가 작성한 리뷰 목록을 상세 조회합니다.<br>
                    정렬 파라미터로 최신순/오래된순을 제어할 수 있습니다.<br><br>
                    <b>사용 방법</b><br>
                    GET /api/v1/reviews/written?sort=DESC<br>
                    - sort: DESC(최신순, 기본값) | ASC(오래된순)
                    """,
            security = { @SecurityRequirement(name = "JWT") }
    )
    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = WrittenReviewResponse.class))))
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = WrittenReviewResponse[].class,
                    description = "작성한 리뷰 상세 조회가 성공적으로 완료되었습니다."
            ),
            errors = {
                    @SwaggerApiFailedResponse(value = ExceptionType.MEMBER_NOT_FOUND, description = "존재하지 않는 사용자입니다."),
                    @SwaggerApiFailedResponse(value = ExceptionType.ACCESS_DENIED, description = "USER 권한이 필요합니다.")
            }
    )
    @Parameter(
            name = "sort",
            description = "정렬 방향 (DESC=최신순, ASC=오래된순). 기본값: DESC",
            schema = @Schema(allowableValues = {"DESC", "ASC"}, defaultValue = "DESC")
    )
    @PreAuthorize("hasAuthority('ROLE_USER')")
    ResponseEntity<ResponseBody<List<WrittenReviewResponse>>> getWrittenReviewDetails(
            @AuthenticationPrincipal Long memberId,
            String sort
    );
}
