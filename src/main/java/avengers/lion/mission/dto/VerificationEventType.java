package avengers.lion.mission.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum VerificationEventType {
    STARTED,     // 분석 시작
    PROGRESS,    // 진행률 업데이트
    COMPLETED,   // 성공적으로 완료
    FAILED,      // 분석 실패
    ERROR;        // 시스템 오류

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static VerificationEventType of(String value) {
        if(value == null) return null;
        return VerificationEventType.valueOf(value.toUpperCase());
    }
    @JsonValue
    public String toValue() {
        return name().toLowerCase();
    }
}