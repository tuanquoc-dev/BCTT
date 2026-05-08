package be.repository;

import be.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Integer> {
    List<Image> findByProductId(Integer productId);

    long countByProductId(Integer productId);
}
