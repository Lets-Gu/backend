package avengers.lion.wallet.service;

import avengers.lion.global.exception.BusinessException;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.item.domain.OrderItem;
import avengers.lion.item.domain.OrderItemStatus;
import avengers.lion.item.domain.Orders;
import avengers.lion.item.service.OrderService;
import avengers.lion.member.domain.Member;
import avengers.lion.member.service.MemberService;
import avengers.lion.wallet.domain.PointTransaction;
import avengers.lion.wallet.domain.PointType;
import avengers.lion.wallet.dto.*;
import avengers.lion.wallet.repository.PointTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class WalletService {

    private final PointTransactionRepository pointTransactionRepository;
    private final OrderService orderService;
    private final MemberService memberService;


    /*
    내 포인트 조회
     */
    public MyPointResponse getMyPoint(Long memberId){
        return new MyPointResponse(memberService.getMyPoint(memberId));
    }

    public MyWalletResponse getMyWallet(Long memberId){
        Long giftCardCount = orderService.myGiftCardCount(memberId);
        Long partnerCardCount = orderService.myPartnerCardCount(memberId);
        Long consumedItemCount = orderService.myConsumedItemCount(memberId);
        List<GiftCardResponse> giftCards = getMyGiftCards(memberId);
        List<ParentItemResponse> partnerCards = getMyPartnerCards(memberId);
        List<ConsumedItemResponse> usedItems = getMyUsedItems(memberId);
        return new MyWalletResponse(giftCardCount, partnerCardCount, consumedItemCount, giftCards, partnerCards, usedItems);
    }

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
    리워드 거래 내역 추가 (아이템 구매용)
     */
    public void addPointTransaction(long price, Member member){
        if(price<=0)
            throw new BusinessException(ExceptionType.PRICE_IS_POSITIVE);
        
        // 거래 후 잔액 계산 (가격만큼 차감)
        int balanceAfter = Math.toIntExact(member.getPoint() - price);
        
        PointTransaction pointTransaction = PointTransaction.builder()
                .changeAmount(Math.toIntExact(-price))
                .balanceAfter(Math.toIntExact(balanceAfter))
                .pointType(PointType.ITEM_EXCHANGE)
                .member(member)
                .build();
        pointTransactionRepository.save(pointTransaction);
    }

    /*
    상품 사용 기능
     */
    @Transactional
    public void useItem(Long memberId, Long orderItemId){
        OrderItem orderItem = orderService.getOrderItem(memberId, orderItemId);
        if (orderItem.getOrderItemStatus() == OrderItemStatus.CONSUMED) {
            throw new BusinessException(ExceptionType.ALREADY_USED_ITEM);
        }
        orderItem.useItem();
    }
}
