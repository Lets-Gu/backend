package avengers.lion.item.api;

import avengers.lion.auth.domain.KakaoMemberDetails;
import avengers.lion.global.config.swagger.SwaggerApiFailedResponse;
import avengers.lion.global.config.swagger.SwaggerApiResponses;
import avengers.lion.global.config.swagger.SwaggerApiSuccessResponse;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.global.response.ResponseBody;
import avengers.lion.item.dto.ExchangeItemRequest;
import avengers.lion.item.dto.ItemResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "아이템 API", description = "리워드샵 아이템 관련 API")
public interface ItemApi {

    @Operation(
            summary = "전체 아이템 조회",
            description = """
                    리워드샵에서 포인트로 교환 가능한 모든 아이템의 목록을 조회합니다.<br>
                    각 아이템의 이름, 가격, 재고 수량 등의 정보를 확인할 수 있습니다.<br>
                    사용자는 이 목록에서 원하는 아이템을 선택하여 교환할 수 있습니다.
                    """
    )
    @ApiResponse(content = @Content(schema = @Schema(implementation = ItemResponse.class)))
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = ItemResponse[].class,
                    description = "전체 아이템 조회가 성공적으로 완료되었습니다."
            ),
            errors = {
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.ACCESS_DENIED,
                            description = "USER 권한이 필요합니다."
                    )
            }
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<ResponseBody<List<ItemResponse>>> getAllItem();

    @Operation(
            summary = "아이템 교환",
            description = """
                    사용자의 포인트를 사용하여 아이템을 교환합니다.<br>
                    교환하려는 수량만큼 충분한 포인트가 있어야 하며,<br>
                    아이템의 재고 수량이 교환하려는 수량보다 많아야 합니다.<br>
                    교환 성공 시 사용자의 포인트가 차감되고 아이템 재고가 감소합니다.
                    """
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    description = "아이템 교환이 성공적으로 완료되었습니다."
            ),
            errors = {
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.ITEM_NOT_FOUND,
                            description = "존재하지 않는 아이템입니다."
                    ),
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.MEMBER_NOT_FOUND,
                            description = "존재하지 않는 사용자입니다."
                    ),
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.STOCK_NOT_AVAILABLE,
                            description = "교환하려는 수량보다 아이템의 재고가 부족합니다."
                    ),
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.INSUFFICIENT_POINT,
                            description = "아이템 교환에 필요한 포인트가 부족합니다."
                    ),
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.ACCESS_DENIED,
                            description = "USER 권한이 필요합니다."
                    ),

            }
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<ResponseBody<Void>> exchangeItem(
            @AuthenticationPrincipal KakaoMemberDetails kakaoMemberDetails,
            @Parameter(description = "교환할 아이템 ID", example = "1")
            @PathVariable Long itemId,
            @Valid @RequestBody ExchangeItemRequest exchangeItemRequest
    );
}