package avengers.lion.review.dto;

import avengers.lion.review.domain.Review;

import java.util.List;

public record  ReviewDto(Long reviewId, String content, String imageUrl, String placeName, String address) {

    public static ReviewsDto of(List<Review> reviews) {
        List<ReviewDto> reviewsDtoList = reviews.stream()
                .map(review-> new ReviewDto(
                        review.getId(),
                        review.getContent(),
                        review.getImageUrl(),
                        review.getCompletedMission().getMission().getPlaceName(),
                        review.getCompletedMission().getMission().getAddress()
                        ))
                .toList();
        return new ReviewsDto(reviewsDtoList, reviewsDtoList.size());

    }
    public record ReviewsDto (List<ReviewDto> reviews, int reviewCount) {}
}
