package avengers.lion.review.repository;

import avengers.lion.mission.domain.CompletedMission;
import avengers.lion.mission.domain.QCompletedMission;
import avengers.lion.mission.domain.ReviewStatus;
import avengers.lion.review.domain.SortType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CompletedMissionQueryRepositoryImpl implements CompletedMissionQueryRepository {

    private final JPAQueryFactory jpa;

    @Override
    public List<CompletedMission> findUnwrittenPage(Long memberId, Long cursorId, int limitPlusOne, SortType sortType) {
        QCompletedMission cm = QCompletedMission.completedMission;
        BooleanBuilder where = new BooleanBuilder()
                .and(cm.member.id.eq(memberId))
                .and(cm.reviewStatus.eq(ReviewStatus.INACTIVE));

        if (cursorId != null) {
            where.and(sortType.equals(SortType.ASC) ? cm.id.gt(cursorId) : cm.id.lt(cursorId));
            System.out.println("DEBUG: cursorId=" + cursorId + ", sortType=" + sortType);  
            if (sortType.equals(SortType.ASC)) {   
                System.out.println("DEBUG: Using cm.id > " + cursorId);
                where.and(cm.id.gt(cursorId));     
            } else { 
                System.out.println("DEBUG: Using cm.id < " + cursorId); 
                where.and(cm.id.lt(cursorId));
            } 
        }
        OrderSpecifier<?> orderSpecifier = sortType.equals(SortType.ASC) ? cm.id.asc() : cm.id.desc();
        return jpa.selectFrom(cm)
                .where(where)
                .orderBy(orderSpecifier)
                .limit(limitPlusOne)
                .fetch();
    }
}
