package avengers.lion.mission.domain;

import avengers.lion.member.Member;
import avengers.lion.review.domain.Review;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/*
사용자의 미션 수행 결과 저장
 */
@Entity
@NoArgsConstructor
public class CompletedMission {

    @Id @GeneratedValue
    @Column(name = "completed_mission_id", nullable = false)
    private Long CompletedMissionId;

    @Column(name = "member_id", nullable = false)
    private LocalDateTime completedAt;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "completedMission")
    private List<Review> reviews;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

}
