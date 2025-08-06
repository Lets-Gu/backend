package avengers.lion.review.controller;

import avengers.lion.auth.domain.KakaoMemberDetails;
import avengers.lion.global.response.ResponseBody;
import avengers.lion.global.response.ResponseUtil;
import avengers.lion.review.dto.ReviewDto;
import avengers.lion.review.dto.UnWrittenReviewResponse;
import avengers.lion.review.dto.WriteReviewRequest;
import avengers.lion.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    /*
    작성 가능한 리뷰 조회하기
     */
    @GetMapping("/not-written")
    public ResponseEntity<ResponseBody<List<UnWrittenReviewResponse>>> getUnWrittenReview(@AuthenticationPrincipal KakaoMemberDetails kakaoMemberDetails){
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(reviewService.getUnWrittenReview(kakaoMemberDetails.getMemberId())));
    }

    /*
    작성 가능한 리뷰 리뷰 작성하기
     */
    @PostMapping
    public ResponseEntity<ResponseBody<Void>> writeUnWrittenReview(@AuthenticationPrincipal KakaoMemberDetails kakaoMemberDetails, @Valid @RequestBody WriteReviewRequest writeReviewRequest){
        reviewService.writeUnWrittenReview(kakaoMemberDetails.getMemberId(), writeReviewRequest);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse());
    }

    /*
    내가 작성한 리뷰
     */
    @GetMapping
    public ResponseEntity<ResponseBody<List<ReviewDto>>> getAllReviews(@AuthenticationPrincipal KakaoMemberDetails kakaoMemberDetails){
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(reviewService.getAllReviews(kakaoMemberDetails.getMemberId())));
    }
}
