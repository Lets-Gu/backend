package avengers.lion.wallet.controller;

import avengers.lion.auth.domain.KakaoMemberDetails;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    /*
    내가 구매한 상품권 조회
     */
    @GetMapping("/gift-cards")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseBody<List<GiftCardResponse>>> getMyGiftCards(@AuthenticationPrincipal KakaoMemberDetails kakaoMemberDetails){
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(walletService.getMyGiftCards(kakaoMemberDetails.getMemberId())));
    }

    /*
    내 제휴 쿠폰 조회
     */
    @GetMapping("/partner-cards")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseBody<List<ParentItemResponse>>> getMyPartnerCards(@AuthenticationPrincipal KakaoMemberDetails kakaoMemberDetails){
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(walletService.getMyPartnerCards(kakaoMemberDetails.getMemberId())));
    }

    /*
    사용한 상품권/제휴쿠폰 조회
     */
    @GetMapping("/used-items")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseBody<List<ConsumedItemResponse>>> getMyUsedItems(@AuthenticationPrincipal KakaoMemberDetails kakaoMemberDetails){
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(walletService.getMyUsedItems(kakaoMemberDetails.getMemberId())));
    }


    /*
    리워드 내역 조회
     */
    @GetMapping("/reward-history")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseBody<List<RewardHistoryResponse>>> getRewardHistory(@AuthenticationPrincipal KakaoMemberDetails kakaoMemberDetails){
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(walletService.getRewardHistory(kakaoMemberDetails.getMemberId())));
    }
}
