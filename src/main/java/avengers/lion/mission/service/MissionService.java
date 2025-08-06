package avengers.lion.mission.service;

import avengers.lion.global.exception.BusinessException;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.mission.domain.Mission;
import avengers.lion.mission.domain.MissionStatus;
import avengers.lion.mission.dto.GpsAuthenticationRequest;
import avengers.lion.mission.dto.MissionResponse;
import avengers.lion.mission.dto.MissionReviewResponse;
import avengers.lion.mission.repository.CompletedMissionRepository;
import avengers.lion.mission.repository.MissionRepository;
import avengers.lion.review.domain.Review;
import avengers.lion.review.repository.ReviewRepository;
import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionService {

    private static final double THRESHOLD_METERS = 100.0;

    private final MissionRepository missionRepository;
    private final ReviewRepository reviewRepository;
    private final CompletedMissionRepository completedMissionRepository;
    private final Cloudinary cloudinary;

    /*
    미션 전체조회
     */
    public List<MissionResponse> getAllMissions(){
        List<Mission> activeMissions = missionRepository.findAllByMissionStatus(MissionStatus.ACTIVE);

        return activeMissions.stream()
                .map(MissionResponse::from)
                .toList();
    }

    /*
    미션 리뷰조회
     */
    public List<MissionReviewResponse> getMissionReviews(Long missionId){
        List<Review> reviews = reviewRepository.findAllReviewByMissionId(missionId);
        return reviews.stream()
                .map(MissionReviewResponse::from)
                .toList();
    }

    /*
    미션 GPS 인증
     */
    public void GpsAuthentication(Long missionId, GpsAuthenticationRequest gpsAuthenticationRequest) {
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
