package avengers.lion.review.repository;

import avengers.lion.mission.domain.ReviewStatus;
import avengers.lion.review.domain.Review;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r"+
           " JOIN FETCH r.completedMission cm " +
           " JOIN FETCH cm.mission m " +
           " WHERE r.member.id = :memberId")
    List<Review> findAllByMemberId(@Param("memberId") Long memberId, Sort sort);


    /*
    해당 미션에 대한 리뷰를 모두 가져옴
     */
    @Query("SELECT r FROM Review r " +
            "JOIN FETCH r.member m " +
            "JOIN FETCH r.completedMission cm " +
            "JOIN FETCH cm.mission mission " +
            "WHERE cm.mission.id = :missionId")
    List<Review> findAllReviewByMissionId(@Param("missionId") Long missionId);


    Long countByMemberId(Long memberId);
}

