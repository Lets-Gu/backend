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

    @Query("""
    SELECT mt FROM MissionTemplate mt
    JOIN mt.place p
    WHERE p.category = :category
      AND (mt.lastSelectionAt IS NULL OR mt.lastSelectionAt < :cutOffDate)
    ORDER BY function('RAND')
""")
    List<MissionTemplate> findRandomByCategory(
            @Param("category") PlaceCategory category,
            @Param("cutOffDate") LocalDateTime cutOffDate
    );
}
