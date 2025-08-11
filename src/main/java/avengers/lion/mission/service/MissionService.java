package avengers.lion.mission.service;

import avengers.lion.global.exception.BusinessException;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.mission.domain.Mission;
import avengers.lion.mission.domain.MissionStatus;
import avengers.lion.mission.dto.GpsAuthenticationRequest;
import avengers.lion.mission.dto.MissionPreReviewResponse;
import avengers.lion.mission.dto.MissionResponse;
import avengers.lion.mission.dto.MissionReviewResponse;
import avengers.lion.mission.repository.MissionRepository;
import avengers.lion.review.domain.Review;
import avengers.lion.review.domain.SortType;
import avengers.lion.global.base.PageResult;
import avengers.lion.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MissionService {

    private static final double THRESHOLD_METERS = 100.0;

    private final MissionRepository missionRepository;
    private final ReviewRepository reviewRepository;

    /*
    미션 전체조회 프리뷰 -> 처음에 3개
     */
    public List<MissionResponse> getAllMissions(Long memberId){
        List<Mission> activeMissions = missionRepository.findAllByMissionStatusWithCompletedMissions(MissionStatus.ACTIVE);

        // 완료된 미션 ID 집합을 미리 추출하여 O(1) 조회 최적화
        Set<Long> completedMissionIds = activeMissions.stream()
                .flatMap(mission -> mission.getCompletedMissions().stream())
                .filter(completed -> completed.getMember().getId().equals(memberId))
                .map(completed -> completed.getMission().getId())
                .collect(Collectors.toSet());

        return activeMissions.stream()
                .map(mission -> {
                    Boolean isCompleted = completedMissionIds.contains(mission.getId());
                    return MissionResponse.of(mission, isCompleted);
                })
                .toList();
    }

    public MissionPreReviewResponse getMissionPreReviews(Long missionId, SortType sortType){
        List<Review> reviews = reviewRepository.findAllReviewByMissionId(missionId, null, 3, sortType);
        Long count = reviewRepository.countReviewByMissionId(missionId);
        boolean hasNext = reviews.size() > 3;
        if (hasNext) reviews = reviews.subList(0, 3);
        List<MissionReviewResponse> data = reviews.stream()
                .map(MissionReviewResponse::of)
                .toList();
        LocalDateTime nextAt = null; Long nextId = null;
        if (!reviews.isEmpty()) { Review last = reviews.getLast(); nextId = last.getId(); }
        return new MissionPreReviewResponse(count, data, new MissionPreReviewResponse.PageMeta(hasNext, nextId));
    }




    /*
    미션 리뷰조회 스크롤
     */
    public PageResult<MissionReviewResponse> getMissionReviews(Long missionId, Long lastReviewId, int limit, SortType sortType){
        List<Review> reviews = reviewRepository.findAllReviewByMissionId(missionId, lastReviewId, limit, sortType);
        boolean hasNext = reviews.size() > limit;
        if (hasNext) reviews = reviews.subList(0, limit);

        List<MissionReviewResponse> data = reviews.stream()
                .map(MissionReviewResponse::of)
                .toList();

        LocalDateTime nextAt = null; Long nextId = null;
        if (!reviews.isEmpty()) { Review last = reviews.getLast(); nextAt = last.getCreatedAt(); nextId = last.getId(); }

        return new PageResult<>(data, hasNext, nextAt, nextId);
    }

    /*
    미션 GPS 인증
     */
    public void gpsAuthentication(Long missionId, GpsAuthenticationRequest gpsAuthenticationRequest) {
        double userLat = gpsAuthenticationRequest.latitude();
        double userLng = gpsAuthenticationRequest.longitude();
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(()->new BusinessException(ExceptionType.MISSION_NOT_FOUND));
        double dist = calcDistanceMeters(userLat, userLng, mission.getLatitude(), mission.getLongitude());
        if(dist > THRESHOLD_METERS)
            throw new BusinessException(ExceptionType.GPS_AUTH_FAILED);
    }

    private double calcDistanceMeters(
            double lat1, double lng1, double lat2, double lng2
    ) {
        double r = 6_371_000; // 지구 반지름(m)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat/2)*Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng/2)*Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return r * c;
    }
}
