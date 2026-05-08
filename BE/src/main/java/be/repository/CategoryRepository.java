package be.repository;

import be.entity.Category;
import be.enums.CommonStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Page<Category> findByNameContainingIgnoreCaseAndStatus(
            String name,
            CommonStatus status,
            Pageable pageable
    );

    Page<Category> findByNameContainingIgnoreCase(
            String name,
            Pageable pageable
    );

    Page<Category> findByStatus(
            CommonStatus status,
            Pageable pageable
    );
}