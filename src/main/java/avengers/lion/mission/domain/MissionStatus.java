package avengers.lion.mission.domain;

import lombok.Getter;

@Getter
public enum MissionStatus {

    ACTIVE("활성화"),
    INACTIVE("비활성화");

    MissionStatus(String name){
        this.name=name;
    }

    private final String name;
}
