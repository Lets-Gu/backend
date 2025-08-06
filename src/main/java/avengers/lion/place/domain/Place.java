package avengers.lion.place.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Column(name = "last_search_date")
    private LocalDateTime lastSearchDate;

    @Column(name = "season")
    @Enumerated(EnumType.STRING)
    private Season season;

    private String address;

    @Column(name = "latitude", columnDefinition = "DECIMAL(10,8)")
    private Double latitude;

    @Column(name = "longitude", columnDefinition = "DECIMAL(11,8)")
    private Double longitude;

    @Column(name = "selection_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int selectionCount=0;

    public void setGeocodingResult(Double latitude, Double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    public void updateSelectionInfo() {
        this.lastSearchDate = LocalDateTime.now();
        this.selectionCount += 1;
    }
}
