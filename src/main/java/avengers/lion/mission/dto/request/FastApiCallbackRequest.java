package avengers.lion.mission.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
Fast Api로부터 받아오는 dto
 */
public record FastApiCallbackRequest(
    @JsonProperty("job_id") String jobId,
    @JsonProperty("event_type") String eventType,  // "progress" | "completed" | "failed"
    Boolean verified    // 인증 성공 여부// 성공/실패 사유
) {
}