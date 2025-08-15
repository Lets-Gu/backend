package avengers.lion.mission.dto.response;

public record UploadUrlResponse(
    String uploadUrl,    // Cloudinary upload URL
    String uploadKey,    // 임시 업로드 키 (publicId)
    String uploadPreset // Cloudinary upload preset// Cloudinary API key
) {}