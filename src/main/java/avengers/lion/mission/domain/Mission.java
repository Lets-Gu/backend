package avengers.lion.mission.domain;

import avengers.lion.global.base.BaseEntity;
import avengers.lion.place.domain.Place;
import avengers.lion.review.domain.Review;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/*
실제 미션 데이터 -> 구글 api + ai로 생성된 미션 정보
 */
@Entity
@NoArgsConstructor
@Getter
public class Mission extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_id", nullable = false)
    private Long id;

    @Column(name = "place_name", nullable = false)
    private String placeName;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MissionStatus status;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "latitude", precision = 10, scale = 8)
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private BigDecimal longitude;

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "mission_batch_id", nullable = false)
    private MissionBatches missionBatches;

    public Mission(Long id, String placeName, String title, String address, String description, MissionStatus status, BigDecimal latitude, BigDecimal longitude, MissionBatches missionBatches) {
        this.id = id;
        this.placeName = placeName;
        this.title = title;
        this.address = address;
        this.description = description;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
        this.missionBatches = missionBatches;
        this.reviews = new ArrayList<>();
    }

    public static Mission createFromPlace(Place place, String title, String description, MissionBatches batch) {
        return new Mission(
            null,
            place.getName(),
            title,
            place.getAddress(),
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
