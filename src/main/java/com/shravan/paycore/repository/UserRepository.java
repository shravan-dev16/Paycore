package com.shravan.paycore.repository;

import com.shravan.paycore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}