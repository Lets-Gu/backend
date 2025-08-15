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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "미션 인증 API", description = "GPS/이미지 인증 플로우 + SSE 이벤트 스트림")
@RequestMapping("/api/v1/missions")
public interface VerificationApi {

    // =========================================================
    // 1) GPS 인증
    // =========================================================
    @Operation(
            summary = "미션 GPS 인증",
            description = """
                    ### 목적
                    사용자의 현재 위치로 미션 지점 도달 여부를 검증합니다.

                    ### 프런트 사용법
                    1. **언제 호출?** 사진 인증 전에, 사용자가 실제 장소에 있는지 확인할 때 호출합니다.
                    2. **헤더**: Authorization: Bearer <JWT>
                    3. **요청 바디**: 단말의 위도/경도(GpsAuthenticationRequest)
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

    // =========================================================
    // 2) 이미지 업로드 URL 발급 (Pre-signed)
    // =========================================================
    @Operation(
            summary = "이미지 업로드 URL 발급",
            description = """
                    ### 목적
                    클라이언트가 **Cloudinary**로 이미지를 직접 올릴 수 있도록 미리 서명된 업로드 정보를 발급합니다.

                    ### 프런트 사용법 (핵심 흐름)
                    1. **언제 호출?** 사진 촬영/선택 후, 서버에 이미지 파일을 보내지 않고 **클라우드에 직접 업로드**하기 위해 먼저 호출합니다.
                    2. **헤더**: Authorization: Bearer <JWT>
                    3. **응답(UploadUrlResponse)**:
                       - uploadUrl: Cloudinary 업로드 엔드포인트
                       - publicId: 이번 업로드에 사용할 키(후속 검증 시 필요)
                       - uploadPreset: Cloudinary unsigned preset 이름 (폼 필드에 같이 전송)
                    4. **그 다음**: 프론트는 
                       - file(Blob/File), upload_preset, public_id 등을 넣어 **uploadUrl**에 POST 업로드합니다.
                    5. **업로드 완료 후**: Cloudinary가 리턴한 **이미지 최종 URL**을 확보해 3단계(이미지 인증 시작)로 이동합니다.

                    ### 주의사항
                    - 이 API는 **파일을 받지 않습니다.** 파일 업로드는 프런트 → Cloudinary로 직접 이뤄집니다.
                    """,
            security = { @SecurityRequirement(name = "JWT") }
    )
    @Parameter(name = "missionId", description = "미션 ID", required = true,
            schema = @Schema(type = "integer", format = "int64"))
    @ApiResponse(responseCode = "200", description = "업로드 URL 발급 성공",
            content = @Content(schema = @Schema(implementation = UploadUrlResponse.class)))
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

    // =========================================================
    // 3) 이미지 인증 시작 (비동기 분석 트리거)
    // =========================================================
    @Operation(
            summary = "이미지 인증 시작(비동기)",
            description = """
                    ### 목적
                    Cloudinary에 올라간 이미지 URL로 **서버 측 분석 작업을 비동기로 시작**합니다.

                    ### 프런트 사용법 (핵심 흐름)
                    1. **언제 호출?** 2단계 업로드가 성공해 **최종 이미지 URL**을 확보한 직후
                    2. **헤더**: Authorization: Bearer <JWT>
                    3. **요청 바디(VerifyImageRequest)**:
                       - imageUrl: Cloudinary에서 반환된 최종 접근 URL
                       - uploadKey: 2단계에서 받은 publicId (정리/추적용)
                    4. **응답(VerifyImageResponse)**: jobId (분석 작업 식별자)
                    5. **그 다음**: 4단계 SSE 구독(/analyze/{jobId}/events)으로 진행 상황/결과를 실시간 수신
                    ### 실패 시 처리
                    - 이 단계에서 실패하면 서버가 업로드 이미지를 정리(삭제)할 수 있으니, 프런트에서 별도 정리 불필요.
                    """,
            security = { @SecurityRequirement(name = "JWT") }
    )
    @Parameter(name = "missionId", description = "미션 ID", required = true,
            schema = @Schema(type = "integer", format = "int64"))
    @ApiResponse(responseCode = "200", description = "이미지 인증 시작",
            content = @Content(schema = @Schema(implementation = VerifyImageResponse.class)))
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(response = VerifyImageResponse.class, description = "이미지 인증 시작(비동기 분석 트리거)"),
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

    // =========================================================
    // 4) SSE 이벤트 스트림 구독
    // =========================================================
    @Operation(
            summary = "SSE 구독(인증 진행/완료 이벤트)",
            description = """
                    ### 목적
                    3단계에서 받은 jobId로 **실시간 인증 진행 상황/결과**를 받습니다.

                    ### 프런트 사용법
                    1. **언제 구독?** 3단계 응답(jobId) 수신 직후
                    2. **요청**: GET /api/v1/missions/analyze/{jobId}/events
                    3. **헤더**: Authorization: Bearer <JWT) — SSE에서도 인증 필요
                    4. **응답**: text/event-stream (이벤트명: "verification")
                    5. **이벤트 Payload(요약)**:
                       - eventType: STARTED | PROGRESS | COMPLETED | FAILED | ERROR
                       - jobId: 작업 식별자
                       - imageUrl: (COMPLETED & verified=true일 때) 저장된 최종 이미지 URL
                       - 기타 서버가 보내는 진행 관련 필드
                    ### 연결/종료 정책
                    - 첫 연결 시 서버가 STARTED를 1회 보내 연결 확인
                    - COMPLETED/FAILED/ERROR 수신 시 서버가 스트림을 종료합니다.
                    - 타임아웃/네트워크 오류 시 프런트는 **재연결**하면 됩니다(동일 jobId 사용).
                    """,
            security = { @SecurityRequirement(name = "JWT") }
    )
    @Parameter(name = "jobId", description = "이미지 인증 작업 ID", required = true,
            schema = @Schema(type = "string"))
    @ApiResponse(responseCode = "200", description = "SSE 스트림 시작",
            content = @Content(mediaType = "text/event-stream"))
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
