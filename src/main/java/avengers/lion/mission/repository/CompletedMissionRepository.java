package avengers.lion.mission.repository;

import avengers.lion.mission.domain.CompletedMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompletedMissionRepository extends JpaRepository<CompletedMission, Long> {

    @Query("SELECT cm FROM CompletedMission cm" +
           " JOIN FETCH cm.mission m" +
           " WHERE cm.member.id = :memberId" +
           " AND cm.reviewStatus = 'INACTIVE'")
    List<CompletedMission> getUnwrittenReviewsByMemberId(@Param("memberId") Long memberId);
}
