package avengers.lion.review.domain;

import avengers.lion.global.base.BaseEntity;
import avengers.lion.member.Member;
import avengers.lion.mission.domain.CompletedMission;
import avengers.lion.mission.domain.Mission;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Review extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "title", nullable = false)
    private String title;

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
}
