package be.repository;

import be.entity.Order;
import be.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    Optional<Order> findByCode(String code);

    List<Order> findByUserIdOrderByCreatedAtDesc(Integer userId);


    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Long countByStatus(OrderStatus status);

    @Query("""
        SELECT COALESCE(SUM(o.totalPrice),0)
        FROM Order o
        WHERE o.status = 'COMPLETED'
    """)
    BigDecimal getTotalRevenue();

    Page<Order> findByCodeContainingIgnoreCaseAndStatusOrderByCreatedAtDesc(
            String code,
            OrderStatus status,
            Pageable pageable
    );

    Page<Order> findByCodeContainingIgnoreCaseOrderByCreatedAtDesc(
            String code,
            Pageable pageable
    );
}