package avengers.lion.wallet.dto;

import avengers.lion.item.domain.OrderItem;

public record ParentItemResponse(Long itemId, String itemName) {

    public static ParentItemResponse of(OrderItem orderItem){
        return new ParentItemResponse(orderItem.getItem().getId(), orderItem.getItem().getName());
    }
}
