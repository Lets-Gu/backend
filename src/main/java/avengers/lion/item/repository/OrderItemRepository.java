package avengers.lion.item.repository;



import avengers.lion.item.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;



public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {



}
