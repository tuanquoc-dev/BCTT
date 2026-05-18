package be.repository;

import be.entity.OrderItem;
import be.entity.Product;
import be.entity.User;
import be.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

    List<OrderItem> findByOrderId(Integer orderId);

    boolean existsByOrderUserIdAndProductIdAndOrderStatus(
            Integer userId,
            Integer productId,
            OrderStatus status
    );
}