package avengers.lion.mission.dto;

public enum VerificationEventType {
    STARTED,     // 분석 시작
    PROGRESS,    // 진행률 업데이트
    COMPLETED,   // 성공적으로 완료
    FAILED,      // 분석 실패
    ERROR        // 시스템 오류
}