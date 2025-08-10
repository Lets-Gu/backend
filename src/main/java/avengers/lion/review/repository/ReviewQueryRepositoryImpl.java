package avengers.lion.review.repository;

import avengers.lion.review.domain.QReview;
import avengers.lion.review.domain.Review;
import avengers.lion.review.domain.SortType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewQueryRepositoryImpl implements ReviewQueryRepository{

    private final JPAQueryFactory jpa;

    @Override
    public List<Review> findWrittenPage(Long memberId, Long cursorId, int limit, SortType sortType) {
        QReview r= QReview.review;
        BooleanBuilder where = new BooleanBuilder()
                .and(r.member.id.eq(memberId));
        if (cursorId != null) {
            where.and(sortType.equals(SortType.ASC) ? r.id.gt(cursorId) : r.id.lt(cursorId));
        }
        OrderSpecifier<?> orderSpecifier = sortType.equals(SortType.ASC) ? r.id.asc() : r.id.desc();
        return jpa.selectFrom(r)
                .where(where)
                .orderBy(orderSpecifier)
                .limit(limit)
                .fetch();
    }
}
