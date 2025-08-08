package avengers.lion.wallet.domain;

import lombok.Getter;

@Getter
public enum PointType {

    MISSION_SUCCESS("미션 성공"),
    ITEM_EXCHANGE("리워드 교환");


    private final String name;

    PointType(String name){
        this.name=name;
    }
}
