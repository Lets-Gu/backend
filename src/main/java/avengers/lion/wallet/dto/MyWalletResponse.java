package avengers.lion.wallet.dto;

import java.util.List;

public record MyWalletResponse(
        Long giftCardCount,
        Long parentItemCount,
        Long consumedItemCount,
        List<GiftCardResponse> giftCards,
        List<ParentItemResponse> parentItems,
        List<ConsumedItemResponse> consumedItems) {
}
