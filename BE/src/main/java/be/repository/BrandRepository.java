package be.repository;

import be.entity.Brand;
import be.enums.CommonStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Integer> {

    Page<Brand> findByNameContainingIgnoreCaseAndStatus(
            String name,
            CommonStatus status,
            Pageable pageable
    );

    Page<Brand> findByNameContainingIgnoreCase(
            String name,
            Pageable pageable
    );

    Page<Brand> findByStatus(
            CommonStatus status,
            Pageable pageable
    );

    List<Brand> findByStatusOrderByNameAsc(CommonStatus status);
}
