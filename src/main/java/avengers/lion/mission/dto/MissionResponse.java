package avengers.lion.mission.dto;

import avengers.lion.mission.domain.Mission;
import avengers.lion.place.domain.PlaceCategory;


public record MissionResponse(Long missionId, String placeName, String title, String description, Double latitude, Double longitude, PlaceCategory placeCategory) {

    public static MissionResponse from(Mission mission){
        return new MissionResponse(
                mission.getId(),
                mission.getPlaceName(),
                mission.getTitle(),
                mission.getDescription(),
                mission.getLatitude(),
                mission.getLongitude(),
                mission.getPlaceCategory()
        );
    };
}
