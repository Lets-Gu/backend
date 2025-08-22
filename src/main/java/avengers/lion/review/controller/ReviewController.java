package avengers.lion.review.controller;


import avengers.lion.global.base.PageResult;
import avengers.lion.global.response.ResponseBody;
import avengers.lion.global.response.ResponseUtil;
import avengers.lion.review.api.ReviewApi;
import avengers.lion.review.domain.SortType;
import avengers.lion.review.dto.*;
import avengers.lion.review.service.ReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController implements ReviewApi {

    private final ReviewService reviewService;

    /** 오버뷰: 카운트 + 프리뷰(각 previewLimit개) + 각 next 커서 */
    @GetMapping("/overview")
    public ResponseEntity<ResponseBody<OverviewResponse>> getOverview(
            @AuthenticationPrincipal Long memberId,
            @RequestParam(defaultValue = "6") @Min(1) @Max(50) int previewLimit) {
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(reviewService.getOverview(memberId, previewLimit)));
    }

    /** 작성 가능한 리뷰 → 리뷰 작성 */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ResponseBody<Void>> writeUnWrittenReview(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody WriteReviewRequest writeReviewRequest) {
        reviewService.writeUnWrittenReview(memberId, writeReviewRequest);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse());
    }

    /** 작성 가능한 리뷰: 무한 스크롤 (ASC/DESC) */
    @GetMapping("/unwritten/page")
    public ResponseEntity<ResponseBody<PageResult<UnWrittenReviewResponse>>> getUnwrittenPage(
            @AuthenticationPrincipal Long memberId,
            @RequestParam("cursorId") Long cursorId,
            @RequestParam(defaultValue = "DESC") SortType sort,
            @RequestParam(defaultValue = "4") @Min(1) @Max(100) int limit
    ) {
        log.info("Controller: cursorId={}, sort={}, limit={}", cursorId, sort, limit);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(reviewService.getUnwrittenPage(memberId, cursorId, limit, sort)));
    }

    /** 작성한 리뷰 전체조회: 최신순만 (커서 쌍) */
    @GetMapping("/written/page")
    public ResponseEntity<ResponseBody<PageResult<WrittenReviewResponse>>> getWrittenPage(
            @AuthenticationPrincipal Long memberId,
            @RequestParam("cursorId") Long cursorId,
            @RequestParam(defaultValue = "4") @Min(1) @Max(100) int limit
    ) {
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(reviewService.getWrittenPage(memberId, cursorId, limit)));
    }
}
