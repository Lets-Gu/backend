package avengers.lion.place.domain;

import lombok.Getter;

@Getter
public enum PlaceCategory {

    CULTURE_HISTORY("문화/역사"),
    NATURE_PARK("자연/공원"),
    FOOD_CAFE("음식/카페"),
    ART_EXHIBITION_EXPERIENCE("예술/전시/체험"),
    LIFE_CONVENIENCE("생활/편의시설");

    private final String name;

    PlaceCategory(String name){
        this.name=name;
    }
}
