package avengers.lion.review.api;

import avengers.lion.global.config.swagger.SwaggerApiFailedResponse;
import avengers.lion.global.config.swagger.SwaggerApiResponses;
import avengers.lion.global.config.swagger.SwaggerApiSuccessResponse;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.global.response.ResponseBody;
import avengers.lion.review.dto.ReviewDto;
import avengers.lion.review.dto.UnWrittenReviewResponse;
import avengers.lion.review.dto.WriteReviewRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "리뷰 API", description = "리뷰 관련 API")
public interface ReviewApi {

    @Operation(
            summary = "작성 가능한 리뷰 조회",
            description = """
                    사용자가 완료한 미션 중 아직 리뷰를 작성하지 않은 미션들의 목록을 조회합니다.<br>
                    완료된 미션(CompletedMission) 중 리뷰 상태가 INACTIVE인 미션들만 반환됩니다.
                    """
    )
    @ApiResponse(content = @Content(schema = @Schema(implementation = UnWrittenReviewResponse.UnWrittenReviewsResponse.class)))
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = UnWrittenReviewResponse.UnWrittenReviewsResponse.class,
                    description = "작성 가능한 리뷰 목록 조회가 성공적으로 완료되었습니다."
            ),
            errors = {
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.MEMBER_NOT_FOUND,
                            description = "존재하지 않는 사용자입니다."
                    )
            }
    )
    ResponseEntity<ResponseBody<UnWrittenReviewResponse.UnWrittenReviewsResponse>> getUnWrittenReview(@AuthenticationPrincipal Long memberId);

    @Operation(
            summary = "리뷰 작성",
            description = """
                    완료된 미션에 대한 리뷰를 작성합니다.<br>
                    본인이 완료한 미션에 대해서만 리뷰 작성이 가능하며, 이미 리뷰가 작성된 미션에는 중복 작성할 수 없습니다.
                    """
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    description = "리뷰 작성이 성공적으로 완료되었습니다."
            ),
            errors = {
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.COMPLETED_MISSION_NOT_FOUND,
                            description = "존재하지 않는 완료된 미션입니다."
                    ),
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.ACCESS_DENIED,
                            description = "본인의 완료된 미션에 대해서만 리뷰를 작성할 수 있습니다."
                    ),
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.REVIEW_ALREADY_EXISTS,
                            description = "이미 작성된 리뷰입니다"
                    ),
            }
    )
    ResponseEntity<ResponseBody<Void>> writeUnWrittenReview(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody WriteReviewRequest writeReviewRequest
    );

    @Operation(
            summary = "내가 작성한 리뷰 조회",
            description = """
                    사용자가 작성한 모든 리뷰 목록을 조회합니다.<br>
                    작성 날짜 순으로 정렬되어 반환됩니다.
                    """
    )
    @ApiResponse(content = @Content(schema = @Schema(implementation = ReviewDto.ReviewsDto.class)))
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = ReviewDto.ReviewsDto.class,
                    description = "내가 작성한 리뷰 조회가 성공적으로 완료되었습니다."
            ),
            errors = {
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.MEMBER_NOT_FOUND,
                            description = "존재하지 않는 사용자입니다."
                    )
            }
    )

    ResponseEntity<ResponseBody<ReviewDto.ReviewsDto>> getAllReviews(@AuthenticationPrincipal Long memberId);
}