package avengers.lion.wallet.dto;

import avengers.lion.wallet.domain.PointTransaction;
import avengers.lion.wallet.domain.PointType;

public record RewardHistoryResponse(Long pointTransactionId, PointType pointType, int changeAmount, int balanceAfter) {

    public static RewardHistoryResponse of(PointTransaction pointTransaction) {
        return new RewardHistoryResponse(
                pointTransaction.getId(),
                pointTransaction.getPointType(),
                pointTransaction.getChangeAmount(),
                pointTransaction.getBalanceAfter()
        );
    }
}
