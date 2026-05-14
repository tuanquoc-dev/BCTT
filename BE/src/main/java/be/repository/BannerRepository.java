package be.repository;

import be.entity.Banner;
import be.enums.CommonStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BannerRepository extends JpaRepository<Banner, Integer> {

    Page<Banner> findByNameContainingIgnoreCaseAndStatus(
            String keyword,
            CommonStatus status,
            Pageable pageable
    );

    Page<Banner> findByNameContainingIgnoreCase(
            String keyword,
            Pageable pageable
    );

    Page<Banner> findByStatus(
            CommonStatus status,
            Pageable pageable
    );

    Page<Banner> findByPositionAndStatus(
            String position,
            CommonStatus status,
            Pageable pageable
    );

    Page<Banner> findByPositionAndTypeAndStatus(
            String position,
            String type,
            CommonStatus status,
            Pageable pageable
    );

    @Query("SELECT COALESCE(MAX(b.sortOrder), 0) FROM Banner b")
    Integer findMaxSortOrder();

    @Modifying
    @Query("UPDATE Banner b SET b.sortOrder = b.sortOrder + 1 WHERE b.sortOrder BETWEEN :start AND :end")
    void incrementRange(int start, int end);

    @Modifying
    @Query("UPDATE Banner b SET b.sortOrder = b.sortOrder - 1 WHERE b.sortOrder BETWEEN :start AND :end")
    void decrementRange(int start, int end);
}