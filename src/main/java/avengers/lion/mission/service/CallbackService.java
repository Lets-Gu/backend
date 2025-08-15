package avengers.lion.mission.service;

import avengers.lion.global.exception.BusinessException;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.mission.dto.request.FastApiCallbackRequest;
import avengers.lion.mission.dto.VerificationEvent;
import avengers.lion.mission.domain.CompletedMission;
import avengers.lion.mission.domain.Mission;
import avengers.lion.mission.repository.CompletedMissionRepository;
import avengers.lion.mission.repository.MissionRepository;
import avengers.lion.member.domain.Member;
import avengers.lion.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /*
    콜백 처리 메소드
     */
    public void processCallback(String jobId, FastApiCallbackRequest request) {
        try {
            switch (request.eventType()) {
                case "progress" -> handleProgressCallback(jobId);
                case "completed" -> handleCompletedCallback(jobId, request);
                case "failed" -> handleFailedCallback(jobId, request);
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

        boolean verified = request.verified() != null ? request.verified() : false;

        if (verified) {
            // 인증 성공 시 DB에 저장 (이미지는 이미 Cloudinary에 업로드됨)
            try {
                saveCompletedMission(metadata);
                eventService.sendEvent(jobId, VerificationEvent.completed(jobId, true, metadata.imageUrl()));
                metadataCacheService.removeMetadata(jobId);
            } catch (Exception e) {
                eventService.sendEvent(jobId, VerificationEvent.error(jobId));
                cleanupService.cleanupFailedVerification(jobId);
            }
        } else {
            // 인증 실패 시 업로드된 이미지 삭제
            cleanupService.cleanupFailedVerification(jobId);
            eventService.sendEvent(jobId, VerificationEvent.completed(jobId, false, null));
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