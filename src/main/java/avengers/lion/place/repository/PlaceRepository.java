package avengers.lion.place.repository;

import avengers.lion.place.domain.Place;
import avengers.lion.place.domain.PlaceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

    @Query("SELECT p FROM Place p WHERE p.category = :category " +
        "AND (p.lastSearchDate IS NULL OR p.lastSearchDate < :cutoffDate) " +
        "ORDER BY p.selectionCount ASC, RAND() LIMIT 1")
    Place findByCategoryWithSelectionCriteria(PlaceCategory category, LocalDateTime cutoffDate);
}
