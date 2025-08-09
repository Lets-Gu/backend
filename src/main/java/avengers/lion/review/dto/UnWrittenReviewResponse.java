package avengers.lion.review.dto;

import avengers.lion.mission.domain.CompletedMission;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "작성 가능한 리뷰 항목")
public record UnWrittenReviewResponse(
        @Schema(description = "완료 미션 ID", example = "2")
        Long completedMissionId,
        @Schema(description = "장소명", example = "갈뫼루")
        String placeName,
        @Schema(description = "주소", example = "경상북도 구미시 신평동 69-21")
        String address,
        @Schema(description = "이미지 URL", example = "https://example.com/images/mission6.jpg")
        String imageUrl
) {
    public static List<UnWrittenReviewResponse> of(List<CompletedMission> unWrittenReviews) {
        return unWrittenReviews.stream()
                .map(completedMission -> new UnWrittenReviewResponse(
                        completedMission.getId(),
                        completedMission.getMission().getPlaceName(),
                        completedMission.getMission().getAddress(),
                        completedMission.getImageUrl()
                ))
                .toList();
    }
}
