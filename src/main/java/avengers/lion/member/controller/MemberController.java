package avengers.lion.member.controller;

import avengers.lion.member.api.MemberApi;
import avengers.lion.member.dto.UpdateNicknameRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import avengers.lion.global.response.ResponseBody;
import avengers.lion.global.response.ResponseUtil;
import avengers.lion.member.dto.MyProfileResponse;
import avengers.lion.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController implements MemberApi {

    private final MemberService memberService;

    /*
    마이페이지 내 프로필 조회
     */
    @GetMapping("/my-profile")
    public ResponseEntity<ResponseBody<MyProfileResponse>> getMyProfile(@AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(memberService.getMyProfile(memberId)));
    }
    @PutMapping("/my-profile")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ResponseBody<Void>> updateMyProfile(@AuthenticationPrincipal Long memberId, @RequestBody UpdateNicknameRequest updateNicknameRequest) {
        memberService.updateNickname(memberId, updateNicknameRequest);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse());
    }
}

