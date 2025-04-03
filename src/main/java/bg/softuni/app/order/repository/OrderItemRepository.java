package bg.softuni.app.order.repository;

import bg.softuni.app.order.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {


    List<OrderItem> findAllByOrderId(UUID orderId);

    List<OrderItem> findByOrderId(UUID orderId);


}




