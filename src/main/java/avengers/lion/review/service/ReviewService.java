package avengers.lion.review.service;

import avengers.lion.global.exception.BusinessException;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.mission.domain.CompletedMission;
import avengers.lion.mission.domain.ReviewStatus;
import avengers.lion.mission.repository.CompletedMissionRepository;
import avengers.lion.review.domain.Review;
import avengers.lion.review.dto.ReviewResponse;
import avengers.lion.review.dto.UnWrittenReviewResponse;
import avengers.lion.review.dto.WriteReviewRequest;
import avengers.lion.review.dto.WrittenReviewResponse;
import avengers.lion.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CompletedMissionRepository completedMissionRepository;

    /*
    리뷰 초기 페이지
     */
    public ReviewResponse getReviewInitialPage(Long memberId){
        Long unWrittenCount = completedMissionRepository.countByMemberIdAndReviewStatus(memberId, ReviewStatus.INACTIVE);
        Long writtenCount = reviewRepository.countByMemberId(memberId);
        List<UnWrittenReviewResponse> unWrittenReviewResponse = getUnWrittenReview(memberId);
        List<WrittenReviewResponse> writtenReviewResponse = getAllReviews(memberId);
        return new ReviewResponse(unWrittenCount, writtenCount, unWrittenReviewResponse, writtenReviewResponse);
    }


    /*
    작성 가능한 리뷰 조회
     */
    public List<UnWrittenReviewResponse> getUnWrittenReview(Long memberId){
        List<CompletedMission> unWrittenReviews = completedMissionRepository.getUnwrittenReviewsByMemberId(memberId);
        return UnWrittenReviewResponse.of(unWrittenReviews);
    }

    /*
    작성 가능한 리뷰 작성하기
     */
    @Transactional
    public void writeUnWrittenReview(Long memberId, WriteReviewRequest writeReviewRequest){
        Long completedMissionId = writeReviewRequest.completedMissionId();
        CompletedMission completedMission = completedMissionRepository.findById(completedMissionId)
                        .orElseThrow(()-> new BusinessException(ExceptionType.COMPLETED_MISSION_NOT_FOUND));
        
        // 권한 검증: 본인의 CompletedMission인지 확인
        if (!completedMission.getMember().getId().equals(memberId)) {
            throw new BusinessException(ExceptionType.ACCESS_DENIED);
        }
        
        // 중복 리뷰 작성 방지
        if (completedMission.getReviewStatus() == ReviewStatus.ACTIVE) {
            throw new BusinessException(ExceptionType.REVIEW_ALREADY_EXISTS);
        }

        Review review = Review.builder()
                .content(writeReviewRequest.content())
                .imageUrl(completedMission.getImageUrl())
                .member(completedMission.getMember())
                .completedMission(completedMission)
                .build();
        reviewRepository.save(review);
        
        completedMission.updateReviewStatus(ReviewStatus.ACTIVE);
    }

    /*
    리뷰 전체조회
     */
    public List<WrittenReviewResponse> getAllReviews(Long memberId){
        Sort defaultSort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Review> reviews = reviewRepository.findAllByMemberId(memberId, defaultSort);
        return WrittenReviewResponse.of(reviews);
    }

    /*
    작성한 리뷰 상세조회
     */
    public List<WrittenReviewResponse> getWrittenReviewDetails(Long memberId, String sortType){
        Sort sort = "DESC".equals(sortType)
            ? Sort.by(Sort.Direction.DESC, "createdAt")
            : Sort.by(Sort.Direction.ASC, "createdAt");
        
        List<Review> reviews = reviewRepository.findAllByMemberId(memberId, sort);
        return WrittenReviewResponse.of(reviews);
    }
}
