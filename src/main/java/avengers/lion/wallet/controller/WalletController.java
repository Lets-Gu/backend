package avengers.lion.wallet.controller;

import avengers.lion.wallet.api.WalletApi;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import avengers.lion.global.response.ResponseBody;
import avengers.lion.global.response.ResponseUtil;
import avengers.lion.wallet.dto.ConsumedItemResponse;
import avengers.lion.wallet.dto.GiftCardResponse;
import avengers.lion.wallet.dto.ParentItemResponse;
import avengers.lion.wallet.dto.RewardHistoryResponse;
import avengers.lion.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class WalletController implements WalletApi {

    private final WalletService walletService;
    /*
    내가 구매한 상품권 조회
     */
    @GetMapping("/gift-cards")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ResponseBody<List<GiftCardResponse>>> getMyGiftCards(@AuthenticationPrincipal Long memberId){
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(walletService.getMyGiftCards(memberId)));
    }

    /*
    내 제휴 쿠폰 조회
     */
    @GetMapping("/partner-cards")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ResponseBody<List<ParentItemResponse>>> getMyPartnerCards(@AuthenticationPrincipal Long memberId){
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(walletService.getMyPartnerCards(memberId)));
    }

    /*
    사용한 상품권/제휴쿠폰 조회
     */
    @GetMapping("/used-items")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ResponseBody<List<ConsumedItemResponse>>> getMyUsedItems(@AuthenticationPrincipal Long memberId){
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(walletService.getMyUsedItems(memberId)));
    }


    /*
    리워드 내역 조회
     */
    @GetMapping("/reward-history")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ResponseBody<List<RewardHistoryResponse>>> getRewardHistory(@AuthenticationPrincipal Long memberId){
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(walletService.getRewardHistory(memberId)));
    }
}
