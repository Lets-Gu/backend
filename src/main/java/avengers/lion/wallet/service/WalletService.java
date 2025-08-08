package avengers.lion.wallet.service;

import avengers.lion.global.exception.BusinessException;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.item.domain.Orders;
import avengers.lion.item.service.OrderService;
import avengers.lion.member.domain.Member;
import avengers.lion.wallet.domain.PointTransaction;
import avengers.lion.wallet.dto.ConsumedItemResponse;
import avengers.lion.wallet.dto.GiftCardResponse;
import avengers.lion.wallet.dto.ParentItemResponse;
import avengers.lion.wallet.dto.RewardHistoryResponse;
import avengers.lion.wallet.repository.PointTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class WalletService {

    private final PointTransactionRepository pointTransactionRepository;
    private final OrderService orderService;

    /*
    내 상품권 조회
     */
    public List<GiftCardResponse> getMyGiftCards(Long memberId){
        List<Orders> orders = orderService.getGiftCards(memberId);
        return orders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .map(GiftCardResponse::of)
                .toList();

    }
    /*
    내 제휴상품 조회
     */
    public List<ParentItemResponse> getMyPartnerCards(Long memberId){
        List<Orders> orders = orderService.getPartnerCards(memberId);
        return orders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .map(ParentItemResponse::of)
                .toList();
    }
    /*
    사용한 상품 조회
     */
    public List<ConsumedItemResponse>  getMyUsedItems(Long memberId){
        List<Orders> orders = orderService.getConsumedItem(memberId);
        return orders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .map(ConsumedItemResponse::of)
                .toList();
    }

    /*
    리워드 내역 조회
     */
    public List<RewardHistoryResponse> getRewardHistory(Long memberId){
        List<PointTransaction> pointTransactions = pointTransactionRepository.findAllByMemberId(memberId);
        return pointTransactions.stream()
                .map(RewardHistoryResponse::of)
                .toList();
    }

    /*
    리워드 거래 내역 추가
     */
    public void addPointTransaction(int price, Member member){
        if(price<=0)
            throw new BusinessException(ExceptionType.PRICE_IS_POSITIVE);
        PointTransaction pointTransaction = PointTransaction.builder()
                .changeAmount(-price)
                .member(member)
                .build();
        pointTransactionRepository.save(pointTransaction);
    }
}
