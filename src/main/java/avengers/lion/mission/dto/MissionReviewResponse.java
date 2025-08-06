package avengers.lion.mission.dto;

import avengers.lion.review.domain.Review;


import java.time.LocalDateTime;

public record MissionReviewResponse(Long reviewId, String memberName, String reviewContent, String reviewImageUrl, LocalDateTime reviewDate) {

    public static MissionReviewResponse from(Review review){
        return new MissionReviewResponse(
                review.getReviewId(),
                review.getMember().getNickname(),
                review.getContent(),
                review.getImageUrl(),
                review.getCreatedAt()
        );
    }
}
