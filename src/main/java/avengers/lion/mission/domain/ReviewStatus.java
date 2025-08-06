package avengers.lion.mission.domain;

import lombok.Getter;

@Getter
public enum ReviewStatus {
    ACTIVE("작성 완료"),
    INACTIVE("미작성");

    private final String name;

    ReviewStatus(String name){
        this.name=name;
    }

}
