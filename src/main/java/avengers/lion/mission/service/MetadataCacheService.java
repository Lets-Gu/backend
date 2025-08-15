package avengers.lion.mission.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetadataCacheService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String CACHE_PREFIX = "verification_metadata:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);
    
    public String generateJobId() {
        return UUID.randomUUID().toString();
    }
    
    public void cacheMetadata(String jobId, String imageUrl, String publicId, Long missionId, Long memberId) {
        try {
            VerificationMetadata metadata = new VerificationMetadata(imageUrl, publicId, missionId, memberId);
            String key = CACHE_PREFIX + jobId;
            
            redisTemplate.opsForValue().set(key, metadata, CACHE_TTL);
            log.debug("Cached metadata for jobId: {}", jobId);
            
        } catch (Exception e) {
            log.error("Failed to cache metadata for jobId: {}", jobId, e);
            throw new RuntimeException("메타데이터 캐싱에 실패했습니다.", e);
        }
    }
    
    public VerificationMetadata getMetadata(String jobId) {
        try {
            String key = CACHE_PREFIX + jobId;
            Object cached = redisTemplate.opsForValue().get(key);
            
            if (cached instanceof VerificationMetadata metadata) {
                return metadata;
            }
            
            log.debug("No metadata found for jobId: {}", jobId);
            return null;
            
        } catch (Exception e) {
            log.error("Failed to retrieve metadata for jobId: {}", jobId, e);
            return null;
        }
    }
    
    public void removeMetadata(String jobId) {
        try {
            String key = CACHE_PREFIX + jobId;
            redisTemplate.delete(key);
            log.debug("Removed metadata for jobId: {}", jobId);
        } catch (Exception e) {
            log.error("Failed to remove metadata for jobId: {}", jobId, e);
        }
    }
    

    public record VerificationMetadata(
        String imageUrl,      // Cloudinary 이미지 URL
        String publicId,      // Cloudinary public ID (삭제용)
        Long missionId,
        Long memberId
    ) {}
}