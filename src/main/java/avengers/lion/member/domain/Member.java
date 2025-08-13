package avengers.lion.member.domain;

import avengers.lion.global.base.BaseEntity;
import avengers.lion.global.exception.BusinessException;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.mission.domain.CompletedMission;
import avengers.lion.review.domain.Review;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Member extends BaseEntity {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @JsonIgnore
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Column(name = "point", nullable = false)
    private Long point;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompletedMission> completedMissions;

    @Builder
    public Member(String email, String nickname, String password, MemberRole role, String profileImageUrl) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
        this.point = 0L;
    }

    public void buyItemByPoint(Long price){
        if(this.point<price)
            throw new BusinessException(ExceptionType.INSUFFICIENT_POINT);
        this.point-=price;
    }
}
