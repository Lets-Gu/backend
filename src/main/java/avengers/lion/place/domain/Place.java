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

    @Id @GeneratedValue
    @Column(name = "place_id")
    private Long placeId;

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

    @Column(name = "latitude", precision = 10, scale = 8)
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")

    private BigDecimal longitude;

    @Column(name = "selection_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int selectionCount=0;

    public void setGeocodingResult(BigDecimal latitude, BigDecimal longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
