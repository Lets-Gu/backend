package avengers.lion.review.api;

import avengers.lion.global.base.PageResult;
import avengers.lion.global.config.swagger.SwaggerApiFailedResponse;
import avengers.lion.global.config.swagger.SwaggerApiResponses;
import avengers.lion.global.config.swagger.SwaggerApiSuccessResponse;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.global.response.ResponseBody;
import avengers.lion.review.domain.SortType;
import avengers.lion.review.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "리뷰 API", description = "리뷰 오버뷰(카운트/프리뷰/커서), 무한 스크롤, 리뷰 작성")
@RequestMapping("/api/v1/reviews")
public interface ReviewApi {

    // -------------------------------------------------------
    // 1) 오버뷰: 카운트 + 각 탭 프리뷰(미리보기) + 각 next 커서
    // -------------------------------------------------------
    @Operation(
            summary = "리뷰 프리뷰(초기 진입)",
            description = """
                    리뷰 페이지 최초 진입 시 한 번 호출합니다.\n
                    • 작성 가능한 리뷰/작성한 리뷰의 총 개수 제공\n
                    • 각 탭 프리뷰 목록을 previewLimit개 제공\n
                    • 각 탭 다음 페이지를 위한 커서(nextId, nextAt) 제공\n
                    이후 무한 스크롤은 /unwritten/page, /written/page API로 cursorId를 넘겨 조회하세요.
                    """,
            security = { @SecurityRequirement(name = "JWT") }
    )
    @Parameter(
            name = "previewLimit",
            description = "각 탭 프리뷰 아이템 수 (1~50). 기본 6",
            schema = @Schema(type = "integer", minimum = "1", maximum = "50", defaultValue = "6")
    )
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(schema = @Schema(implementation = OverviewResponse.class))
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = OverviewResponse.class,
                    description = "오버뷰 조회 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(value = ExceptionType.ACCESS_DENIED, description = "USER 권한이 필요합니다."),
                    @SwaggerApiFailedResponse(value = ExceptionType.MEMBER_NOT_FOUND, description = "존재하지 않는 사용자입니다.")
            }
    )
    @GetMapping("/overview")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    ResponseEntity<ResponseBody<OverviewResponse>> getOverview(
            @AuthenticationPrincipal Long memberId,
            @RequestParam(defaultValue = "6") @Min(1) @Max(50) int previewLimit
    );

    // -------------------------------------------------------
    // 2) 작성 가능한 리뷰 → 리뷰 작성
    // -------------------------------------------------------
    @Operation(
            summary = "작성 가능한 리뷰 작성",
            description = """
                    미작성(INACTIVE) 상태의 완료 미션에 대해 리뷰를 작성합니다.
                    성공 시 해당 완료 미션은 ACTIVE로 변경됩니다.
                    """,
            security = { @SecurityRequirement(name = "JWT") }
    )
    @ApiResponse(responseCode = "200", description = "작성 성공")
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(response = Void.class, description = "리뷰 작성 성공"),
            errors = {
                    @SwaggerApiFailedResponse(value = ExceptionType.ACCESS_DENIED, description = "본인의 완료 미션만 작성 가능"),
                    @SwaggerApiFailedResponse(value = ExceptionType.COMPLETED_MISSION_NOT_FOUND, description = "완료 미션 없음"),
                    @SwaggerApiFailedResponse(value = ExceptionType.REVIEW_ALREADY_EXISTS, description = "이미 리뷰가 존재"),
                    @SwaggerApiFailedResponse(value = ExceptionType.MEMBER_NOT_FOUND, description = "존재하지 않는 사용자")
            }
    )
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    ResponseEntity<ResponseBody<Void>> writeUnWrittenReview(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody WriteReviewRequest writeReviewRequest
    );

    // -------------------------------------------------------
    // 3) 작성 가능한 리뷰: 무한 스크롤 페이지
    // -------------------------------------------------------
    @Operation(
            summary = "작성 가능한 리뷰 페이지(무한 스크롤)",
            description = """
                    작성 가능한 리뷰(INACTIVE)를 무한 스크롤로 조회합니다.\n
                    • 다음 페이지는 직전 응답의 nextId를 cursorId로 그대로 전달\n
                    • 정렬 sort: DESC(최신순, 기본)\n
                    • limit: 1~100 (기본 6) -> limit 기본값이 4이므로 값을 넣지 않고 전달해도 됩니다.\n
                    응답의 hasNext=false면 더 불러올 데이터가 없습니다.
                    """,
            security = { @SecurityRequirement(name = "JWT") }
    )
    @Parameter(name = "cursorId", description = "다음 페이지 시작 커서(직전 응답 nextId)", schema = @Schema(type = "integer", format = "int64"))
    @Parameter(name = "sort", description = "정렬 방향 기본값 DESC",
            schema = @Schema(implementation = SortType.class, defaultValue = "DESC", allowableValues = {"ASC","DESC"}))
    @Parameter(name = "limit", description = "페이지 크기 (1~100, 기본 6)",
            schema = @Schema(type = "integer", minimum = "1", maximum = "100", defaultValue = "5"))
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(schema = @Schema(implementation = PageResult.class))
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(response = PageResult.class, description = "작성 가능한 리뷰 페이지 조회 성공"),
            errors = {
                    @SwaggerApiFailedResponse(value = ExceptionType.ACCESS_DENIED, description = "USER 권한이 필요합니다."),
                    @SwaggerApiFailedResponse(value = ExceptionType.MEMBER_NOT_FOUND, description = "존재하지 않는 사용자입니다.")
            }
    )
    @GetMapping("/unwritten/page")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    ResponseEntity<ResponseBody<PageResult<UnWrittenReviewResponse>>> getUnwrittenPage(
            @AuthenticationPrincipal Long memberId,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "DESC") SortType sort,
            @RequestParam(defaultValue = "6") @Min(1) @Max(100) int limit
    );

    // -------------------------------------------------------
    // 4) 작성한 리뷰: 전체(최신순) 페이지
    // -------------------------------------------------------
    @Operation(
            summary = "작성한 리뷰 페이지(무한 스크롤)",
            description = """
                    내가 작성한 리뷰(ACTIVE)를 최신순(DESC)으로 무한 스크롤 조회합니다\n.
                    • 다음 페이지는 직전 응답의 nextId를 cursorId로 그대로 전달\n
                    • limit: 1~100 (기본 6)
                    """,
            security = { @SecurityRequirement(name = "JWT") }
    )
    @Parameter(name = "cursorId", description = "다음 페이지 시작 커서(직전 응답 nextId)", schema = @Schema(type = "integer", format = "int64"))
    @Parameter(name = "limit", description = "페이지 크기 (1~100, 기본 6)", schema = @Schema(type = "integer", minimum = "1", maximum = "100", defaultValue = "5"))
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(schema = @Schema(implementation = PageResult.class))
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(response = PageResult.class, description = "작성한 리뷰(최신순) 페이지 조회 성공"),
            errors = {
                    @SwaggerApiFailedResponse(value = ExceptionType.ACCESS_DENIED, description = "USER 권한이 필요합니다."),
                    @SwaggerApiFailedResponse(value = ExceptionType.MEMBER_NOT_FOUND, description = "존재하지 않는 사용자입니다.")
            }
    )
    @GetMapping("/written/page")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    ResponseEntity<ResponseBody<PageResult<WrittenReviewResponse>>> getWrittenPage(
            @AuthenticationPrincipal Long memberId,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "6") @Min(1) @Max(100) int limit
    );

    // -------------------------------------------------------
    // 5) 작성한 리뷰: 상세(ASC/DESC) 페이지
    // -------------------------------------------------------
    @Operation(
            summary = "작성한 리뷰 상세 페이지(",
            description = """
                    내가 작성한 리뷰(ACTIVE)를 ASC/DESC로 무한 스크롤 조회합니다.
                    • 첫 페이지는 cursorId 없이 호출
                    • 다음 페이지부터는 직전 응답의 nextId를 cursorId로 그대로 전달
                    • 정렬 sort: DESC(최신순, 기본) / ASC(오래된순)
                    • limit: 1~100 (기본 6)
                    """,
            security = { @SecurityRequirement(name = "JWT") }
    )
    @Parameter(name = "cursorId", description = "다음 페이지 시작 커서(직전 응답 nextId). 첫 페이지는 생략", schema = @Schema(type = "integer", format = "int64"))
    @Parameter(name = "sort", description = "정렬 방향 (DESC=최신순 / ASC=오래된순). 기본값 DESC",
            schema = @Schema(implementation = SortType.class, defaultValue = "DESC", allowableValues = {"ASC","DESC"}))
    @Parameter(name = "limit", description = "페이지 크기 (1~100, 기본 6)", schema = @Schema(type = "integer", minimum = "1", maximum = "100", defaultValue = "5"))
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(schema = @Schema(implementation = PageResult.class))
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(response = PageResult.class, description = "작성한 리뷰(정렬 지원) 페이지 조회 성공"),
            errors = {
                    @SwaggerApiFailedResponse(value = ExceptionType.ACCESS_DENIED, description = "USER 권한이 필요합니다."),
                    @SwaggerApiFailedResponse(value = ExceptionType.MEMBER_NOT_FOUND, description = "존재하지 않는 사용자입니다.")
            }
    )
    @GetMapping("/written/detail/page")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    ResponseEntity<ResponseBody<PageResult<WrittenReviewResponse>>> getWrittenDetailPage(
            @AuthenticationPrincipal Long memberId,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "DESC") SortType sort,
            @RequestParam(defaultValue = "6") @Min(1) @Max(100) int limit
    );
}
