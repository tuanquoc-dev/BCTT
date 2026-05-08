package be.repository;

import be.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    boolean existsBySlug(String slug);

    @Query("""
        SELECT p FROM Product p
        WHERE
            (:keyword IS NULL OR LOWER(p.name)
                LIKE LOWER(CONCAT('%', :keyword, '%')))

        AND (:brandId IS NULL OR p.brand.id = :brandId)

        AND (:categoryId IS NULL OR p.category.id = :categoryId)

        AND (:minPrice IS NULL OR p.price >= :minPrice)

        AND (:maxPrice IS NULL OR p.price <= :maxPrice)

        ORDER BY
            CASE WHEN :sort = 'price_asc'
                THEN p.price END ASC,

            CASE WHEN :sort = 'price_desc'
                THEN p.price END DESC,

            CASE WHEN :sort = 'newest'
                THEN p.createdAt END DESC,

            p.id DESC
    """)
    Page<Product> search(
            @Param("keyword") String keyword,
            @Param("brandId") Integer brandId,
            @Param("categoryId") Integer categoryId,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("sort") String sort,
            Pageable pageable
    );
}