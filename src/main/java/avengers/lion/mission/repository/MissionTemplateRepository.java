package avengers.lion.mission.repository;

import avengers.lion.mission.domain.MissionTemplate;
import avengers.lion.place.domain.PlaceCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface MissionTemplateRepository extends JpaRepository<MissionTemplate, Long> {

      @Query(value = """
        SELECT mt.*
        FROM mission_template mt
        JOIN place p ON p.place_id = mt.place_id
        WHERE p.category = :category
          AND (mt.last_selection_at IS NULL OR mt.last_selection_at < :cutOffDate)
        ORDER BY RAND()
        LIMIT 1
    """, nativeQuery = true)
    Optional<MissionTemplate> findRandomByCategory(
            @Param("category") PlaceCategory category,
            @Param("cutOffDate") LocalDateTime cutOffDate
    );
}
