package avengers.lion.wallet.domain;

import lombok.Getter;

@Getter
public enum PointType {

    MISSION_SUCCESS("미션 성공"),
    GIFT_CARD_EXCHANGE("구미사랑상품권 교환"),
    PARTNER_ITEM_EXCHANGE("제휴 쿠폰 교환"),
    REVIEW("리뷰 작성");


    private final String name;

    PointType(String name){
        this.name=name;
    }
}
