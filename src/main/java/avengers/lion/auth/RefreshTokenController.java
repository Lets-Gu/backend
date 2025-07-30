package avengers.lion.auth;

import avengers.lion.auth.domain.KakaoMemberDetails;
import avengers.lion.auth.service.RefreshTokenService;
import avengers.lion.global.response.ResponseBody;
import avengers.lion.global.response.ResponseUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/refreshToken")
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseBody<Void>> refreshToken(@AuthenticationPrincipal KakaoMemberDetails kakaoMemberDetails, @RequestHeader("Refresh-Token") String refreshToken,
                                                           HttpServletResponse response){
        refreshTokenService.reissueRefreshToken(kakaoMemberDetails.getMemberId(), refreshToken, response);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse());
    }
}
