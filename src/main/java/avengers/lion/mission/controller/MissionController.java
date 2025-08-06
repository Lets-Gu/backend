package avengers.lion.mission.controller;

import avengers.lion.global.response.ResponseBody;
import avengers.lion.global.response.ResponseUtil;
import avengers.lion.mission.dto.GpsAuthenticationRequest;
import avengers.lion.mission.dto.MissionResponse;
import avengers.lion.mission.dto.MissionReviewResponse;
import avengers.lion.mission.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/missions")
public class MissionController {

    private final MissionService missionService;

    /*
    미션하러가기    전체조회
     */
    @GetMapping
    @PreAuthorize( "hasRole('ROLE_USER')")
    public ResponseEntity<ResponseBody<List<MissionResponse>>> getAllMissions(){
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(missionService.getAllMissions()));
    }

    /*
    미션에 대한 리뷰 전체조회
     */
    @GetMapping("/{missionId}/reviews")
    public ResponseEntity<ResponseBody<List<MissionReviewResponse>>> getMissionReviews(@PathVariable Long missionId){
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(missionService.getMissionReviews(missionId)));
    }

    /*
     미션 인증하기  -> gps 인증
     */
    @PostMapping("/{missionId}/gps")
    public ResponseEntity<ResponseBody<Void>> gpsAuthentication(@PathVariable Long missionId, @RequestBody GpsAuthenticationRequest gpsAuthenticationRequest){
        missionService.GpsAuthentication(missionId, gpsAuthenticationRequest);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse());
    }
    //TODO: 사진 인증  SSE 구현

}
