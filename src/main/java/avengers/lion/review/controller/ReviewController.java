package avengers.lion.review.controller;


import avengers.lion.review.api.ReviewApi;
import avengers.lion.review.dto.ReviewResponse;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class  ReviewController implements ReviewApi {

    private final ReviewService reviewService;


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

}
