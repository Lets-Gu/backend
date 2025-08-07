package avengers.lion.item.domain;

import lombok.Getter;

@Getter
public enum ItemCategory {
    GIFT_CARD("상품권"),
    PARTNER_ITEM("제휴 상품");


    private final String name;

    ItemCategory(String name){
        this.name=name;
    }

}
