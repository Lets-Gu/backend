package avengers.lion.review.repository;

import avengers.lion.member.domain.QMember;
import avengers.lion.mission.domain.QCompletedMission;
import avengers.lion.mission.domain.QMission;
import avengers.lion.review.domain.QReview;
import avengers.lion.review.domain.Review;
import avengers.lion.review.domain.SortType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewQueryRepositoryImpl implements ReviewQueryRepository{

    private final JPAQueryFactory jpa;

    @Override
    public List<Review> findAllReviewByMissionId(Long missionId, Long cursorId, int limit, SortType sortType) {
        QMission m= QMission.mission;
        QCompletedMission cm = QCompletedMission.completedMission;
        QReview r = QReview.review;
        QMember member = QMember.member;
        BooleanBuilder where = new BooleanBuilder()

                .and(r.completedMission.mission.id.eq(missionId));
        if (cursorId != null) {
            where.and(sortType.equals(SortType.ASC) ? r.id.lt(cursorId) : r.id.gt(cursorId));
        }
        OrderSpecifier<?> orderSpecifier = sortType.equals(SortType.ASC) ? r.id.asc() : r.id.desc();

        return jpa.selectFrom(r)
                .join(r.completedMission,cm).fetchJoin()
                .join(cm.mission,m).fetchJoin()
                .join(r.member, member).fetchJoin()
                .where(where)
                .orderBy(orderSpecifier)
                .limit(limit)
                .fetch();
    }

    @Override
    public List<Review> findWrittenPage(Long memberId, Long cursorId, int limit, SortType sortType) {
        QReview r= QReview.review;
        BooleanBuilder where = new BooleanBuilder()
                .and(r.member.id.eq(memberId));
        if (cursorId != null) {
            where.and(sortType.equals(SortType.ASC) ? r.id.lt(cursorId) : r.id.gt(cursorId));
        }
        OrderSpecifier<?> orderSpecifier = sortType.equals(SortType.ASC) ? r.id.asc() : r.id.desc();
        QCompletedMission cm = QCompletedMission.completedMission;
        QMission m = QMission.mission;
        return jpa.selectFrom(r)
                .join(r.completedMission, cm).fetchJoin()
                .join(cm.mission, m).fetchJoin()
                .where(where)
                .orderBy(orderSpecifier)
                .limit(limit)
                .fetch();
    }

}
