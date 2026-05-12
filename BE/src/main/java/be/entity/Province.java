package be.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "provinces")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Province {

    @Id
    private Integer id;

    private String name;

    @OneToMany(mappedBy = "province")
    private List<District> districts;
}