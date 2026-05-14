package be.service.service;

import be.dto.request.CancelOrderRequest;
import be.dto.request.CreateOrderRequest;
import be.dto.response.OrderDashboardResponse;
import be.dto.response.OrderResponse;
import be.enums.OrderStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {

    OrderResponse create(CreateOrderRequest request);

    List<OrderResponse> getMyOrders();

    OrderResponse getDetail(String code);

    // admin

    Page<OrderResponse> findByCodeContainingAndStatus(
            String keyword,
            OrderStatus status,
            int page,
            int size
    );

    OrderResponse getDetail(Integer id);

    OrderResponse confirm(Integer id);

    OrderResponse reject(Integer id);

    OrderResponse cancel(Integer id);

    OrderResponse shipping(Integer id);

    OrderResponse complete(Integer id);

    OrderDashboardResponse dashboard();

    OrderResponse customerCancel(
            String code,
            CancelOrderRequest request
    );
}