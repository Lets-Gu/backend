package avengers.lion.review.controller;


import avengers.lion.review.api.ReviewApi;
import avengers.lion.review.dto.ReviewResponse;
import avengers.lion.review.dto.WrittenReviewResponse;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import avengers.lion.global.response.ResponseBody;
import avengers.lion.global.response.ResponseUtil;

import avengers.lion.review.dto.WriteReviewRequest;
import avengers.lion.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController implements ReviewApi {

    private final ReviewService reviewService;


    /*
    리뷰페이지 데이터 ㅈ회
     */
    @GetMapping
    public ResponseEntity<ResponseBody<ReviewResponse>> getReviewInitialPage(@AuthenticationPrincipal Long memberId){
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(reviewService.getReviewInitialPage(memberId)));
    }

    /*
    작성 가능한 리뷰 리뷰 작성하기
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ResponseBody<Void>> writeUnWrittenReview(@AuthenticationPrincipal Long memberId, @Valid @RequestBody WriteReviewRequest writeReviewRequest){
        reviewService.writeUnWrittenReview(memberId, writeReviewRequest);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse());
    }

    /*
    작성한 리뷰 상세조회
     */
    @GetMapping("/written")
    public ResponseEntity<ResponseBody<List<WrittenReviewResponse>>> getWrittenReviewDetails(
            @AuthenticationPrincipal Long memberId,
            @RequestParam(value = "sort", defaultValue = "DESC") String sortType){
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(reviewService.getWrittenReviewDetails(memberId, sortType)));
    }


}
