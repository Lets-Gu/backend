package avengers.lion.mission.controller;

import avengers.lion.global.response.ResponseBody;
import avengers.lion.global.response.ResponseUtil;
import avengers.lion.mission.api.VerificationApi;
import avengers.lion.mission.dto.request.FastApiCallbackRequest;
import avengers.lion.mission.dto.request.GpsAuthenticationRequest;
import avengers.lion.mission.dto.response.UploadUrlResponse;
import avengers.lion.mission.dto.request.VerifyImageRequest;
import avengers.lion.mission.dto.response.VerifyImageResponse;
import avengers.lion.mission.service.CallbackService;
import avengers.lion.mission.service.MissionService;
import avengers.lion.mission.service.MissionVerifyService;
import jakarta.validation.Valid;
import avengers.lion.mission.service.VerificationEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/missions")
@RequiredArgsConstructor
public class VerificationController implements VerificationApi {

    private final MissionVerifyService missionVerifyService;
    private final VerificationEventService verificationEventService;
    private final MissionService missionService;
    private final CallbackService callbackService;

    /*
     미션 인증하기  -> gps 인증
     */
    @PostMapping("/{missionId}/gps")
    @PreAuthorize( "hasAuthority('ROLE_USER')")
    public ResponseEntity<ResponseBody<Void>> gpsAuthentication(@PathVariable Long missionId, @RequestBody GpsAuthenticationRequest gpsAuthenticationRequest){
        missionService.gpsAuthentication(missionId, gpsAuthenticationRequest);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse());
    }

    /*
    1단계: 이미지 업로드용 Pre-signed URL 요청
     */
    @GetMapping("/{missionId}/upload-url")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ResponseBody<UploadUrlResponse>> getUploadUrl(
            @PathVariable Long missionId) {
        
        UploadUrlResponse response = missionVerifyService.generateUploadUrl(missionId);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(response));
    }

    /*
    2단계: 이미지 인증 시작 (업로드된 이미지로)
     */
    @PostMapping("/{missionId}/verify-image")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ResponseBody<VerifyImageResponse>> startVerification(
            @PathVariable Long missionId,
            @Valid @RequestBody VerifyImageRequest request,
            @AuthenticationPrincipal Long memberId) {
        
        VerifyImageResponse response = missionVerifyService.startVerification(missionId, request, memberId);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(response));
    }

    /*
    SSE 스트림 구독
     */
    @GetMapping("/analyze/{jobId}/events")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public SseEmitter getVerificationEvents(@PathVariable String jobId) {
        return verificationEventService.createEventStream(jobId);
    }

    /*
    콜백
     */
    @PostMapping("/{jobId}/callback")
    public ResponseEntity<ResponseBody<Void>> handleFastApiCallback(
            @PathVariable String jobId,
            @Valid @RequestBody FastApiCallbackRequest request,
            @RequestHeader("X-Callback-Signature") String signature) {

        // HMAC 서명 검증
        callbackService.verifySignatureOrThrow(jobId, request, signature);
        // 콜백 처리
        callbackService.processCallback(jobId, request);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse());
    }
}
