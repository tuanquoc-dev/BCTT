package be.repository;

import be.entity.ShippingFee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShippingFeeRepository extends JpaRepository<ShippingFee, Integer> {

    Optional<ShippingFee> findByProvinceId(Integer provinceId);
}