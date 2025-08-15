package avengers.lion.mission.dto.request;

import jakarta.validation.constraints.NotBlank;

public record VerifyImageRequest(
    @NotBlank
    String uploadImageUrl,

    @NotBlank(message = "이미지 URL은 필수입니다")
    String imageUrl,     // 업로드된 Cloudinary 이미지 URL
    
    @NotBlank(message = "업로드 키는 필수입니다") 
    String uploadKey     // Cloudinary publicId (삭제용)
) {}