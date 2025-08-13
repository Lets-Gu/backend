package avengers.lion.item.repository;


import avengers.lion.item.domain.ItemCategory;
import avengers.lion.item.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("""
        SELECT sum(oi.count) FROM OrderItem oi
        JOIN oi.item i
        JOIN oi.orders o
        JOIN o.member m
        WHERE m.id = :memberId AND i.itemCategory = :itemCategory
        """)
    Long getTotalCountByMemberIdAndItemCategory(@Param("memberId") Long memberId, @Param("itemCategory") ItemCategory itemCategory);


}
