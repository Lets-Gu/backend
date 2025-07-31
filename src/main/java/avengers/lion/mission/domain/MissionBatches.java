package avengers.lion.mission.domain;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/*
2주마다 새로운 미션 세트를 그룹화 -> 미션의 생명주기 관리
 */
@Entity
@NoArgsConstructor
public class MissionBatches {

    @Id @GeneratedValue
    @Column(name = "mission_batch_id", nullable = false)
    private Long missionBatchId;

    @Column(name = "batch_name", nullable = false)
    private String batchName;

    @Column(name = "batch_start_date", nullable = false)
    private LocalDateTime batchStartDate;

    @Column(name = "batch_end_date", nullable = false)
    private LocalDateTime batchEndDate;

    @Column(name = "batch_status", nullable = false)
    private BatchStatus batchStatus;

    @OneToMany(mappedBy = "missionBatches")
    private List<Mission> missions;
}
