package avengers.lion.wallet.controller;

import avengers.lion.wallet.api.WalletApi;
import avengers.lion.wallet.dto.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import avengers.lion.global.response.ResponseBody;
import avengers.lion.global.response.ResponseUtil;
import avengers.lion.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class  WalletController implements WalletApi {

    private final WalletService walletService;


    /*
    내 포인트 조회
     */
    @GetMapping("/my-point")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ResponseBody<MyPointResponse>> getMyPoint(@AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(walletService.getMyPoint(memberId)));
    }

    @GetMapping("/my-wallet")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ResponseBody<MyWalletResponse>> getMyWallet(@AuthenticationPrincipal Long memberId){
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(walletService.getMyWallet(memberId)));
    }
    /*
    리워드 내역 조회
     */
    @GetMapping("/reward-history")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ResponseBody<List<RewardHistoryResponse >>> getRewardHistory(@AuthenticationPrincipal Long memberId){
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(walletService.getRewardHistory(memberId)));
    }
    /*
    상품권 사용
     */
    @PostMapping("/my-wallet/{orderItemId}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ResponseBody<Void>> useItem(@AuthenticationPrincipal Long memberId, @PathVariable Long orderItemId ) {
        walletService.useItem(memberId, orderItemId);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse());
    }
}
