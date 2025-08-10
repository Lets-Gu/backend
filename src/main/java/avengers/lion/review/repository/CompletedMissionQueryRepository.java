package avengers.lion.review.repository;

import avengers.lion.mission.domain.CompletedMission;
import avengers.lion.review.domain.SortType;

import java.util.List;

public interface CompletedMissionQueryRepository {
    List<CompletedMission> findUnwrittenPage(Long memberId, Long cursorId, int limitPlusOne, SortType sortType);
}
