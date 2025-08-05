package avengers.lion.mission.repository;

import avengers.lion.mission.domain.BatchStatus;
import avengers.lion.mission.domain.MissionBatches;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MissionBatchRepository extends JpaRepository<MissionBatches, Long> {
    
    @Query("SELECT mb FROM MissionBatches mb WHERE mb.batchStatus = :status ORDER BY mb.batchStartDate DESC")
    MissionBatches findFirstByBatchStatusOrderByBatchStartDateDesc(BatchStatus status);

}