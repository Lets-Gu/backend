package avengers.lion.mission.dto.request;

import avengers.lion.mission.dto.VerificationEventType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/*
Fast Api로부터 받아오는 dto
 */
@JsonPropertyOrder({"job_id", "event_type", "verified"})
public record FastApiCallbackRequest(
        @JsonProperty("job_id") String jobId,
        @JsonProperty("event_type") VerificationEventType eventType,  // "progress" | "completed" | "failed"
        @JsonProperty("verified") boolean verified
) {
}