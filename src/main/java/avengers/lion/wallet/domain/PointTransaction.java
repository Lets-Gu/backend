package avengers.lion.wallet.domain;

import avengers.lion.global.base.BaseEntity;
import avengers.lion.member.domain.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class PointTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_transaction_id")
    private Long id;

    @Column(name = "change_amount", nullable = false)
    private int changeAmount;

    @Column(name = "point_type", nullable = false)
    private PointType pointType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public PointTransaction(int changeAmount, Member member) {
        this.changeAmount = changeAmount;
        this.pointType = PointType.ITEM_EXCHANGE;
        this.member = member;
    }
}
