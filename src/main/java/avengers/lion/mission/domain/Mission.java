package avengers.lion.mission.domain;

import avengers.lion.global.base.BaseEntity;
import avengers.lion.review.domain.Review;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.List;

/*
실제 미션 데이터 -> 구글 api + ai로 생성된 미션 정보
 */
@Entity
@NoArgsConstructor
public class Mission extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "mission_id", nullable = false)
    private Long missionId;

    @Column(name = "place_name", nullable = false)
    private String placeName;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "status", nullable = false)
    private MissionStatus status;

    @ManyToOne
    @JoinColumn(name = "mission_batch_id", nullable = false)
    private MissionBatches missionBatches;

    @OneToMany(mappedBy = "mission")
    private List<Review> reviews;
}
