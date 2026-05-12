package be.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDashboardResponse {

    private Long totalOrders;

    private Long pendingOrders;

    private Long confirmedOrders;

    private Long shippingOrders;

    private Long completedOrders;

    private Long cancelledOrders;

    private Long rejectedOrders;

    private BigDecimal totalRevenue;
}