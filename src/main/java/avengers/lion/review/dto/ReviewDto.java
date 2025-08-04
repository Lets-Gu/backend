package avengers.lion.review.dto;

import avengers.lion.review.domain.Review;

public record ReviewDto(Long reviewId, String title, String content, String imageUrl) {

    public static ReviewDto from(Review review){
        return new ReviewDto(review.getReviewId(), review.getTitle(), review.getContent(), review.getImageUrl());
    }
}
