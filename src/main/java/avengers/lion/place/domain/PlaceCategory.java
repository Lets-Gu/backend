package avengers.lion.place.domain;

import lombok.Getter;

@Getter
public enum PlaceCategory {

    RESTAURANT("음식점");

    private final String name;

    PlaceCategory(String name){
        this.name=name;
    }
}
