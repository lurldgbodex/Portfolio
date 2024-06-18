package tech.sgcor.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.sgcor.customer.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByConfirmationToken(String token);
    Optional<User> findByResetToken(String token);
}
