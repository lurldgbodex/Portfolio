package tech.sgcor.portfolio.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.sgcor.portfolio.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
