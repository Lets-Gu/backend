package avengers.lion.mission.api;

import avengers.lion.global.config.swagger.SwaggerApiFailedResponse;
import avengers.lion.global.config.swagger.SwaggerApiResponses;
import avengers.lion.global.config.swagger.SwaggerApiSuccessResponse;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.global.response.ResponseBody;
import avengers.lion.mission.dto.GpsAuthenticationRequest;
import avengers.lion.mission.dto.MissionPreReviewResponse;
import avengers.lion.mission.dto.MissionResponse;
import avengers.lion.mission.dto.MissionReviewResponse;
import avengers.lion.review.domain.SortType;
import avengers.lion.global.base.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "미션 API", description = "미션 목록, 미션별 리뷰(프리뷰/스크롤), GPS 인증")
@RequestMapping("/api/v1/missions")
public interface MissionApi {

    // -----------------------------------------------
    // 1) 미션하러가기 - 전체 조회
    // -----------------------------------------------
    @Operation(
            summary = "미션 전체 조회",
            description = """
                    활성 상태의 미션을 모두 조회합니다.<br/>
                    프런트는 각 미션 카드에 `isCompleted` 여부(사용자가 해당 미션을 완료했는지)를 표시할 수 있습니다.
                    """,
            security = { @SecurityRequirement(name = "JWT") }
    )
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = MissionResponse.class)))
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = MissionResponse[].class,
                    description = "미션 전체 조회 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(value = ExceptionType.ACCESS_DENIED, description = "USER 권한이 필요합니다.")
            }
    )
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    ResponseEntity<ResponseBody<List<MissionResponse>>> getAllMissions(
            @AuthenticationPrincipal Long memberId
    );

    // -----------------------------------------------
    // 2) 특정 미션의 리뷰 - 프리뷰(처음 3개) + hasNext/nextId
    // -----------------------------------------------
    @Operation(
            summary = "미션 리뷰 프리뷰(처음 3개)",
            description = """
                    특정 미션의 리뷰를 프리뷰로 최대 3개까지 내려줍니다.<br/>
                    응답에는 리뷰 총 개수(`count`)와 다음 페이지 유무(`hasNext`), 다음 커서(`nextId`)가 포함됩니다.<br/><br/>
                    • 정렬: `sortType` = DESC(최신순, 기본) / ASC(오래된순)<br/>
                    • 다음 페이지가 필요하면 `/reviews/scroll`을 `lastReviewId=nextId`로 호출하세요.
                    """,
            security = { @SecurityRequirement(name = "JWT") }
    )
    @Parameter(name = "missionId", description = "미션 ID", required = true, schema = @Schema(type = "integer", format = "int64"))
    @Parameter(name = "sortType", description = "정렬 방향 (DESC=최신순 / ASC=오래된순). 기본값 DESC",
            schema = @Schema(implementation = SortType.class, defaultValue = "DESC", allowableValues = {"ASC","DESC"}))
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(schema = @Schema(implementation = MissionPreReviewResponse.class))
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = MissionPreReviewResponse.class,
                    description = "미션 리뷰 프리뷰 조회 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(value = ExceptionType.MISSION_NOT_FOUND, description = "해당 미션이 존재하지 않습니다."),
                    @SwaggerApiFailedResponse(value = ExceptionType.ACCESS_DENIED, description = "USER 권한이 필요합니다.")
            }
    )
    @GetMapping("/{missionId}/reviews/preview")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    ResponseEntity<ResponseBody<MissionPreReviewResponse>> getMissionReviews(
            @PathVariable Long missionId,
            @RequestParam(required = false, defaultValue = "DESC") SortType sortType
    );

    // -----------------------------------------------
    // 3) 특정 미션의 리뷰 - 무한 스크롤
    // -----------------------------------------------
    @Operation(
            summary = "미션 리뷰 스크롤(무한 페이징)",
            description = """
                    특정 미션의 리뷰를 커서 기반으로 무한 스크롤 조회합니다.<br/>
                    • 첫 페이지는 `lastReviewId` 없이 호출하세요.<br/>
                    • 다음 페이지부터는 직전 응답의 `nextId`를 `lastReviewId`로 그대로 전달합니다.<br/>
                    • 정렬: `sortType` = DESC(최신순, 기본) / ASC(오래된순)<br/>
                    • 페이지 크기: `limit` (기본 3)<br/>
                    응답의 `hasNext=false`면 더 이상 데이터가 없습니다.
                    """,
            security = { @SecurityRequirement(name = "JWT") }
    )
    @Parameter(name = "missionId", description = "미션 ID", required = true, schema = @Schema(type = "integer", format = "int64"))
    @Parameter(name = "lastReviewId", description = "다음 페이지 시작 커서(직전 응답의 nextId). 첫 페이지면 생략",
            schema = @Schema(type = "integer", format = "int64"))
    @Parameter(name = "limit", description = "페이지 크기(기본 3)", schema = @Schema(type = "integer", defaultValue = "3"))
    @Parameter(name = "sortType", description = "정렬 방향 (DESC=최신순 / ASC=오래된순). 기본값 DESC",
            schema = @Schema(implementation = SortType.class, defaultValue = "DESC", allowableValues = {"ASC","DESC"}))
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(schema = @Schema(implementation = PageResult.class))
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = PageResult.class,
                    description = "미션 리뷰 스크롤 조회 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(value = ExceptionType.MISSION_NOT_FOUND, description = "해당 미션이 존재하지 않습니다."),
                    @SwaggerApiFailedResponse(value = ExceptionType.ACCESS_DENIED, description = "USER 권한이 필요합니다.")
            }
    )
    @GetMapping("/{missionId}/reviews/scroll")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    ResponseEntity<ResponseBody<PageResult<MissionReviewResponse>>> getMissionReviewsScroll(
            @PathVariable Long missionId,
            @RequestParam(required = false) Long lastReviewId,
            @RequestParam(required = false, defaultValue = "3") int limit,
            @RequestParam(required = false, defaultValue = "DESC") SortType sortType
    );

    // -----------------------------------------------
    // 4) 미션 GPS 인증
    // -----------------------------------------------
    @Operation(
            summary = "미션 GPS 인증",
            description = """
                    사용자의 현재 GPS 위치로 해당 미션의 인증을 수행합니다.<br/>
                    서버는 사전 정의된 반경(예: 100m) 내에 있는지 검증합니다.
                    """,
            security = { @SecurityRequirement(name = "JWT") }
    )
    @Parameter(name = "missionId", description = "미션 ID", required = true, schema = @Schema(type = "integer", format = "int64"))
    @RequestBody(
            required = true,
            content = @Content(schema = @Schema(implementation = GpsAuthenticationRequest.class))
    )
    @ApiResponse(responseCode = "200", description = "GPS 인증 성공")
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(response = Void.class, description = "GPS 인증 성공"),
            errors = {
                    @SwaggerApiFailedResponse(value = ExceptionType.MISSION_NOT_FOUND, description = "해당 미션이 존재하지 않습니다."),
                    @SwaggerApiFailedResponse(value = ExceptionType.GPS_AUTH_FAILED, description = "GPS 인증 실패(반경 벗어남)"),
                    @SwaggerApiFailedResponse(value = ExceptionType.ACCESS_DENIED, description = "USER 권한이 필요합니다.")
            }
    )
    @PostMapping("/{missionId}/gps")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    ResponseEntity<ResponseBody<Void>> gpsAuthentication(
            @PathVariable Long missionId,
            @RequestBody GpsAuthenticationRequest gpsAuthenticationRequest
    );
}
