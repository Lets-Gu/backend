package avengers.lion.wallet.dto;

import avengers.lion.item.domain.OrderItem;

public record ConsumedItemResponse(Long itemId, String itemName) {

    public static ConsumedItemResponse of(OrderItem orderItem){
        return new ConsumedItemResponse(orderItem.getItem().getId(), orderItem.getItem().getName());
    }
}
