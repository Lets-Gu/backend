package avengers.lion.mission.dto;

import avengers.lion.mission.domain.Mission;
import avengers.lion.place.domain.PlaceCategory;


public record MissionResponse(Long missionId, String placeName,  String description, Double latitude, Double longitude, PlaceCategory placeCategory, Boolean isCompleted) {

    public static MissionResponse of(Mission mission, Boolean isCompleted){
        return new MissionResponse(
                mission.getId(),
                mission.getPlaceName(),
                mission.getDescription(),
                mission.getLatitude(),
                mission.getLongitude(),
                mission.getPlaceCategory(),
                isCompleted
        );
    };
}
