package avengers.lion.item.domain;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PAID("결제 완료"),
    PENDING("대기중");

    private final String name;

    OrderStatus(String name){
        this.name=name;
    }
}
