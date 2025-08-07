package avengers.lion.review.dto;

import avengers.lion.mission.domain.CompletedMission;
import java.util.List;

public record UnWrittenReviewResponse(Long completedMissionId, String placeName, String address, String imageUrl) {

    public static UnWrittenReviewsResponse of(List<CompletedMission> unWrittenReviews) {
        List<UnWrittenReviewResponse> reviews = unWrittenReviews.stream()
                .map(completedMission -> new UnWrittenReviewResponse(
                        completedMission.getId(),
                        completedMission.getMission().getPlaceName(),
                        completedMission.getMission().getAddress(),
                        completedMission.getImageUrl()
                ))
                .toList();
        return new UnWrittenReviewsResponse(reviews, reviews.size());
    }

    public record UnWrittenReviewsResponse(List<UnWrittenReviewResponse> reviews, int reviewCount) { }


}
