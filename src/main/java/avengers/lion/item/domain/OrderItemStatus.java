package avengers.lion.item.domain;

import lombok.Getter;

@Getter
public enum OrderItemStatus {
    CONSUMED("사용 완료"),
    UNUSED("미 사용"),
    EXPIRED("유효기간 만료");

    private final String name;

    OrderItemStatus(String name){
        this.name=name;
    }
}
