package avengers.lion.member.dto;

import avengers.lion.member.domain.Member;

public record MyProfileResponse(String nickname, String imageUrl, String email) {

    public static MyProfileResponse of(Member member) {
        return new MyProfileResponse(
                member.getNickname(),
                member.getProfileImageUrl(),
                member.getEmail()
        );
    }
}
