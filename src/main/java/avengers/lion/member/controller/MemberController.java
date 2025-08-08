package avengers.lion.member.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import avengers.lion.global.response.ResponseBody;
import avengers.lion.global.response.ResponseUtil;
import avengers.lion.member.dto.MyProfileResponse;
import avengers.lion.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    /*
    마이페이지 내 프로필 조회
     */
    @GetMapping("/my-profile")
    public ResponseEntity<ResponseBody<MyProfileResponse>> getMyProfile(@AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(memberService.getMyProfile(memberId)));
    }
}
