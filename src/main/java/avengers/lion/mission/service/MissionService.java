package avengers.lion.mission.service;

import avengers.lion.mission.domain.Mission;
import avengers.lion.mission.domain.MissionStatus;
import avengers.lion.mission.dto.MissionResponse;
import avengers.lion.mission.dto.MissionReviewResponse;
import avengers.lion.mission.repository.MissionRepository;
import avengers.lion.review.domain.Review;
import avengers.lion.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final ReviewRepository reviewRepository;
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
}
