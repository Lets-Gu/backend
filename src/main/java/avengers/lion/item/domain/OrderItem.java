package avengers.lion.item.domain;

import avengers.lion.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class OrderItem extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @Column(name = "count", nullable = false)
    private int count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Orders orders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Enumerated(EnumType.STRING)
    private OrderItemStatus orderItemStatus;

    @Builder
    public OrderItem(int count, Orders orders, Item item, OrderItemStatus orderItemStatus){
        this.count = count;
        this.orders = orders;
        this.item = item;
        this.orderItemStatus = orderItemStatus;
    }
    
    // 비즈니스 로직 메소드들
    public void useItem() {
        if (this.orderItemStatus != OrderItemStatus.UNUSED) {
            throw new IllegalStateException("이미 사용된 아이템입니다.");
        }
        this.orderItemStatus = OrderItemStatus.CONSUMED;
    }
    
    public void expireItem() {
        if (this.orderItemStatus == OrderItemStatus.CONSUMED) {
            throw new IllegalStateException("이미 사용된 아이템은 만료처리할 수 없습니다.");
        }
        this.orderItemStatus = OrderItemStatus.EXPIRED;
    }
    
    public boolean isUsable() {
        return this.orderItemStatus == OrderItemStatus.UNUSED;
    }
    
    public int getItemTotalPrice() {
        return this.item.getPrice() * this.count;
    }
}
