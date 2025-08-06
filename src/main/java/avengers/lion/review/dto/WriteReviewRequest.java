package avengers.lion.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WriteReviewRequest(
        @NotNull
        Long completedMissionId,
        @NotBlank(message = "content를 입력해주세요.")
        String content
)
{}
