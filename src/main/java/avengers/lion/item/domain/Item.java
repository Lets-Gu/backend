package avengers.lion.item.domain;

import avengers.lion.global.base.BaseEntity;
import avengers.lion.global.exception.BusinessException;
import avengers.lion.global.exception.ExceptionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Item extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "stock_count", nullable = false)
    private int stockCount;

    @Enumerated(EnumType.STRING)
    private ItemCategory itemCategory;

    public void buyItem(int count) {
        if(this.stockCount < count) {
            throw new BusinessException(ExceptionType. STOCK_NOT_AVAILABLE);
        }
        this.stockCount -= count;
    }
}
