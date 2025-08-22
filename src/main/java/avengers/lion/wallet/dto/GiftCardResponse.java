package avengers.lion.wallet.dto;

import avengers.lion.item.domain.OrderItem;

import java.time.LocalDateTime;

public record GiftCardResponse(Long orderItemId, Long itemId, String itemName, LocalDateTime createdAt, String imageUrl) {

    public static GiftCardResponse of(OrderItem orderItem){
        return new GiftCardResponse(orderItem.getId(),
            orderItem.getItem().getId(),
            orderItem.getItem().getName(),
                orderItem.getCreatedAt(),
                orderItem.getItem().getImageUrl()
        );
    }
}
