package avengers.lion.mission.domain;

import lombok.Getter;

@Getter
public enum BatchStatus {

    ACTIVE("진행 중인 미션"),
    COMPLETED("지난 미션");

    BatchStatus(String name){
        this.name=name;
    }
    private final String name;
}
