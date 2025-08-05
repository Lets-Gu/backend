package avengers.lion.review.service;

import avengers.lion.review.domain.Review;
import avengers.lion.review.dto.ReviewDto;
import avengers.lion.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    /*
    리뷰 전체조회
     */
    public List<ReviewDto> getAllReviews(Long memberId){
        List<Review> reviewDtos= reviewRepository.findAllByMemberMemberId(memberId);

        return reviewDtos.stream()
                .map(ReviewDto::from)
                .toList();
    }
}
