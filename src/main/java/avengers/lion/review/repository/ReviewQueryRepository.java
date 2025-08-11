package avengers.lion.review.repository;

import avengers.lion.review.domain.Review;
import avengers.lion.review.domain.SortType;

import java.util.List;

public interface ReviewQueryRepository {

    /*
    미션 페이지 리뷰 전체조회
     */
    List<Review> findAllReviewByMissionId(Long missionId, Long cursorId, int limit, SortType sortType);

    /*
    개인 리뷰 전체조회 : 최신순
     */
    List<Review> findWrittenPage(Long memberId, Long cursorId, int limit, SortType sortType);
}
