package avengers.lion.review.service;

import avengers.lion.global.base.PageResult;
import avengers.lion.global.exception.BusinessException;
import avengers.lion.global.exception.ExceptionType;

import avengers.lion.mission.domain.CompletedMission;
import avengers.lion.mission.domain.ReviewStatus;
import avengers.lion.mission.repository.CompletedMissionRepository;
import avengers.lion.review.domain.Review;
import avengers.lion.review.domain.SortType;
import avengers.lion.review.dto.*;
import avengers.lion.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CompletedMissionRepository completedMissionRepository;

    /** 오버뷰: 카운트 + 각 6개 프리뷰 + 각 next 커서 */
    public OverviewResponse getOverview(Long memberId, int previewLimit) {

        // 가져오고자 하는 데이터의 개수
       

        // 작성 가능 프리뷰 (DESC)
        List<CompletedMission> uw = completedMissionRepository.findUnwrittenPage(memberId, null, previewLimit+1, SortType.DESC);
        boolean uwHasNext = uw.size() > previewLimit;
        // 다음 페이지가 존재하면, size까지 끊기
        if (uwHasNext) uw = uw.subList(0, previewLimit);
        List<UnWrittenReviewResponse> unwritten = UnWrittenReviewResponse.of(uw);
        LocalDateTime uwNextAt = null; Long uwNextId = null;
        //비지 않았다 -> 다음 데이터가 있다
        if (!uw.isEmpty()) { CompletedMission last = uw.getLast(); uwNextAt = last.getCreatedAt(); uwNextId = last.getId(); }

        // 작성한 프리뷰 (전체조회는 최신순만)
        List<Review> wr = reviewRepository.findWrittenPage(memberId,  null, previewLimit+1, SortType.DESC);
        boolean wrHasNext = wr.size() > previewLimit;
        if (wrHasNext) wr = wr.subList(0, previewLimit);
        List<WrittenReviewResponse> written = WrittenReviewResponse.of(wr);
        LocalDateTime wrNextAt = null; Long wrNextId = null;
        if (!wr.isEmpty()) { Review last = wr.getLast(); wrNextAt = last.getCreatedAt(); wrNextId = last.getId(); }

        Long unwrittenCount = completedMissionRepository.countByMemberIdAndReviewStatus(memberId, ReviewStatus.INACTIVE);
        Long writtenCount   = reviewRepository.countByMemberId(memberId);

        return new OverviewResponse(
                unwrittenCount, writtenCount,
                unwritten, new OverviewResponse.PageMeta(uwHasNext, uwNextAt, uwNextId),
                written,   new OverviewResponse.PageMeta(wrHasNext, wrNextAt, wrNextId)
        );
    }


    public PageResult<UnWrittenReviewResponse> getUnwrittenPage(Long memberId,
                                                                Long cursorId,
                                                                int limit,
                                                                SortType sort) {
        

        List<CompletedMission> rows = completedMissionRepository.findUnwrittenPage(memberId, cursorId, limit+1, sort);

        boolean hasNext = rows.size() > limit;
        if (hasNext) rows = rows.subList(0, limit);

        List<UnWrittenReviewResponse> data = UnWrittenReviewResponse.of(rows);
        LocalDateTime nextAt = null; Long nextId = null;
        if (!rows.isEmpty()) { CompletedMission last = rows.getLast(); nextAt = last.getCreatedAt(); nextId = last.getId(); }

        return new PageResult<>(data, hasNext, nextAt, nextId);
    }

    /** 작성한 리뷰 전체조회 (최신순만) */
    public PageResult<WrittenReviewResponse> getWrittenPage(Long memberId,
                                                            Long cursorId,
                                                            int limit) {
        List<Review> rows = reviewRepository.findWrittenPage(memberId, cursorId, limit+1, SortType.DESC);

        boolean hasNext = rows.size() > limit;
        if (hasNext) rows = rows.subList(0, limit);

        List<WrittenReviewResponse> data = WrittenReviewResponse.of(rows);
        LocalDateTime nextAt = null; Long nextId = null;
        if (!rows.isEmpty()) { Review last = rows.getLast(); nextAt = last.getCreatedAt(); nextId = last.getId(); }

        return new PageResult<>(data, hasNext, nextAt, nextId);
    }

    /** 작성한 리뷰 상세조회 (ASC/DESC) */
    public PageResult<WrittenReviewResponse> getWrittenDetailPage(Long memberId,
                                                                  Long cursorId,
                                                                  int limit,
                                                                  SortType sort) {
        
        List<Review> rows = reviewRepository.findWrittenPage(memberId, cursorId, limit+1, sort);


        boolean hasNext = rows.size() > limit;
        if (hasNext) rows = rows.subList(0, limit);

        List<WrittenReviewResponse> data = WrittenReviewResponse.of(rows);
        LocalDateTime nextAt = null; Long nextId = null;
        if (!rows.isEmpty()) { Review last = rows.getLast(); nextAt = last.getCreatedAt(); nextId = last.getId(); }

        return new PageResult<>(data, hasNext, nextAt, nextId);
    }

    /** 작성 가능한 리뷰 작성 */
    @Transactional
    public void writeUnWrittenReview(Long memberId, WriteReviewRequest req) {
        CompletedMission cm = completedMissionRepository.findById(req.completedMissionId())
                .orElseThrow(() -> new BusinessException(ExceptionType.COMPLETED_MISSION_NOT_FOUND));

        if (!cm.getMember().getId().equals(memberId))
            throw new BusinessException(ExceptionType.ACCESS_DENIED);
        if (cm.getReviewStatus().equals(ReviewStatus.ACTIVE))   // ReviewStatus.ACTIVE 체크(프로젝트 enum에 맞게)
            throw new BusinessException(ExceptionType.REVIEW_ALREADY_EXISTS);

        Review review = Review.builder()
                .content(req.content())
                .imageUrl(cm.getImageUrl())
                .member(cm.getMember())
                .completedMission(cm)
                .build();

        reviewRepository.save(review);
        cm.updateReviewStatus(avengers.lion.mission.domain.ReviewStatus.ACTIVE);
    }
}
