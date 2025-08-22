package avengers.lion.wallet.dto;

import avengers.lion.item.domain.OrderItem;

public record ConsumedItemResponse(Long itemId, String itemName, String imageUrl) {

    public static ConsumedItemResponse of(OrderItem orderItem){
        return new ConsumedItemResponse(orderItem.getItem().getId(), orderItem.getItem().getName(), orderItem.getItem().getImageUrl());
    }
}
