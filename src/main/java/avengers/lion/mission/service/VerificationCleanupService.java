package avengers.lion.mission.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationCleanupService {

    private final CloudinaryPreSignedService cloudinaryService;
    private final MetadataCacheService metadataCacheService;

    /**
     * 인증 실패 시 즉시 정리
     */
    public void cleanupFailedVerification(String jobId) {
        MetadataCacheService.VerificationMetadata metadata = metadataCacheService.getMetadata(jobId);
        if (metadata != null) {
            log.info("Cleaning up failed verification: jobId={}, publicId={}", jobId, metadata.publicId());
            
            // 1. Cloudinary에서 이미지 삭제
            try {
                cloudinaryService.deleteImage(metadata.publicId());
            } catch (Exception e) {
                log.error("Failed to delete Cloudinary image: {}", metadata.publicId(), e);
            }
            // 2. 메타데이터 캐시 정리
            metadataCacheService.removeMetadata(jobId);
        }
    }
}