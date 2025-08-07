package avengers.lion.item.dto;

import jakarta.validation.constraints.Min;

public record ExchangeItemRequest(@Min(value = 1, message = "수량은 1개 이상이어야 합니다") int count) {
}
