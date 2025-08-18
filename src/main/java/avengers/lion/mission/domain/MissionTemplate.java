package avengers.lion.mission.domain;

import avengers.lion.place.domain.Place;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class MissionTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "template_id", nullable = false)
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "selection_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int selectionCount = 0;

    @Column(name = "last_selection_at")
    private LocalDateTime lastSelectionAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @OneToOne
    @JoinColumn(name = "mission_id", nullable = true)
    private Mission mission;

    public void updateSelectionCount() {
        this.selectionCount++;
        this.lastSelectionAt = LocalDateTime.now();
    }

}
