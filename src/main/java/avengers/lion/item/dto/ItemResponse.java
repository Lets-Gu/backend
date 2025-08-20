package avengers.lion.item.dto;


import avengers.lion.item.domain.Item;

public record ItemResponse(Long itemId, String itemName, int price, int count, String imageUrl) {

    public static ItemResponse of(Item item){
        return new ItemResponse(
                item.getId(),
                item.getName(),
                item.getPrice(),
                item.getStockCount(),
                item.getImageUrl()
        );
    }
}
