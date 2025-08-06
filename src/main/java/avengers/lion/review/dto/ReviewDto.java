package avengers.lion.review.dto;

import avengers.lion.review.domain.Review;

public record  ReviewDto(Long reviewId, String content, String imageUrl, String placeName, String address) {

    public static ReviewDto from(Review review){
        return new ReviewDto(
                review.getId(),
                review.getContent(),
                review.getImageUrl(),
                review.getMission().getPlaceName(),
                review.getMission().getAddress());
    };
}
