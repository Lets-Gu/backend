package avengers.lion.mission.dto.response;


import avengers.lion.global.base.PageMeta;

import java.util.List;

public record MissionPreReviewResponse(
        Long count,
        List<MissionReviewResponse> missionReviewResponse,
        PageMeta reviewPage) {
}
