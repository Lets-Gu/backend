package avengers.lion.mission.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryPreSignedService {

    private final Cloudinary cloudinary;

    /**
     * Unsigned upload용 파라미터 생성 (프론트가 Cloudinary로 직접 업로드)
     */
    public CloudinaryUploadInfo generatePreSignedUrl(String jobId) {
        try {
            //  저장 경로
            String publicId = "missions/temp/" + jobId + "/" + UUID.randomUUID();

            // 프론트가 업로드할 엔드포인트 (고정 규칙)
            String uploadUrl = "https://api.cloudinary.com/v1_1/" + cloudinary.config.cloudName + "/image/upload";

            // 업로드 후 접근 가능한 예상 URL
            String imageUrl = cloudinary.url()
                    .secure(true)
                    .resourceType("image")
                    .publicId(publicId)
                    .generate();

            log.info("Generated unsigned upload params. jobId={}, publicId={}", jobId, publicId);

            return new CloudinaryUploadInfo(
                    uploadUrl,
                    imageUrl,
                    publicId,
                    null, // signature 불필요
                    0L,   // timestamp 불필요
                    null  // apiKey 불필요
            );

        } catch (Exception e) {
            log.error("Failed to generate unsigned upload params. jobId={}", jobId, e);
            throw new RuntimeException("업로드 파라미터 생성 실패", e);
        }
    }
    
    public void deleteImage(String publicId) {
        try {
            Map result = cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap(
                            "invalidate", true,               // CDN 무효화
                            "resource_type", "image"          // 안전히 명시
                    )
            );
            log.info("Deleted Cloudinary image: {}, result: {}", publicId, result.get("result"));
        } catch (Exception e) {
            log.error("Failed to delete Cloudinary image: {}", publicId, e);
        }
    }
    
    
    public record CloudinaryUploadInfo(
            String uploadUrl,   // 업로드 엔드포인트
            String imageUrl,    // 업로드 후 접근 URL(예상)
            String publicId,    // 저장 식별자 (삭제/이동/DB 기록에 사용)
            String signature,   // 서버 서명 (필수)
            long timestamp,     // 타임스탬프 (필수)
            String apiKey       // 클라이언트 업로드에 필요
    ) {}
}