package avengers.lion.mission.repository;

import avengers.lion.mission.domain.CompletedMission;
import avengers.lion.mission.domain.ReviewStatus;
import avengers.lion.review.repository.CompletedMissionQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompletedMissionRepository extends JpaRepository<CompletedMission, Long>, CompletedMissionQueryRepository {
    Long countByMemberIdAndReviewStatus(Long memberId, ReviewStatus status);
}
