package avengers.lion.auth.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRedisImpl implements RefreshTokenRepository {

    private final StringRedisTemplate redis;

    /*
    리프레시 토큰 레디스에 저장
     */
    public void saveRefreshToken(Long userId, String refreshToken){
        String key = "refreshToken:" + userId;
        redis.opsForValue().set(key, refreshToken, Duration.ofDays(7));
    }

    /*
    리프레시 토큰 조회
     */
    public Optional<String> findRefreshToken(Long userId){
        String key = "refreshToken:" + userId;
        String token = redis.opsForValue().get(key);
        return Optional.ofNullable(token);
    }

    public void deleteRefreshToken(Long userId){
        redis.delete("refreshToken:" + userId);
    }

}
