package be.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ward {

    @Id
    private Integer id;

    private String name;

    // ================= RELATION =================

    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;
}