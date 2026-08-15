package com.shravan.paycore.repository;
import java.util.Optional;
import com.shravan.paycore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}