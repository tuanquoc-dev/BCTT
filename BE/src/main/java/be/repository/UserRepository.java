package be.repository;

import be.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
    @Query("""
SELECT u FROM User u
JOIN FETCH u.role r
LEFT JOIN FETCH r.permissions
WHERE u.username = :username
""")
    Optional<User> findByUsernameWithRole(String username);

    Optional<User> findByEmail(String email);
    @Query("""
    SELECT u FROM User u
    WHERE (:keyword IS NULL OR 
           LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
           LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
           LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))
""")
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);

}