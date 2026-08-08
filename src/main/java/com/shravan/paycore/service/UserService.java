package com.shravan.paycore.service;

import com.shravan.paycore.dto.RegisterUserRequest;
import com.shravan.paycore.dto.UserResponse;
import com.shravan.paycore.entity.User;
import com.shravan.paycore.enums.Role;
import com.shravan.paycore.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse registerUser(RegisterUserRequest request) {

        // Create User Entity
        User user = new User();

        // DTO -> Entity Mapping
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        // Server-controlled fields
        user.setWalletBalance(0.0);
        user.setRole(Role.USER);
        user.setVerified(false);
        user.setCreatedAt(LocalDateTime.now());

        // Save User
        User savedUser = userRepository.save(user);

        // Entity -> DTO Mapping
        UserResponse response = new UserResponse();
        response.setId(savedUser.getId());
        response.setName(savedUser.getName());
        response.setEmail(savedUser.getEmail());

        return response;
    }
}