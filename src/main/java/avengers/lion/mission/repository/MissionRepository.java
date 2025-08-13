package avengers.lion.mission.repository;

import avengers.lion.mission.domain.Mission;
import avengers.lion.mission.domain.MissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {

    @Query("SELECT DISTINCT m FROM Mission m LEFT JOIN FETCH m.completedMissions cm " +
            "LEFT JOIN FETCH cm.member " +
            "WHERE m.status = :missionStatus")
    List<Mission> findAllByMissionStatusWithCompletedMissions(@Param("missionStatus") MissionStatus missionStatus);
}
