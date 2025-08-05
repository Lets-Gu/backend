package avengers.lion.mission.domain;

import avengers.lion.global.base.BaseEntity;
import avengers.lion.place.domain.Place;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/*
실제 미션 데이터 -> 구글 api + ai로 생성된 미션 정보
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
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
    @Enumerated(EnumType.STRING)
    private MissionStatus status;

    @Column(name = "latitude", precision = 10, scale = 8)
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private BigDecimal longitude;

    @ManyToOne
    @JoinColumn(name = "mission_batch_id", nullable = false)
    private MissionBatches missionBatches;

    public static Mission createFromPlace(Place place, String title, String description, MissionBatches batch) {
        return new Mission(
            null,
            place.getName(),
            title,
            description,
            MissionStatus.ACTIVE,
            place.getLatitude(),
            place.getLongitude(),
            batch
        );
    }
    public void finishMission() {
        this.status = MissionStatus.INACTIVE;
    }
}
