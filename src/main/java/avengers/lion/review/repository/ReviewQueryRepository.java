package avengers.lion.review.repository;

import avengers.lion.review.domain.Review;
import avengers.lion.review.domain.SortType;

import java.util.List;

public interface ReviewQueryRepository {

    /*
    전체조회 : 최신순
     */
    List<Review> findWrittenPage(Long memberId, Long cursorId, int limit, SortType sortType);
}
