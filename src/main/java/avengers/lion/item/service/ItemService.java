package avengers.lion.item.service;

import avengers.lion.global.exception.BusinessException;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.item.domain.Item;
import avengers.lion.item.dto.ExchangeItemRequest;
import avengers.lion.item.dto.ItemResponse;
import avengers.lion.item.repository.ItemRepository;
import avengers.lion.member.domain.Member;
import avengers.lion.member.repository.MemberRepository;
import avengers.lion.wallet.service.WalletService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final WalletService walletService;
    private final OrderService orderService;

    /*
    아이템 전체 조회
     */
    public List<ItemResponse> getAllItem(){
        List<Item> items = itemRepository.findAll();
        return items.stream()
                .map(ItemResponse::of)
                .toList();
    }

    /*
    아이템 교환하기
     */
    @Transactional
    public void exchangeItem (Long memberId, Long itemId, ExchangeItemRequest exchangeItemRequest){
        int count = exchangeItemRequest.count();
        // 상품을 찾기
        Item item = itemRepository.findById(itemId)
                .orElseThrow(()-> new BusinessException(ExceptionType.ITEM_NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new BusinessException(ExceptionType.MEMBER_NOT_FOUND));
        int price = Math.multiplyExact(item.getPrice(), count);
        // 구매하고자 하는 수량보다 재고가 적다 -> 에러 발생
        if(item.getStockCount()<count){
            throw new BusinessException(ExceptionType.STOCK_NOT_AVAILABLE);
        }
        member.buyItemByPoint(price);
        walletService.addPointTransaction(price, member);
        item.buyItem(count);
        
        // 주문 생성 (통합 메소드 사용)
        orderService.createCompleteOrder(member, item, count);
    }
}
