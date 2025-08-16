package avengers.lion.mission.dto.request;

import avengers.lion.mission.dto.VerificationEventType;
import com.fasterxml.jackson.annotation.JsonProperty;

/*
Fast Api로부터 받아오는 dto
 */
public record FastApiCallbackRequest(
        @JsonProperty("job_id") String jobId,
        @JsonProperty("event_type") VerificationEventType eventType  // "progress" | "completed" | "failed"
) {
}