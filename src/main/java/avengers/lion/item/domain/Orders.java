package avengers.lion.item.domain;


import avengers.lion.global.base.BaseEntity;
import avengers.lion.member.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Orders extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orders_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "orders")
    private List<OrderItem> orderItems;

    @Builder
    public Orders(Member member, OrderStatus orderStatus){
        this.member = member;
        this.orderStatus = orderStatus;
    }

}
