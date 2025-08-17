package avengers.lion.mission.service;

import avengers.lion.global.exception.BusinessException;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.mission.dto.VerificationEventType;
import avengers.lion.mission.dto.request.FastApiCallbackRequest;
import avengers.lion.mission.dto.VerificationEvent;
import avengers.lion.mission.domain.CompletedMission;
import avengers.lion.mission.domain.Mission;
import avengers.lion.mission.repository.CompletedMissionRepository;
import avengers.lion.mission.repository.MissionRepository;
import avengers.lion.member.domain.Member;
import avengers.lion.member.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallbackService {

    private final VerificationEventService eventService;
    private final MetadataCacheService metadataCacheService;
    private final VerificationCleanupService cleanupService;
    private final CompletedMissionRepository completedMissionRepository;
    private final MissionRepository missionRepository;
    private final MemberRepository memberRepository;
    
    @Value("${app.callback.secret-key}")
    private String secretKey;

    private final ObjectMapper objectMapper;

    /*
    HMAC 서명 검증
     */
    public void verifySignatureOrThrow(String jobId, FastApiCallbackRequest request, String signature) {
        if (signature == null || signature.trim().isEmpty()) {
            log.warn("Missing callback signature for jobId: {}", jobId);
            throw new BusinessException(ExceptionType.FAST_API_DENIED);
        }

        try {
            // 페이로드 생성: jobId + requestBody (JSON)
            String requestJson = objectMapper.writeValueAsString(request);
            String payload = jobId + requestJson;
            
            // HMAC-SHA256 서명 생성
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(signingKey);
            
            byte[] rawHmac = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String expectedSignature = HexFormat.of().formatHex(rawHmac);
            
            // 서명 비교 (타이밍 공격 방지)
            if (!constantTimeEquals(signature, expectedSignature)) {
                log.warn("Invalid callback signature for jobId: {}", jobId);
                throw new BusinessException(ExceptionType.FAST_API_DENIED);
            }
            
            log.debug("Callback signature verified successfully for jobId: {}", jobId);
            
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Failed to verify callback signature for jobId: {}", jobId, e);
            throw new BusinessException(ExceptionType.FAST_API_DENIED);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    타이밍 공격 방지를 위한 상수 시간 비교
     */
    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        if (a.length() != b.length()) return false;
        
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }

    /*
    콜백 처리 메소드
     */
    public void processCallback(String jobId, FastApiCallbackRequest request) {
        try {
            switch (request.eventType()) {
                case PROGRESS -> handleProgressCallback(jobId);
                case COMPLETED -> handleCompletedCallback(jobId, request);
                case FAILED -> handleFailedCallback(jobId, request);
                default -> {
                    eventService.sendEvent(jobId, VerificationEvent.error(jobId));
                }
            }
        } catch (Exception e) {
            eventService.sendEvent(jobId, VerificationEvent.error(jobId));
            // 실패 시 메타데이터 정리 및 이미지 삭제
            cleanupService.cleanupFailedVerification(jobId);
        }
    }

    /*
    진행 중 콜백
     */
    private void handleProgressCallback(String jobId) {
        eventService.sendEvent(jobId, VerificationEvent.progress(jobId));
    }

    /*
    완료 콜백
     */
    private void handleCompletedCallback(String jobId, FastApiCallbackRequest request) {
        MetadataCacheService.VerificationMetadata metadata = metadataCacheService.getMetadata(jobId);
        if (metadata == null) {
            eventService.sendEvent(jobId, VerificationEvent.error(jobId));
            return;
        }

        boolean verified = request.eventType().equals(VerificationEventType.COMPLETED);

        if (verified) {
            // 인증 성공 시 DB에 저장 (이미지는 이미 Cloudinary에 업로드됨)
            try {
                saveCompletedMission(metadata);
                eventService.sendEvent(jobId, VerificationEvent.completed(jobId,  metadata.imageUrl()));
                metadataCacheService.removeMetadata(jobId);
            } catch (Exception e) {
                eventService.sendEvent(jobId, VerificationEvent.error(jobId));
                cleanupService.cleanupFailedVerification(jobId);
            }
        } else {
            // 인증 실패 시 업로드된 이미지 삭제
            cleanupService.cleanupFailedVerification(jobId);
            eventService.sendEvent(jobId, VerificationEvent.completed(jobId, null));
        }
    }

    private void handleFailedCallback(String jobId, FastApiCallbackRequest request) {
        cleanupService.cleanupFailedVerification(jobId);
        eventService.sendEvent(jobId, VerificationEvent.failed(jobId));
    }

    /*
    미션 완료 시 CompletedMission 객체 저장
     */
    @Transactional
    public void saveCompletedMission(MetadataCacheService.VerificationMetadata metadata) {

        Mission mission = missionRepository.findById(metadata.missionId())
            .orElseThrow(() -> new BusinessException(ExceptionType.MISSION_NOT_FOUND));
        
        Member member = memberRepository.findById(metadata.memberId())
            .orElseThrow(() -> new BusinessException(ExceptionType.MEMBER_NOT_FOUND));

        CompletedMission completedMission = CompletedMission.builder()
            .mission(mission)
            .member(member)
            .imageUrl(metadata.imageUrl())
            .build();

        completedMissionRepository.save(completedMission);
    }
}