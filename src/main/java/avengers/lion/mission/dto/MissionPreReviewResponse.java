package avengers.lion.mission.dto;


import java.util.List;

public record MissionPreReviewResponse(
        Long count,
        List<MissionReviewResponse> missionReviewResponse,
        PageMeta reviewPage) {
    public record PageMeta(Boolean hasNext, Long nextId){};
}
