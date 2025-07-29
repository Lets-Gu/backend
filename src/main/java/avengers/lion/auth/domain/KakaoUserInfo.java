package avengers.lion.auth.domain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class KakaoUserInfo {

    public static final String KAKAO_ACCOUNT = "kakao_account";
    public static final String PROFILE = "profile";
    public static final String PROPERTIES = "properties";
    public static final String EMAIL = "email";
    public static final String NICKNAME = "nickname";
    public static final String PROFILE_IMAGE = "profile_image_url";

    // 카카오 프로바이더에서 받은 원본 사용자 정보 맵
    private Map<String, Object> attributes;

    public KakaoUserInfo(Map<String, Object> attributes){
        this.attributes=attributes;
    }

    // 이메일 추출 메소드
    public String getEmail() {
        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<Map<String, Object>> typeReferencer = new TypeReference<Map<String, Object>>() {};

        Object kakaoAccount = attributes.get(KAKAO_ACCOUNT);
        if (kakaoAccount == null) return null;
        
        Map<String, Object> account = objectMapper.convertValue(kakaoAccount, typeReferencer);
        return (String) account.get(EMAIL);
    }

    // 닉네임 추출 메소드
    public String getNickname() {
        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<Map<String, Object>> typeReferencer = new TypeReference<Map<String, Object>>() {};

        Object kakaoAccount = attributes.get(KAKAO_ACCOUNT);
        if (kakaoAccount == null) return null;
        
        Map<String, Object> account = objectMapper.convertValue(kakaoAccount, typeReferencer);
        Object profile = account.get(PROFILE);
        if (profile == null) return null;
        
        Map<String, Object> profileMap = objectMapper.convertValue(profile, typeReferencer);
        return (String) profileMap.get(NICKNAME);
    }

    // 프로필 이미지 추출 메소드
    public String getProfileImageUrl() {
        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<Map<String, Object>> typeReferencer = new TypeReference<Map<String, Object>>() {};

        Object kakaoAccount = attributes.get(KAKAO_ACCOUNT);
        if (kakaoAccount == null) return null;
        
        Map<String, Object> account = objectMapper.convertValue(kakaoAccount, typeReferencer);
        Object profile = account.get(PROFILE);
        if (profile == null) return null;
        
        Map<String, Object> profileMap = objectMapper.convertValue(profile, typeReferencer);
        return (String) profileMap.get(PROFILE_IMAGE);
    }
}
