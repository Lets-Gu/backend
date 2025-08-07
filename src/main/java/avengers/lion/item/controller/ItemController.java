package avengers.lion.item.controller;

import avengers.lion.auth.domain.KakaoMemberDetails;
import avengers.lion.global.response.ResponseBody;
import avengers.lion.global.response.ResponseUtil;
import avengers.lion.item.api.ItemApi;
import avengers.lion.item.dto.ExchangeItemRequest;
import avengers.lion.item.dto.ItemResponse;
import avengers.lion.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController implements ItemApi {

    private final ItemService itemService;

    /*
    리워드샵 : 아이템 전체 조회
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseBody<List<ItemResponse>>> getAllItem(){
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(itemService.getAllItem()));
    }

    /*
    아이템 교환하기
     */
    @PostMapping("/{itemId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseBody<Void>> exchangeItem(@AuthenticationPrincipal KakaoMemberDetails kakaoMemberDetails,
                                                           @PathVariable Long itemId,
                                                           @RequestBody ExchangeItemRequest exchangeItemRequest){
        itemService.exchangeItem(kakaoMemberDetails.getMemberId(), itemId, exchangeItemRequest);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse());
    }
}
