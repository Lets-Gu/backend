package avengers.lion.auth.repository;

import java.util.Optional;

public interface RefreshTokenRepository {

    void saveRefreshToken(Long userId, String refreshToken);

    Optional<String> findRefreshToken(Long userId);

    void deleteRefreshToken(Long userId);
}
