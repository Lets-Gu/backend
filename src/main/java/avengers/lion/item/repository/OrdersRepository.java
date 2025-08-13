package avengers.lion.item.repository;

import avengers.lion.item.domain.ItemCategory;
import avengers.lion.item.domain.OrderItemStatus;
import avengers.lion.item.domain.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    @Query("SELECT DISTINCT o FROM Orders o"+
            " JOIN FETCH o.orderItems i" +
            " JOIN FETCH i.item it" +
            " WHERE o.member.id = :memberId" +
            " AND i.orderItemStatus = :orderItemStatus" +
            " AND it.itemCategory IN :categories"+
            " ORDER BY o.createdAt DESC")
    List<Orders> findOrderItemByMemberIdAndItemStatus(
            @Param("memberId") Long memberId,
            @Param("orderItemStatus") OrderItemStatus orderItemStatus,
            @Param("categories") List<ItemCategory> categories);

    @Query("SELECT COUNT(oi) FROM OrderItem oi" +
            " JOIN oi.item i" +
            " WHERE oi.orders.member.id = :memberId" +
            " AND oi.orderItemStatus = :orderItemStatus" +
            " AND i.itemCategory IN :categories")
    Long countOrderItemsByMemberIdAndItemStatus(
            @Param("memberId") Long memberId,
            @Param("orderItemStatus") OrderItemStatus orderItemStatus,
            @Param("categories") List<ItemCategory> categories);


}
