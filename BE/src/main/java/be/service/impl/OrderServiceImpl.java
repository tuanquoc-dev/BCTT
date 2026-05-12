package be.service.impl;

import be.dto.request.CreateOrderItemRequest;
import be.dto.request.CreateOrderRequest;
import be.dto.response.OrderDashboardResponse;
import be.dto.response.OrderItemResponse;
import be.dto.response.OrderResponse;
import be.entity.*;
import be.enums.CommonStatus;
import be.enums.DiscountType;
import be.enums.OrderStatus;
import be.enums.PaymentStatus;
import be.exception.AppException;
import be.exception.ErrorCode;
import be.repository.*;
import be.security.SecurityUtils;
import be.service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    private final ProvinceRepository provinceRepository;

    private final DistrictRepository districtRepository;

    private final WardRepository wardRepository;

    private final ShippingFeeRepository shippingFeeRepository;

    // =====================================================
    // CREATE ORDER
    // =====================================================

    @Override
    @Transactional
    public OrderResponse create(CreateOrderRequest request) {

        // ================= USER =================

        String username =
                SecurityUtils.getCurrentUsername();

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new AppException(ErrorCode.USER_NOT_FOUND));

        // ================= VALIDATE ITEMS =================

        if (request.getItems() == null ||
                request.getItems().isEmpty()) {

            throw new AppException(
                    ErrorCode.ORDER_ITEM_EMPTY
            );
        }

        // ================= ADDRESS =================

        Province province = provinceRepository
                .findById(Integer.valueOf(request.getProvinceId()))
                .orElseThrow(() ->
                        new AppException(ErrorCode.PROVINCE_NOT_FOUND));

        District district = districtRepository
                .findById(Integer.valueOf(request.getDistrictId()))
                .orElseThrow(() ->
                        new AppException(ErrorCode.DISTRICT_NOT_FOUND));

        Ward ward = wardRepository
                .findById(Integer.valueOf(request.getWardId()))
                .orElseThrow(() ->
                        new AppException(ErrorCode.WARD_NOT_FOUND));

        // district thuộc province
        if (!district.getProvince().getId()
                .equals(province.getId())) {

            throw new AppException(
                    ErrorCode.ADDRESS_INVALID
            );
        }

        // ward thuộc district
        if (!ward.getDistrict().getId()
                .equals(district.getId())) {

            throw new AppException(
                    ErrorCode.ADDRESS_INVALID
            );
        }

        // ================= SHIPPING =================

        ShippingFee shippingFeeEntity =
                shippingFeeRepository
                        .findByProvinceId(province.getId())
                        .orElseThrow(() ->
                                new AppException(
                                        ErrorCode.SHIPPING_FEE_NOT_FOUND
                                ));

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;

        // ================= CALCULATE =================

        for (CreateOrderItemRequest item : request.getItems()) {

            if (item.getQuantity() == null ||
                    item.getQuantity() <= 0) {

                throw new AppException(
                        ErrorCode.INVALID_REQUEST
                );
            }

            Product product = productRepository
                    .findById(item.getProductId())
                    .orElseThrow(() ->
                            new AppException(
                                    ErrorCode.PRODUCT_NOT_FOUND
                            ));

            // inactive
            if (product.getStatus() != CommonStatus.ACTIVE){

                throw new AppException(
                        ErrorCode.PRODUCT_NOT_FOUND
                );
            }

            // out stock
            if (product.getStock() <= 0) {

                throw new AppException(
                        ErrorCode.PRODUCT_OUT_OF_STOCK
                );
            }

            // insufficient stock
            if (product.getStock() < item.getQuantity()) {

                throw new AppException(
                        ErrorCode.INSUFFICIENT_STOCK
                );
            }

            BigDecimal finalPrice =
                    calculateFinalPrice(
                            product.getPrice(),
                            product.getDiscount()
                    );

            BigDecimal quantity =
                    BigDecimal.valueOf(item.getQuantity());

            BigDecimal originalTotal =
                    product.getPrice().multiply(quantity);

            BigDecimal finalTotal =
                    finalPrice.multiply(quantity);

// subtotal sau giảm
            subtotal = subtotal.add(finalTotal);

// số tiền được giảm
            discountAmount =
                    discountAmount.add(
                            originalTotal.subtract(finalTotal)
                    );
        }

        BigDecimal shippingFee =
                BigDecimal.valueOf(shippingFeeEntity.getFee());

        BigDecimal totalPrice =
                subtotal.add(shippingFee);

        // ================= CREATE ORDER =================

        Order order = Order.builder()
                .user(user)

                .province(province)
                .district(district)
                .ward(ward)

                .receiverName(request.getReceiverName())
                .phone(request.getPhone())
                .addressDetail(request.getAddressDetail())
                .note(request.getNote())

                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.UNPAID)

                .status(OrderStatus.PENDING)

                .subtotal(subtotal)
                .discountAmount(discountAmount)
                .shippingFee(shippingFee)
                .totalPrice(totalPrice)

                .code(generateOrderCode())

                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())

                .build();

        orderRepository.save(order);

        // ================= CREATE ITEMS =================

        for (CreateOrderItemRequest item : request.getItems()) {

            Product product = productRepository
                    .findById(item.getProductId())
                    .orElseThrow(() ->
                            new AppException(
                                    ErrorCode.PRODUCT_NOT_FOUND
                            ));

            BigDecimal finalPrice =
                    calculateFinalPrice(
                            product.getPrice(),
                            product.getDiscount()
                    );

            BigDecimal itemTotal =
                    finalPrice.multiply(
                            BigDecimal.valueOf(item.getQuantity())
                    );

            OrderItem orderItem = OrderItem.builder()

                    .order(order)
                    .product(product)

                    .productName(product.getName())
                    .thumbnail(product.getThumbnail())

                    .color(product.getColor())
                    .ram(product.getRam())

                    .price(finalPrice)

                    .quantity(item.getQuantity())

                    .totalPrice(itemTotal)

                    .build();

            orderItemRepository.save(orderItem);

            // ================= UPDATE STOCK =================

            product.setStock(
                    product.getStock() - item.getQuantity()
            );

            product.setSoldQuantity(
                    product.getSoldQuantity() + item.getQuantity()
            );

            productRepository.save(product);
        }

        return mapOrder(order);
    }

    // =====================================================
    // MY ORDERS
    // =====================================================

    @Override
    public List<OrderResponse> getMyOrders() {

        String username =
                SecurityUtils.getCurrentUsername();

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new AppException(ErrorCode.USER_NOT_FOUND));

        return orderRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::mapOrder)
                .toList();
    }

    // =====================================================
    // DETAIL
    // =====================================================

    @Override
    public OrderResponse getDetail(String code) {

        String username =
                SecurityUtils.getCurrentUsername();

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new AppException(ErrorCode.USER_NOT_FOUND));

        Order order = orderRepository
                .findByCode(code)
                .orElseThrow(() ->
                        new AppException(ErrorCode.ORDER_NOT_FOUND));

        // chỉ được xem đơn của mình
        if (!order.getUser().getId().equals(user.getId())) {

            throw new AppException(ErrorCode.FORBIDDEN);
        }

        return mapOrder(order);
    }

    // =====================================================
    // MAP ORDER
    // =====================================================

    private OrderResponse mapOrder(Order order) {

        List<OrderItemResponse> items =
                orderItemRepository
                        .findByOrderId(order.getId())
                        .stream()
                        .map(item -> OrderItemResponse.builder()

                                .id(item.getId())

                                .productId(item.getProduct().getId())

                                .productName(item.getProductName())

                                .thumbnail(item.getThumbnail())

                                .color(item.getColor())
                                .ram(item.getRam())

                                .price(item.getPrice())

                                .quantity(item.getQuantity())

                                .totalPrice(item.getTotalPrice())

                                .build())
                        .toList();

        return OrderResponse.builder()

                .id(order.getId())

                .code(order.getCode())

                .receiverName(order.getReceiverName())

                .phone(order.getPhone())

                .addressDetail(order.getAddressDetail())

                .note(order.getNote())

                .provinceName(order.getProvince().getName())

                .districtName(order.getDistrict().getName())

                .wardName(order.getWard().getName())

                .subtotal(order.getSubtotal())

                .discountAmount(order.getDiscountAmount())

                .shippingFee(order.getShippingFee())

                .totalPrice(order.getTotalPrice())

                .paymentMethod(order.getPaymentMethod())

                .paymentStatus(order.getPaymentStatus())

                .status(order.getStatus())

                .createdAt(order.getCreatedAt())

                .items(items)

                .build();
    }

    // =====================================================
    // ADMIN
    // =====================================================

    // =====================================================
    // LIST
    // =====================================================

    @Override
    public Page<OrderResponse> findByCodeContainingAndStatus(
            String keyword,
            OrderStatus status,
            int page,
            int size
    ) {

        Pageable pageable =
                PageRequest.of(page, size);

        Page<Order> orders;

        // status có
        if (status != null) {

            orders =
                    orderRepository
                            .findByCodeContainingIgnoreCaseAndStatusOrderByCreatedAtDesc(
                                    keyword == null ? "" : keyword,
                                    status,
                                    pageable
                            );

        } else {

            orders =
                    orderRepository
                            .findByCodeContainingIgnoreCaseOrderByCreatedAtDesc(
                                    keyword == null ? "" : keyword,
                                    pageable
                            );
        }

        return orders.map(this::mapOrder);
    }

    // =====================================================
    // DETAIL
    // =====================================================

    @Override
    public OrderResponse getDetail(Integer id) {

        Order order = getOrder(id);

        return mapOrder(order);
    }

    // =====================================================
    // CONFIRM
    // =====================================================

    @Override
    public OrderResponse confirm(Integer id) {

        Order order = getOrder(id);

        if (order.getStatus() != OrderStatus.PENDING) {

            throw new AppException(
                    ErrorCode.ORDER_STATUS_INVALID
            );
        }

        order.setStatus(OrderStatus.CONFIRMED);

        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);

        return mapOrder(order);
    }

    // =====================================================
    // REJECT
    // =====================================================

    @Override
    public OrderResponse reject(Integer id) {

        Order order = getOrder(id);

        if (order.getStatus() != OrderStatus.PENDING) {

            throw new AppException(
                    ErrorCode.ORDER_STATUS_INVALID
            );
        }

        restoreStock(order);

        order.setStatus(OrderStatus.REJECTED);

        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);

        return mapOrder(order);
    }

    // =====================================================
    // CANCEL
    // =====================================================

    @Override
    public OrderResponse cancel(Integer id) {

        Order order = getOrder(id);

        if (
                order.getStatus() == OrderStatus.COMPLETED
                        || order.getStatus() == OrderStatus.REJECTED
                        || order.getStatus() == OrderStatus.CANCELLED
        ) {

            throw new AppException(
                    ErrorCode.ORDER_STATUS_INVALID
            );
        }

        restoreStock(order);

        order.setStatus(OrderStatus.CANCELLED);

        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);

        return mapOrder(order);
    }

    // =====================================================
    // SHIPPING
    // =====================================================

    @Override
    public OrderResponse shipping(Integer id) {

        Order order = getOrder(id);

        if (order.getStatus() != OrderStatus.CONFIRMED) {

            throw new AppException(
                    ErrorCode.ORDER_STATUS_INVALID
            );
        }

        order.setStatus(OrderStatus.SHIPPING);

        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);

        return mapOrder(order);
    }

    // =====================================================
    // COMPLETE
    // =====================================================

    @Override
    @Transactional
    public OrderResponse complete(Integer id) {

        Order order = getOrder(id);

        if (order.getStatus() != OrderStatus.SHIPPING) {

            throw new AppException(
                    ErrorCode.ORDER_STATUS_INVALID
            );
        }

        order.setStatus(OrderStatus.COMPLETED);

        order.setPaymentStatus(PaymentStatus.PAID);

        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);

        return mapOrder(order);
    }

    // =====================================================
    // DASHBOARD
    // =====================================================

    @Override
    public OrderDashboardResponse dashboard() {

        return OrderDashboardResponse.builder()

                .totalOrders(
                        orderRepository.count()
                )

                .pendingOrders(
                        orderRepository.countByStatus(OrderStatus.PENDING)
                )

                .confirmedOrders(
                        orderRepository.countByStatus(OrderStatus.CONFIRMED)
                )

                .shippingOrders(
                        orderRepository.countByStatus(OrderStatus.SHIPPING)
                )

                .completedOrders(
                        orderRepository.countByStatus(OrderStatus.COMPLETED)
                )

                .cancelledOrders(
                        orderRepository.countByStatus(OrderStatus.CANCELLED)
                )

                .rejectedOrders(
                        orderRepository.countByStatus(OrderStatus.REJECTED)
                )

                .totalRevenue(
                        orderRepository.getTotalRevenue()
                )

                .build();
    }

    // =====================================================
    // FINAL PRICE
    // =====================================================

    private BigDecimal calculateFinalPrice(
            BigDecimal price,
            Discount discount
    ) {

        if (price == null) {
            return BigDecimal.ZERO;
        }

        if (discount == null) {
            return price;
        }

        // inactive
        if (discount.getStatus() != CommonStatus.ACTIVE) {
            return price;
        }

        LocalDateTime now = LocalDateTime.now();

        // not started
        if (
                discount.getStartDate() != null
                        &&
                        now.isBefore(discount.getStartDate())
        ) {
            return price;
        }

        // expired
        if (
                discount.getEndDate() != null
                        &&
                        now.isAfter(discount.getEndDate())
        ) {
            return price;
        }

        // =====================================================
        // PERCENT
        // =====================================================

        if (discount.getDiscountType() == DiscountType.PERCENT) {

            BigDecimal discountValue =
                    BigDecimal.valueOf(
                            discount.getDiscountValue()
                    );

            BigDecimal discountAmount =
                    price.multiply(discountValue)
                            .divide(
                                    BigDecimal.valueOf(100)
                            );

            // max discount
            if (discount.getMaxDiscount() != null) {

                BigDecimal maxDiscount =
                        BigDecimal.valueOf(
                                discount.getMaxDiscount()
                        );

                if (
                        discountAmount.compareTo(maxDiscount)
                                > 0
                ) {
                    discountAmount = maxDiscount;
                }
            }

            BigDecimal finalPrice =
                    price.subtract(discountAmount);

            return finalPrice.compareTo(BigDecimal.ZERO) < 0
                    ? BigDecimal.ZERO
                    : finalPrice;
        }

        // =====================================================
        // AMOUNT
        // =====================================================

        if (discount.getDiscountType() == DiscountType.AMOUNT) {

            BigDecimal discountAmount =
                    BigDecimal.valueOf(
                            discount.getDiscountValue()
                    );

            BigDecimal finalPrice =
                    price.subtract(discountAmount);

            return finalPrice.compareTo(BigDecimal.ZERO) < 0
                    ? BigDecimal.ZERO
                    : finalPrice;
        }

        return price;
    }

    // =====================================================
    // RESTORE STOCK
    // =====================================================

    private void restoreStock(Order order) {

        for (OrderItem item :
                orderItemRepository.findByOrderId(order.getId())) {

            Product product = item.getProduct();

            product.setStock(
                    product.getStock() + item.getQuantity()
            );

            product.setSoldQuantity(
                    product.getSoldQuantity() - item.getQuantity()
            );

            productRepository.save(product);
        }
    }

    // =====================================================
    // GET ORDER
    // =====================================================

    private Order getOrder(Integer id) {

        return orderRepository
                .findById(id)
                .orElseThrow(() ->
                        new AppException(
                                ErrorCode.ORDER_NOT_FOUND
                        ));
    }

    // =====================================================
    // GENERATE ORDER CODE
    // =====================================================

    private String generateOrderCode() {

        String code;

        do {

            code = "ORD-" +
                    UUID.randomUUID()
                            .toString()
                            .substring(0, 8)
                            .toUpperCase();

        } while (orderRepository.findByCode(code).isPresent());

        return code;
    }
}