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

    @Query("SELECT m FROM Mission m WHERE m.status = :missionStatus")
    List<Mission> findAllByMissionStatus(@Param("missionStatus") MissionStatus missionStatus);
}
