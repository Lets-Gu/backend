package avengers.lion.wallet.dto;

import avengers.lion.item.domain.OrderItem;

public record ParentItemResponse(Long orderItemId, Long itemId, String itemName, String imageUrl) {

    public static ParentItemResponse of(OrderItem orderItem){
        return new ParentItemResponse(orderItem.getId(), orderItem.getItem().getId(), orderItem.getItem().getName(), orderItem.getItem().getImageUrl());
    }
}
