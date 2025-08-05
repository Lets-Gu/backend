package avengers.lion.review.controller;

import avengers.lion.auth.domain.KakaoMemberDetails;
import avengers.lion.global.response.ResponseBody;
import avengers.lion.global.response.ResponseUtil;
import avengers.lion.review.dto.ReviewDto;
import avengers.lion.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    /*
    마이페이지 내에서 나의 발자취 버튼 클릭 -> 리뷰 전체보기
     */
    @GetMapping
    public ResponseEntity<ResponseBody<List<ReviewDto>>> getAllReviews(@AuthenticationPrincipal KakaoMemberDetails kakaoMemberDetails){
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(reviewService.getAllReviews(kakaoMemberDetails.getMemberId())));
    }
}
