package avengers.lion.mission.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record VerificationEvent(
    String jobId,
    VerificationEventType eventType,// 0-100
    Boolean verified,     // 인증 성공 여부 (COMPLETED 시)// 실패/성공 사유
    String imageUrl       // 최종 업로드된 이미지 URL (COMPLETED 시)
) {
    
    public static VerificationEvent started(String jobId) {
        return new VerificationEvent(jobId, VerificationEventType.STARTED,  null, null);
    }
    
    public static VerificationEvent progress(String jobId) {
        return new VerificationEvent(jobId, VerificationEventType.PROGRESS,   null,  null);
    }
    
    public static VerificationEvent completed(String jobId, boolean verified, String imageUrl) {
        return new VerificationEvent(jobId, VerificationEventType.COMPLETED, verified, imageUrl);
    }
    
    public static VerificationEvent failed(String jobId) {
        return new VerificationEvent(jobId, VerificationEventType.FAILED,  false, null);
    }
    
    public static VerificationEvent error(String jobId) {
        return new VerificationEvent(jobId, VerificationEventType.ERROR,  null, null);
    }
}