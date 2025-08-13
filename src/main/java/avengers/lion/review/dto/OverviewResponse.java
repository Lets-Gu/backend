package avengers.lion.review.dto;

import avengers.lion.global.base.PageMeta;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "OverviewResponse", description = "프리뷰: 작성 가능한리뷰, 작성한 리뷰 개수 + 각 탭 프리뷰 데이")
public record OverviewResponse(
        @Schema(description = "작성 가능한 리뷰 총 개수", example = "8")
        Long unWrittenReviewCount,

        @Schema(description = "작성한 리뷰 총 개수", example = "2")
        Long writtenReviewCount,

        @Schema(description = "작성 가능한 리뷰 프리뷰 목록(초기 진입에서 previewLimit개)")
        List<UnWrittenReviewResponse> unwritten,

        @Schema(description = "작성 가능한 리뷰 프리뷰의 다음 페이지 메타정보")
        PageMeta unwrittenPage,

        @Schema(description = "작성한 리뷰 프리뷰 목록(초기 진입에서 previewLimit개)")
        List<WrittenReviewResponse> written,

        @Schema(description = "작성한 리뷰 프리뷰의 다음 페이지 메타")
        PageMeta writtenPage
) {

}
