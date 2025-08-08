package avengers.lion.mission.domain;

import avengers.lion.member.domain.Member;
import avengers.lion.review.domain.Review;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/*
사용자의 미션 수행 결과 저장
 */
@Entity
@Getter
@NoArgsConstructor
public class CompletedMission {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "completed_mission_id", nullable = false)
    private Long id;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "review_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReviewStatus reviewStatus;

    @OneToMany(mappedBy = "completedMission")
    private List<Review> reviews;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public void updateReviewStatus(ReviewStatus reviewStatus) {
        this.reviewStatus = reviewStatus;
    }
}
