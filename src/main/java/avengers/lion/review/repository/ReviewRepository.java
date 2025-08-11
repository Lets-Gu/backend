package avengers.lion.review.repository;

import avengers.lion.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewQueryRepository {
    Long countByMemberId(Long memberId);

    @Query("""
        SELECT r from Review r
        JOIN r.completedMission cm
        JOIN cm.mission m
        WHERE m.id = :missionId
""")
    List<Review> findAllReviewByMissionId(@Param("missionId") Long missionId);

    @Query("""
        SELECT count(r) FROM Review r
        JOIN r.completedMission cm
        JOIN cm.mission m
        WHERE m.id = :missionId
""")
    Long countReviewByMissionId(Long missionId);
}
