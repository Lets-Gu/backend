package avengers.lion.item.repository;

import avengers.lion.item.domain.ItemCategory;
import avengers.lion.item.domain.OrderItemStatus;
import avengers.lion.item.domain.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    @Query("SELECT o FROM Orders o"+
            " JOIN FETCH o.orderItems i" +
            " JOIN FETCH i.item it" +
            " WHERE o.member.id = :memberId" +
            " AND i.orderItemStatus = :orderItemStatus" +
            " AND it.itemCategory IN :categories")
    List<Orders> findOrderItemByMemberIdAndItemStatus(
            @Param("memberId") Long memberId,
            @Param("orderItemStatus") OrderItemStatus orderItemStatus,
            @Param("categories") List<ItemCategory> categories);
}
