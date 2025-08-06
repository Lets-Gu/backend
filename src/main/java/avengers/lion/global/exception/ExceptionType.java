package avengers.lion.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ExceptionType {
    // Common
    UNEXPECTED_SERVER_ERROR(INTERNAL_SERVER_ERROR,"C001","예상치 못한 서버 오류가 발생했습니다."),
    BINDING_ERROR(BAD_REQUEST,"C002","요청 데이터 변환 과정에서 오류가 발생했습니다."),
    ESSENTIAL_FIELD_MISSING_ERROR(NO_CONTENT , "C003","필수 필드를 누락했습니다."),
    INVALID_ENDPOINT(NOT_FOUND, "C004", "잘못된 API URI로 요청했습니다."),
    INVALID_HTTP_METHOD(METHOD_NOT_ALLOWED, "C005","잘못된 HTTP 메서드로 요청했습니다."),

    // Member
    MEMBER_NOT_FOUND(NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),


    //Auth
    EXPIRED_REFRESH_TOKEN(UNAUTHORIZED,"A001","리프레시 토큰이 만료되었습니다."),
    INVALID_REFRESH_TOKEN(UNAUTHORIZED, "A002","리프레시 토큰이 유효하지 않습니다"),

    // place
    PLACE_FORMAT_ERROR(INTERNAL_SERVER_ERROR,"P001","장소 변환 에러"),
    GOOGLE_API_ERROR(INTERNAL_SERVER_ERROR,"P002", "구글 API 호출 에러"),
    PLACE_NOT_FOUND(NOT_FOUND,"P003","장소를 찾을 수 없습니다."),

    // Mission
    COMPLETED_MISSION_NOT_FOUND(NOT_FOUND, "M001","완료된 미션을 찾을 수 없습니다."),
    MISSION_NOT_FOUND(NOT_FOUND, "M002", "미션을 찾을 수 없습니다."),
    GPS_AUTH_FAILED(UNAUTHORIZED,"M003", "GPS 인증에 실패하였습니다."),
    
    // Review
    ACCESS_DENIED(FORBIDDEN, "R001", "접근 권한이 없습니다."),
    REVIEW_ALREADY_EXISTS(CONFLICT, "R002", "이미 작성된 리뷰입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
