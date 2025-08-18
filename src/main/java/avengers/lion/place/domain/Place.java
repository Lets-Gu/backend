package avengers.lion.place.domain;

import avengers.lion.mission.domain.MissionTemplate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

/*
Google Geocoding API 호출용 장소명 보관
 */
@Entity
@NoArgsConstructor
@Getter
public class Place {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private PlaceCategory category;

    @Column(name = "season")
    @Enumerated(EnumType.STRING)
    private Season season;

    private String address;

    @Column(name = "latitude", columnDefinition = "DECIMAL(10,8)")
    private Double latitude;

    @Column(name = "longitude", columnDefinition = "DECIMAL(11,8)")
    private Double longitude;

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MissionTemplate> missionTemplates = new ArrayList<>();

    public void setGeocodingResult(Double latitude, Double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
