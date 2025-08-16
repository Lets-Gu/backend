package avengers.lion.mission.service;

import avengers.lion.mission.dto.response.UploadUrlResponse;
import avengers.lion.mission.dto.request.VerifyImageRequest;
import avengers.lion.mission.dto.response.VerifyImageResponse;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MissionVerifyService {

    private final MetadataCacheService metadataCacheService;
    private final CloudinaryPreSignedService cloudinaryPreSignedService;
    private final VerificationCleanupService cleanupService;
    private final WebClient webClient;
    
    @Value("${app.fast-api.url}")
    private String fastApiUrl;

    @Value("${app.public-base-url}")
    private String publicBaseUrl;

    /*
    1단계: 이미지 업로드용 Pre-signed URL 생성
    클라이언트가 Cloudinary로 직접 업로드
     */
    public UploadUrlResponse generateUploadUrl(Long missionId) {
        try {
            log.info("Generating upload URL for missionId: {}", missionId);
            // 임시 키 생성 (실제 인증 시 새로 생성됨)
            String tempKey = "temp-" + metadataCacheService.generateJobId();
            log.info("Temp key generated: {}", tempKey);
            // Cloudinary Pre-signed URL 생성
            CloudinaryPreSignedService.CloudinaryUploadInfo uploadInfo = 
                cloudinaryPreSignedService.generatePreSignedUrl(tempKey);

            return new UploadUrlResponse(
                uploadInfo.uploadUrl(),
                uploadInfo.publicId(),
                "LetsGGu" // Unsigned upload preset (노출 안전)
            );
            
        } catch (Exception e) {
            log.error("Failed to generate upload URL for missionId: {}", missionId, e);
            throw new RuntimeException("업로드 URL 생성 중 오류가 발생했습니다.", e);
        }
    }

    /*
    2단계: 업로드된 이미지로 인증 시작
     */
    public VerifyImageResponse startVerification(Long missionId, VerifyImageRequest request, Long memberId) {
        try {
            //  새로운 jobId 생성
            String jobId = metadataCacheService.generateJobId();

            //  메타데이터 캐시 저장
            metadataCacheService.cacheMetadata(
                jobId, 
                request.imageUrl(), 
                request.uploadKey(),  // publicId
                missionId, 
                memberId
            );
            
            //  FastAPI에 비동기 분석 요청
            callFastApiAsync(jobId, request.imageUrl());

            return new VerifyImageResponse(jobId);
            
        } catch (Exception e) {
            // 실패 시 업로드된 이미지 정리
            if (request.uploadKey() != null) {
                try {
                    cloudinaryPreSignedService.deleteImage(request.uploadKey());
                } catch (Exception deleteError) {
                    log.error("Failed to cleanup image after verification start failure: {}", request.uploadKey(), deleteError);
                }
            }
            
            throw new RuntimeException("인증 시작 중 오류가 발생했습니다.", e);
        }
    }
    


    @Async
    public void callFastApiAsync(String jobId, String imageUrl) {
        try {

            // FastAPI 분석 요청에 추가 정보 포함
            Map<String, Object> requestBody = Map.of(
                "job_id", jobId,
                "image_url", imageUrl,
                "callback_url", publicBaseUrl + "/api/v1/missions/" + jobId + "/callback"
            );

            webClient.post()
                .uri(fastApiUrl + "/analyze")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(
                    result -> log.info("FastAPI call succeeded for jobId: {}", jobId),
                    error -> {
                        log.error("FastAPI call failed for jobId: {}", jobId, error);
                        try {
                            cleanupService.cleanupFailedVerification(jobId);
                        } catch (Exception e) {
                            log.error("Failed to cleanup failed verification: {}", jobId, e);
                        }
                    }
            );
                
        } catch (Exception e) {
            log.error("Failed to call FastAPI for jobId: {}", jobId, e);
        }
    }
}
