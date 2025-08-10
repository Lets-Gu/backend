package avengers.lion.review.dto;

import avengers.lion.review.domain.Review;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "작성한 리뷰 항목")
public record WrittenReviewResponse(
        @Schema(description = "리뷰 ID", example = "1")
        Long reviewId,
        @Schema(description = "리뷰 내용", example = "우와 너무 이뻐요!")
        String content,
        @Schema(description = "이미지 URL", example = "https://example.com/images/mission9.jpg")
        String imageUrl,
        @Schema(description = "장소명", example = "곤충체험연구소")
        String placeName,
        @Schema(description = "주소", example = "경상북도 구미시 산동읍 신당1로1길 17-3")
        String address,
        @Schema(description = "작성일", example = "2023-03-03T12:00:00")
        LocalDateTime createdAt
) {
    public static List<WrittenReviewResponse> of(List<Review> reviews) {
        return reviews.stream()
                .map(review -> new WrittenReviewResponse(
                        review.getId(),
                        review.getContent(),
                        review.getImageUrl(),
                        review.getCompletedMission().getMission().getPlaceName(),
                        review.getCompletedMission().getMission().getAddress(),
                        review.getCreatedAt()
                ))
                .toList();
    }
}
