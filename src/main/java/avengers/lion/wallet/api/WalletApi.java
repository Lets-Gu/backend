package avengers.lion.wallet.api;

import avengers.lion.global.config.swagger.SwaggerApiFailedResponse;
import avengers.lion.global.config.swagger.SwaggerApiResponses;
import avengers.lion.global.config.swagger.SwaggerApiSuccessResponse;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.global.response.ResponseBody;
import avengers.lion.wallet.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "지갑 API", description = "포인트/아이템/리워드 내역 조회")
@RequestMapping("/api/v1/wallet")
public interface WalletApi {

    // ----------------------------------------------------------------
    // 내 포인트
    // ----------------------------------------------------------------
    @Operation(
            summary = "내 포인트 조회",
            description = """
                    현재 로그인한 사용자의 보유 포인트를 조회합니다.
                    클라이언트는 응답의 data.balance를 표시하면 됩니다.
                    """,
            security = { @SecurityRequirement(name = "JWT") }
    )
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(schema = @Schema(implementation = MyPointResponse.class))
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = MyPointResponse.class,
                    description = "내 포인트 조회 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(value = ExceptionType.ACCESS_DENIED, description = "USER 권한이 필요합니다.")
            }
    )
    @GetMapping("/my-point")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    ResponseEntity<ResponseBody<MyPointResponse>> getMyPoint(
            @AuthenticationPrincipal Long memberId
    );

    // ----------------------------------------------------------------
    // 내 지갑(집계 + 프리뷰)
    // ----------------------------------------------------------------
    @Operation(
            summary = "내 지갑 조회(집계+프리뷰)",
            description = """
                    지갑 화면 초기 진입 시 한 번 호출합니다.
                    • giftCardCount / partnerCardCount / consumedItemCount 개수
                    • giftCards / partnerCards / usedItems(각 카테고리 목록)
                    """,
            security = { @SecurityRequirement(name = "JWT") }
    )
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(schema = @Schema(implementation = MyWalletResponse.class))
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = MyWalletResponse.class,
                    description = "내 지갑 조회 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(value = ExceptionType.ACCESS_DENIED, description = "USER 권한이 필요합니다.")
            }
    )
    @GetMapping("/my-wallet")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    ResponseEntity<ResponseBody<MyWalletResponse>> getMyWallet(
            @AuthenticationPrincipal Long memberId
    );

    // ----------------------------------------------------------------
    // 리워드(포인트 거래) 내역
    // ----------------------------------------------------------------
    @Operation(
            summary = "리워드 내역 조회",
            description = """
                    사용자의 포인트 거래 내역을 최신순으로 조회합니다.
                    (획득/사용/차감 등 모든 변동 이력)
                    """,
            security = { @SecurityRequirement(name = "JWT") }
    )
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = RewardHistoryResponse.class)))
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = RewardHistoryResponse[].class,
                    description = "리워드 내역 조회 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(value = ExceptionType.ACCESS_DENIED, description = "USER 권한이 필요합니다.")
            }
    )
    @GetMapping("/reward-history")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    ResponseEntity<ResponseBody<List<RewardHistoryResponse>>> getRewardHistory(
            @AuthenticationPrincipal Long memberId
    );
}
