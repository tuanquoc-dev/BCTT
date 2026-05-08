package be.repository;

import be.entity.Discount;
import be.enums.CommonStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountRepository extends JpaRepository<Discount, Integer> {

    boolean existsByCode(String code);

    Page<Discount> findByCodeContainingIgnoreCase(
            String code,
            Pageable pageable
    );

    Page<Discount> findByCodeContainingIgnoreCaseAndStatus(
            String code,
            CommonStatus status,
            Pageable pageable
    );

    Page<Discount> findByStatus(
            CommonStatus status,
            Pageable pageable
    );
}