package be.repository;

import be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}