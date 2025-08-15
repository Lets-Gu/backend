package avengers.lion.mission.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

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



    /**
     * 매 시간마다 고아 temp 이미지 정리
     */
    @Scheduled(fixedRate = 3600000) // 1시간마다
    public void cleanupOrphanedTempImages() {
        log.info("Starting scheduled cleanup of orphaned temp images");
        
        CompletableFuture.runAsync(() -> {
            try {
                // 1. TTL 만료로 정리가 필요한 이미지들 처리
                cleanupExpiredImages();
                
                // 2. Cloudinary Admin API를 사용해 temp 폴더의 오래된 이미지들 조회 및 삭제
                // 현재는 로그만 남김 (Admin API 구현 필요)
                log.info("Scheduled cleanup completed - Admin API implementation needed");
                
            } catch (Exception e) {
                log.error("Failed to cleanup orphaned temp images", e);
            }
        });
    }
    
    /**
     * TTL 만료로 인한 정리 대상 이미지들 처리
     */
    private void cleanupExpiredImages() {
        try {
            // Redis에서 정리 대상 키들을 스캔하여 처리
            // 실제 구현 시 Redis SCAN 명령 사용
            log.info("Cleaning up TTL expired images - implementation needed");
            
        } catch (Exception e) {
            log.error("Failed to cleanup expired images", e);
        }
    }
}