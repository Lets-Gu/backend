package avengers.lion.review.dto;

import java.util.List;

public record ReviewResponse(Long unWrittenReviewCount,
                             Long writtenReviewCount,
                             List<UnWrittenReviewResponse> unWrittenReviewResponse,
                             List<WrittenReviewResponse> writtenReviewResponse
    )
{ }
