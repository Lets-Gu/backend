package avengers.lion.mission.domain;

import avengers.lion.global.base.BaseEntity;
import avengers.lion.place.domain.Place;
import avengers.lion.place.domain.PlaceCategory;
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

    @Column(name = "place_category", nullable = false)
    private PlaceCategory placeCategory;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MissionStatus status;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "latitude", columnDefinition = "DECIMAL(10,8)")
    private Double latitude;

    @Column(name = "longitude", columnDefinition = "DECIMAL(11,8)")
    private Double longitude;

    @OneToMany(mappedBy = "mission")
    private List<CompletedMission> completedMissions = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "mission_batch_id", nullable = false)
    private MissionBatches missionBatches;

    public Mission(Long id, String placeName, String title, String address, String description, PlaceCategory placeCategory, MissionStatus status, Double latitude, Double longitude, MissionBatches missionBatches) {
        this.id = id;
        this.placeName = placeName;
        this.title = title;
        this.address = address;
        this.description = description;
        this.placeCategory = placeCategory;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
        this.missionBatches = missionBatches;
    }

    public static Mission createFromPlace(Place place, String title, PlaceCategory placeCategory, String description, MissionBatches batch) {
        return new Mission(
            null,
            place.getName(),
            title,
            place.getAddress(),
            description,
            placeCategory,
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
