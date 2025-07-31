package avengers.lion.place.domain;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/*
Google Geocoding API 호출용 장소명 보관
 */
@Entity
@NoArgsConstructor
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

    @Column(name = "selection_count")
    private int selectionCount;
}
