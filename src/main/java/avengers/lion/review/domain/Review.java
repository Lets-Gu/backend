package avengers.lion.review.domain;

import avengers.lion.global.base.BaseEntity;
import avengers.lion.member.Member;
import avengers.lion.mission.domain.CompletedMission;
import avengers.lion.mission.domain.Mission;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Review extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "completed_mission_id")
    private CompletedMission completedMission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id")
    private Mission mission;

    @Builder
    public Review(String content, String imageUrl, Member member, CompletedMission completedMission, Mission mission) {
        this.content = content;
        this.imageUrl = imageUrl;
        this.member = member;
        this.completedMission = completedMission;
        this.mission = mission;
    }
}
