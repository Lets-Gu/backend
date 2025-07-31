package avengers.lion.place.domain;

import lombok.Getter;

@Getter
public enum Season {
    SPRING("봄"),
    SUMMER("여름"),
    FALL("가을"),
    WINTER("겨울");

    Season(String name){
        this.name=name;
    }

    private final String name;
}
