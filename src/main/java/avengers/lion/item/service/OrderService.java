package avengers.lion.item.service;

import avengers.lion.item.domain.*;
import avengers.lion.item.repository.OrderItemRepository;
import avengers.lion.item.repository.OrdersRepository;
import avengers.lion.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;

    /*
    내가 구매한 상품권 조회
     */
    public List<Orders> getGiftCards(Long memberId) {
        // memberId를 통해 해당 유저가 구매한 상품을 들고옴
        // 상품 카테고리는 GiftCard, ItemStatus는 NOT_USED인 제품으로
        return ordersRepository.findOrderItemByMemberIdAndItemStatus(memberId, OrderItemStatus.UNUSED, List.of(ItemCategory.GIFT_CARD));
    }

    /*
    내가 구매한 제휴 쿠폰 조회
     */
    public List<Orders> getPartnerCards(Long memberId) {
        return ordersRepository.findOrderItemByMemberIdAndItemStatus(memberId, OrderItemStatus.UNUSED, List.of(ItemCategory.PARTNER_ITEM));
    }

    /*
    사용 완료한 쿠폰 조회
     */
    public List<Orders> getConsumedItem(Long memberId) {
        return ordersRepository.findOrderItemByMemberIdAndItemStatus(memberId, OrderItemStatus.CONSUMED, List.of(ItemCategory.PARTNER_ITEM, ItemCategory.GIFT_CARD));
    }

    public Orders createOrder(Member member){
        Orders orders = Orders.builder()
                .member(member)
                .orderStatus(OrderStatus.PAID)
                .build();
        return ordersRepository.save(orders);
    }
    // TODO : 상품권, 제휴쿠폰 사용 기능 구현

    /*
    주문 아이템 생성
     */
    public void createOrderItem(Orders orders, Item item, int count){
        OrderItem orderItem = OrderItem.builder()
                .item(item)
                .count(count)
                .orders(orders)
                .orderItemStatus(OrderItemStatus.UNUSED)
                .build();
        orderItemRepository.save(orderItem);
    }
    
    /*
    아이템 구매 완전 처리 (주문 + 주문아이템 생성)
     */
    @Transactional
    public void createCompleteOrder(Member member, Item item, int count) {
        // 주문 생성
        Orders orders = createOrder(member);
        // 주문 아이템 생성
        createOrderItem(orders, item, count);

    }
}
