package avengers.lion.mission.api;

import avengers.lion.global.config.swagger.SwaggerApiFailedResponse;
import avengers.lion.global.config.swagger.SwaggerApiResponses;
import avengers.lion.global.config.swagger.SwaggerApiSuccessResponse;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.global.response.ResponseBody;
import avengers.lion.mission.dto.GpsAuthenticationRequest;
import avengers.lion.mission.dto.MissionResponse;
import avengers.lion.mission.dto.MissionReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "미션 API", description = "미션 관련 API")
public interface MissionApi {

    @Operation(
            summary = "전체 미션 조회",
            description = """
                    활성화된 모든 미션의 목록을 조회합니다.<br>
                    미션 상태가 ACTIVE인 미션들만 반환됩니다.<br>
                    사용자는 이 목록에서 원하는 미션을 선택하여 참여할 수 있습니다.
                    """
    )
    @ApiResponse(content = @Content(schema = @Schema(implementation = MissionResponse.class, type="array")))
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = MissionResponse[].class,
                    description = "전체 미션 조회가 성공적으로 완료되었습니다."
            ),
            errors = {
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.ACCESS_DENIED,
                            description = "본인의 완료된 미션에 대해서만 리뷰를 작성할 수 있습니다."
                    )
            }
    )
    @PreAuthorize("hasAuthority('ROLE_USER')")
    ResponseEntity<ResponseBody<List<MissionResponse>>> getAllMissions();

    @Operation(
            summary = "미션 리뷰 조회",
            description = """
                    특정 미션에 대한 모든 리뷰를 조회합니다.<br>
                    해당 미션을 완료한 사용자들이 작성한 리뷰들을 확인할 수 있습니다.<br>
                    다른 사용자들의 후기를 통해 미션에 대한 정보를 미리 파악할 수 있습니다.
                    """
    )
    @ApiResponse(content = @Content(schema = @Schema(implementation = MissionReviewResponse.class, type="array")))
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = MissionReviewResponse[].class,
                    description = "미션 리뷰 조회가 성공적으로 완료되었습니다."
            ),
            errors = {
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.MISSION_NOT_FOUND,
                            description = "존재하지 않는 미션입니다."
                    ),
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.ACCESS_DENIED,
                            description = "본인의 완료된 미션에 대해서만 리뷰를 작성할 수 있습니다."
                    )
            }
    )
    @PreAuthorize("hasAuthority('ROLE_USER')")
    ResponseEntity<ResponseBody<List<MissionReviewResponse>>> getMissionReviews(
            @Parameter(description = "미션 ID", example = "1")
            @PathVariable Long missionId
    );

    @Operation(
            summary = "GPS 인증",
            description = """
                    사용자의 현재 위치를 통해 미션 지점 도착을 인증합니다.<br>
                    사용자의 GPS 좌표와 미션 지점의 좌표 간 거리를 계산하여<br>
                    100미터 이내에 있을 경우에만 인증이 성공됩니다.<br>
                    GPS 인증 성공 후에는 사진 인증 단계로 진행할 수 있습니다.
                    """
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    description = "GPS 인증이 성공적으로 완료되었습니다."
            ),
            errors = {
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.MISSION_NOT_FOUND,
                            description = "존재하지 않는 미션입니다."
                    ),
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.GPS_AUTH_FAILED,
                            description = "GPS 인증에 실패하였습니다."
                    ),
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.ACCESS_DENIED,
                            description = "본인의 완료된 미션에 대해서만 리뷰를 작성할 수 있습니다."
                    ),
            }
    )
    @PreAuthorize("hasAuthority('ROLE_USER')")
    ResponseEntity<ResponseBody<Void>> gpsAuthentication(
            @Parameter(description = "미션 ID", example = "1")
            @PathVariable Long missionId,
            @RequestBody GpsAuthenticationRequest gpsAuthenticationRequest
    );
}