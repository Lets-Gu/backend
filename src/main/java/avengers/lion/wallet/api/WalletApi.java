package avengers.lion.wallet.api;

import avengers.lion.global.config.swagger.SwaggerApiFailedResponse;
import avengers.lion.global.config.swagger.SwaggerApiResponses;
import avengers.lion.global.config.swagger.SwaggerApiSuccessResponse;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.global.response.ResponseBody;
import avengers.lion.review.dto.UnWrittenReviewResponse;
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

import java.util.List;

@Tag(name = "지갑 API", description = "사용자 지갑 및 포인트 관련 API")
public interface WalletApi {

    @Operation(
            summary = "내 포인트 조회",
            description = """
                    현재 사용자의 보유 포인트를 조회합니다.<br>
                    JWT 인증을 통해 식별된 사용자 기준의 포인트 잔액을 반환합니다.
                    """,
            security = { @SecurityRequirement(name = "JWT") }
    )
    @ApiResponse(content = @Content(schema = @Schema(implementation = MyPointResponse.class)))
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = MyPointResponse.class,
                    description = "내 포인트 조회가 성공적으로 완료되었습니다."
            ),
            errors = {
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.ACCESS_DENIED,
                            description = "USER 권한이 필요합니다."
                    ),
            }
    )
    @PreAuthorize("hasAuthority('ROLE_USER')")
    ResponseEntity<ResponseBody<MyPointResponse>> getMyPoint(
            @AuthenticationPrincipal Long memberId
    );

    @Operation(
            summary = "내 상품권 조회",
            description = """
                    사용자가 포인트로 교환한 상품권 목록을 조회합니다.<br>
                    아직 사용하지 않은 활성 상태의 상품권들만 반환됩니다.<br>
                    각 상품권의 이름, 가격, 구매일 등의 정보를 확인할 수 있습니다.
                    """,
            security = { @SecurityRequirement(name = "JWT") }
    )
    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = GiftCardResponse.class))))
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = GiftCardResponse[].class,
                    description = "내 상품권 조회가 성공적으로 완료되었습니다."
            ),
            errors = {
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.MEMBER_NOT_FOUND,
                            description = "존재하지 않는 사용자입니다."
                    ),
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.ACCESS_DENIED,
                            description = "USER 권한이 필요합니다."
                    )
            }
    )
    @PreAuthorize("hasAuthority('ROLE_USER')")
    ResponseEntity<ResponseBody<List<GiftCardResponse>>> getMyGiftCards(
            @AuthenticationPrincipal Long memberId
    );

    @Operation(
            summary = "내 제휴 쿠폰 조회",
            description = """
                    사용자가 포인트로 교환한 제휴 쿠폰 목록을 조회합니다.<br>
                    파트너사에서 제공하는 할인 쿠폰이나 혜택 쿠폰들을 확인할 수 있습니다.<br>
                    아직 사용하지 않은 활성 상태의 제휴 쿠폰들만 반환됩니다.
                    """
    )
    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = ParentItemResponse.class))))
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = ParentItemResponse[].class,
                    description = "내 제휴 쿠폰 조회가 성공적으로 완료되었습니다."
            ),
            errors = {
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.MEMBER_NOT_FOUND,
                            description = "존재하지 않는 사용자입니다."
                    ),
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.ACCESS_DENIED,
                            description = "USER 권한이 필요합니다."
                    )
            }
    )
    @PreAuthorize("hasAuthority('ROLE_USER')")
    ResponseEntity<ResponseBody<List<ParentItemResponse>>> getMyPartnerCards(
            @AuthenticationPrincipal Long memberId
    );

    @Operation(
            summary = "사용한 아이템 조회",
            description = """
                    사용자가 이미 사용한 상품권과 제휴 쿠폰의 내역을 조회합니다.<br>
                    사용 완료된 상품권, 제휴 쿠폰들의 사용 이력을 확인할 수 있습니다.<br>
                    언제, 어떤 아이템을 사용했는지 기록을 통해 확인 가능합니다.
                    """,
            security = { @SecurityRequirement(name = "JWT") }
    )
    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = ConsumedItemResponse.class))))
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = ConsumedItemResponse[].class,
                    description = "사용한 아이템 조회가 성공적으로 완료되었습니다."
            ),
            errors = {
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.MEMBER_NOT_FOUND,
                            description = "존재하지 않는 사용자입니다."
                    ),
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.ACCESS_DENIED,
                            description = "USER 권한이 필요합니다."
                    )
            }
    )
    @PreAuthorize("hasAuthority('ROLE_USER')")
    ResponseEntity<ResponseBody<List<ConsumedItemResponse>>> getMyUsedItems(
            @AuthenticationPrincipal Long memberId
    );

    @Operation(
            summary = "리워드 내역 조회",
            description = """
                    사용자의 모든 포인트 거래 내역을 조회합니다.<br>
                    포인트 획득, 사용, 차감 등의 모든 거래 기록을 확인할 수 있습니다.<br>
                    미션 완료로 인한 포인트 획득, 아이템 교환으로 인한 포인트 차감 등<br>
                    포인트 변동에 대한 상세 내역을 시간순으로 조회할 수 있습니다.
                    """,
            security = { @SecurityRequirement(name = "JWT") }
    )
    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = RewardHistoryResponse.class))))
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = RewardHistoryResponse[].class,
                    description = "리워드 내역 조회가 성공적으로 완료되었습니다."
            ),
            errors = {
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.MEMBER_NOT_FOUND,
                            description = "존재하지 않는 사용자입니다."
                    ),
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.ACCESS_DENIED,
                            description = "본인의 완료된 미션에 대해서만 리뷰를 작성할 수 있습니다."
                    )
            }
    )
    @PreAuthorize("hasAuthority('ROLE_USER')")
    ResponseEntity<ResponseBody<List<RewardHistoryResponse>>> getRewardHistory(
            @AuthenticationPrincipal Long memberId
    );
}