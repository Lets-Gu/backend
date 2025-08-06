package avengers.lion.review.dto;

import avengers.lion.mission.domain.CompletedMission;

public record UnWrittenReviewResponse(Long completedMissionId, String placeName, String address, String imageUrl) {

    public static UnWrittenReviewResponse from(CompletedMission completedMission){
        return new UnWrittenReviewResponse(
                completedMission.getId(),
                completedMission.getMission().getPlaceName(),
                completedMission.getMission().getAddress(),
                completedMission.getImageUrl()
        );
    }
}
