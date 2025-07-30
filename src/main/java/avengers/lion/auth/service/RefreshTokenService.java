package avengers.lion.auth.service;

import avengers.lion.auth.repository.RefreshTokenRepository;
import avengers.lion.global.exception.BusinessException;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.global.jwt.TokenDto;
import avengers.lion.global.jwt.TokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;

    public void reissueRefreshToken(Long userId, String refreshToken, HttpServletResponse response){
        // 리프레시 토큰이 유효한지 확인
        if(!tokenProvider.validateToken(refreshToken))
            throw new BusinessException(ExceptionType.INVALID_REFRESH_TOKEN);
        // 레디스에 리프레시 토큰이 존재하지 않으면 로그인 실패
        String redisRefreshToken = refreshTokenRepository.findRefreshToken(userId)
                .orElseThrow(()->new BusinessException(ExceptionType.EXPIRED_REFRESH_TOKEN));
        if(!refreshToken.equals(redisRefreshToken))
            throw new BusinessException(ExceptionType.INVALID_REFRESH_TOKEN);
        // 레디스에 리프레시 토큰이 존재하면, 액세스 토큰과 리프레시 토큰 재발급
        TokenDto tokenDto = tokenProvider.createToken(userId.toString(), "ROLE_USER");
        refreshTokenRepository.deleteRefreshToken(userId);
        refreshTokenRepository.saveRefreshToken(userId, tokenDto.refreshToken());
        response.setHeader("Access-Token", tokenDto.accessToken());
        response.setHeader("Refresh-Token", tokenDto.refreshToken());
    }
}
