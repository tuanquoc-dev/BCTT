package be.entity;

import be.enums.OrderStatus;
import be.enums.PaymentMethod;
import be.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ================= RELATION =================

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "discount_id")
    private Discount discount;

    @ManyToOne
    @JoinColumn(name = "province_id")
    private Province province;

    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;

    @ManyToOne
    @JoinColumn(name = "ward_id")
    private Ward ward;

    // ================= ORDER INFO =================

    @Column(unique = true)
    private String code;

    private String receiverName;

    private String phone;

    private String addressDetail;

    private String note;

    // ================= PRICE =================

    @Column(nullable = false)
    private BigDecimal subtotal;

    @Column(nullable = false)
    private BigDecimal shippingFee;

    @Column(nullable = false)
    private BigDecimal discountAmount;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    // ================= PAYMENT =================

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    // ================= STATUS =================

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // ================= AUDIT =================

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // ================= ITEMS =================

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems;
}