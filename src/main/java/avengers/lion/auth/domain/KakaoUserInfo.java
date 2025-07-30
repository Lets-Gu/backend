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

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> TYPE_REFERENCE =
            new TypeReference<Map<String, Object>>() {};
    
    // 카카오 프로바이더에서 받은 원본 사용자 정보 맵
    private Map<String, Object> attributes;

    public KakaoUserInfo(Map<String, Object> attributes){
        this.attributes=attributes;
    }
    private Map<String, Object> getKakaoAccount() {
        Object kakaoAccount = attributes.get(KAKAO_ACCOUNT);
        if (kakaoAccount == null) return null;

        try{
            return OBJECT_MAPPER.convertValue(kakaoAccount, TYPE_REFERENCE);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Map<String, Object> getProfile(Map<String, Object> kakaoAccount) {
        if(kakaoAccount==null) return null;

        Object profile = kakaoAccount.get(PROFILE);
        if (profile == null) return null;

        try{
            return OBJECT_MAPPER.convertValue(profile, TYPE_REFERENCE);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // 이메일 추출 메소드
    public String getEmail() {
        
      Map<String, Object> kakaoAccount = getKakaoAccount();
      if(kakaoAccount==null) return null;
      return (String) kakaoAccount.get(EMAIL);
    }

    // 닉네임 추출 메소드
    public String getNickname() {
       
      Map<String, Object> kakaoAccount = getKakaoAccount();
      Map<String, Object> profile = getProfile(kakaoAccount);
      if(profile==null) return null;
      return (String) profile.get(NICKNAME);
    }

    // 프로필 이미지 추출 메소드
    public String getProfileImageUrl() {
      
        Map<String, Object> kakaoAccount = getKakaoAccount();
        Map<String, Object> profile = getProfile(kakaoAccount);
        if(profile==null) return null;
        return (String) profile.get(PROFILE_IMAGE);
    }
}
