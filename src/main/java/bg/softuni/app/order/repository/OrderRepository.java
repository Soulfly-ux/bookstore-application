package bg.softuni.app.order.repository;

import bg.softuni.app.order.model.Order;
import bg.softuni.app.order.model.OrderStatus;
import bg.softuni.app.user.model.User;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {


    List<Order> findByUserAndStatus(User user, OrderStatus orderStatus);
}
