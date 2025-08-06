package avengers.lion.mission.dto;

import avengers.lion.mission.domain.Mission;

import java.math.BigDecimal;

public record MissionResponse(Long missionId, String placeName, String title, String description, BigDecimal latitude, BigDecimal longitude) {

    public static MissionResponse from(Mission mission){
        return new MissionResponse(
                mission.getMissionId(),
                mission.getPlaceName(),
                mission.getTitle(),
                mission.getDescription(),
                mission.getLatitude(),
                mission.getLongitude());
    }
}
