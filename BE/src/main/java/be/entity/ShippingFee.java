package be.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shipping_fees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ================= RELATION =================

    @ManyToOne
    @JoinColumn(name = "province_id")
    private Province province;

    // ================= BASIC =================

    private Double fee;
}