package com.shravan.paycore.service;
import com.shravan.paycore.dto.LoginRequest;
import com.shravan.paycore.dto.RegisterUserRequest;
import com.shravan.paycore.dto.UserResponse;
import com.shravan.paycore.entity.User;
import com.shravan.paycore.enums.Role;
import com.shravan.paycore.exception.InvalidCredentialsException;
import com.shravan.paycore.exception.UserNotFoundException;
import com.shravan.paycore.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {
    public UserResponse loginUser(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid email or password"));
        System.out.println("Email: " + request.getEmail());
        System.out.println("Stored hash: " + user.getPassword());

        System.out.println(
                "Password matches: " +
                        passwordEncoder.matches(
                                request.getPassword(),
                                user.getPassword()
                        )
        );
        if(!passwordEncoder.matches(request.getPassword(),user.getPassword())){
            throw new InvalidCredentialsException("Invalid email or password");
        }
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());

        return response;

    }

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse registerUser(RegisterUserRequest request) {

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setWalletBalance(0.0);
        user.setRole(Role.USER);
        user.setVerified(false);
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        UserResponse response = new UserResponse();
        response.setId(savedUser.getId());
        response.setName(savedUser.getName());
        response.setEmail(savedUser.getEmail());

        return response;
    }
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());

        return response;
    }
}