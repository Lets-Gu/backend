package avengers.lion.mission.controller;

import avengers.lion.global.response.ResponseBody;
import avengers.lion.global.response.ResponseUtil;
import avengers.lion.mission.api.MissionApi;
import avengers.lion.mission.dto.GpsAuthenticationRequest;
import avengers.lion.mission.dto.MissionPreReviewResponse;
import avengers.lion.mission.dto.MissionResponse;
import avengers.lion.mission.dto.MissionReviewResponse;
import avengers.lion.mission.service.MissionService;
import avengers.lion.review.domain.SortType;
import avengers.lion.global.base.PageResult;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/missions")
public class MissionController implements MissionApi {

    private final MissionService missionService;

    /*
    미션하러가기   전체조회
     */
    @GetMapping
    @PreAuthorize( "hasAuthority('ROLE_USER')")
    public ResponseEntity<ResponseBody<List<MissionResponse>>> getAllMissions(@AuthenticationPrincipal Long memberId){
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(missionService.getAllMissions(memberId)));
    }

    /*
    미션에 대한 리뷰 전체조회 프리뷰
     */
    @GetMapping("/{missionId}/reviews/preview")
    @PreAuthorize( "hasAuthority('ROLE_USER')")
    public ResponseEntity<ResponseBody<MissionPreReviewResponse>> getMissionReviews(
            @PathVariable Long missionId,
            @RequestParam(required = false, defaultValue = "DESC") SortType sortType){
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(missionService.getMissionPreReviews(missionId, sortType)));
    }

    @GetMapping("/{missionId}/reviews/scroll")
    @PreAuthorize( "hasAuthority('ROLE_USER')")
    public ResponseEntity<ResponseBody<PageResult<MissionReviewResponse>>> getMissionReviewsScroll(
            @PathVariable Long missionId,
            @RequestParam(required = false) Long lastReviewId,
            @RequestParam(required = false, defaultValue = "3") @Min(1) @Max(50) int limit,
            @RequestParam(required = false, defaultValue = "DESC") SortType sortType){
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(missionService.getMissionReviews(missionId, lastReviewId, limit, sortType)));
    }

    /*
     미션 인증하기  -> gps 인증
     */
    @PostMapping("/{missionId}/gps")
    @PreAuthorize( "hasAuthority('ROLE_USER')")
    public ResponseEntity<ResponseBody<Void>> gpsAuthentication(@PathVariable Long missionId, @RequestBody GpsAuthenticationRequest gpsAuthenticationRequest){
        missionService.gpsAuthentication(missionId, gpsAuthenticationRequest);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse());
    }
    //TODO: 사진 인증  SSE 구현

}
