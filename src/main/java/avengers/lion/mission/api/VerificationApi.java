package avengers.lion.mission.api;

import avengers.lion.global.config.swagger.SwaggerApiFailedResponse;
import avengers.lion.global.config.swagger.SwaggerApiResponses;
import avengers.lion.global.config.swagger.SwaggerApiSuccessResponse;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.global.response.ResponseBody;
import avengers.lion.mission.dto.request.GpsAuthenticationRequest;
import avengers.lion.mission.dto.request.VerifyImageRequest;
import avengers.lion.mission.dto.response.UploadUrlResponse;
import avengers.lion.mission.dto.response.VerifyImageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "미션 인증 API", description = "GPS → 업로드 URL 발급 → 이미지 인증 시작 → SSE 실시간 결과")
@RequestMapping("/api/v1/missions")
public interface VerificationApi {

    // -----------------------------------------------
    // 1) GPS 인증
    // -----------------------------------------------
    @Operation(
            summary = "1단계 - GPS 인증",
            description = """
                    사용자가 미션 지점 반경 내에 있는지 검증합니다.<br/>
                    호출 시점: 사진 업로드 전 위치 확인 단계.<br/>
                    요청 본문: latitude, longitude 를 포함합니다.
                    """,
            security = { @SecurityRequirement(name = "JWT") }
    )
    @Parameter(name = "missionId", description = "미션 ID", required = true,
            schema = @Schema(type = "integer", format = "int64"))
    @ApiResponse(responseCode = "200", description = "GPS 인증 성공")
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(response = Void.class, description = "GPS 인증 성공"),
            errors = {
                    @SwaggerApiFailedResponse(value = ExceptionType.MISSION_NOT_FOUND, description = "해당 미션이 존재하지 않습니다."),
                    @SwaggerApiFailedResponse(value = ExceptionType.GPS_AUTH_FAILED, description = "허용 반경 밖입니다."),
                    @SwaggerApiFailedResponse(value = ExceptionType.ACCESS_DENIED, description = "USER 권한이 필요합니다.")
            }
    )
    @PostMapping("/{missionId}/gps")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    ResponseEntity<ResponseBody<Void>> gpsAuthentication(
            @PathVariable Long missionId,
            @Valid @RequestBody GpsAuthenticationRequest gpsAuthenticationRequest
    );

    // -----------------------------------------------
    // 2) 이미지 업로드 URL 발급 (Cloudinary)
    // -----------------------------------------------
    @Operation(
            summary = "2단계 - 업로드 URL 발급",
            description = """
                    클라이언트가 Cloudinary로 직접 업로드할 수 있도록 업로드 엔드포인트와 publicId, uploadPreset을 발급합니다.<br/>
                    발급 후, 프론트는 uploadUrl 로 file, upload_preset, public_id 를 포함하여 업로드합니다.
                    """,
            security = { @SecurityRequirement(name = "JWT") }
    )
    @Parameter(name = "missionId", description = "미션 ID", required = true,
            schema = @Schema(type = "integer", format = "int64"))
    @ApiResponse(
            responseCode = "200",
            description = "업로드 URL 발급 성공",
            content = @Content(schema = @Schema(implementation = UploadUrlResponse.class))
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(response = UploadUrlResponse.class, description = "업로드 URL 발급 성공"),
            errors = {
                    @SwaggerApiFailedResponse(value = ExceptionType.ACCESS_DENIED, description = "USER 권한이 필요합니다.")
            }
    )
    @GetMapping("/{missionId}/upload-url")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    ResponseEntity<ResponseBody<UploadUrlResponse>> getUploadUrl(
            @PathVariable Long missionId
    );

    // -----------------------------------------------
    // 3) 이미지 인증 시작 (비동기 분석 트리거)
    // -----------------------------------------------
    @Operation(
            summary = "3단계 - 이미지 인증 시작",
            description = """
                    Cloudinary에 업로드 완료된 이미지 URL로 인증 분석을 비동기로 시작합니다.<br/>
                    요청 본문: imageUrl(조회용 최종 URL), uploadKey(Cloudinary publicId).<br/>
                    응답 값의 jobId 로 4단계 SSE를 구독하세요.
                    """,
            security = { @SecurityRequirement(name = "JWT") }
    )
    @Parameter(name = "missionId", description = "미션 ID", required = true,
            schema = @Schema(type = "integer", format = "int64"))
    @ApiResponse(
            responseCode = "200",
            description = "이미지 인증 시작",
            content = @Content(schema = @Schema(implementation = VerifyImageResponse.class))
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(response = VerifyImageResponse.class, description = "이미지 인증 시작(비동기)"),
            errors = {
                    @SwaggerApiFailedResponse(value = ExceptionType.MISSION_NOT_FOUND, description = "해당 미션이 존재하지 않습니다."),
                    @SwaggerApiFailedResponse(value = ExceptionType.ACCESS_DENIED, description = "USER 권한이 필요합니다.")
            }
    )
    @PostMapping("/{missionId}/verify-image")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    ResponseEntity<ResponseBody<VerifyImageResponse>> startVerification(
            @PathVariable Long missionId,
            @Valid @RequestBody VerifyImageRequest request,
            @AuthenticationPrincipal Long memberId
    );

    // -----------------------------------------------
    // 4) SSE 이벤트 스트림 구독
    // -----------------------------------------------
    @Operation(
            summary = "4단계 - 인증 진행/완료 SSE 구독",
            description = """
                    3단계에서 받은 jobId 로 실시간 진행 상황과 결과를 수신합니다.<br/>
                    이벤트명: verification. COMPLETED/FAILED/ERROR 수신 시 스트림이 종료됩니다.
                    """,
            security = { @SecurityRequirement(name = "JWT") }
    )
    @Parameter(name = "jobId", description = "이미지 인증 작업 ID", required = true,
            schema = @Schema(type = "string"))
    @ApiResponse(
            responseCode = "200",
            description = "SSE 스트림 시작",
            content = @Content(mediaType = "text/event-stream")
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(response = SseEmitter.class, description = "SSE 연결 성공"),
            errors = {
                    @SwaggerApiFailedResponse(value = ExceptionType.ACCESS_DENIED, description = "USER 권한이 필요합니다.")
            }
    )
    @GetMapping("/analyze/{jobId}/events")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    SseEmitter getVerificationEvents(@PathVariable String jobId);
}
