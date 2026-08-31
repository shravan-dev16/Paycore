package com.shravan.paycore.service;

import com.shravan.paycore.dto.LoginRequest;
import com.shravan.paycore.dto.LoginResponse;
import com.shravan.paycore.dto.RegisterUserRequest;
import com.shravan.paycore.dto.UserResponse;
import com.shravan.paycore.entity.User;
import com.shravan.paycore.entity.Wallet;
import com.shravan.paycore.enums.Role;
import com.shravan.paycore.exception.InvalidCredentialsException;
import com.shravan.paycore.exception.UserNotFoundException;
import com.shravan.paycore.repository.UserRepository;
import com.shravan.paycore.repository.WalletRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class UserService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            WalletRepository walletRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.walletRepository = walletRepository;
    }

    @Transactional
    public UserResponse registerUser(RegisterUserRequest request) {

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole(Role.USER);
        user.setVerified(false);
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        Wallet wallet = new Wallet();

        wallet.setUser(savedUser);
        wallet.setBalance(BigDecimal.ZERO);

        walletRepository.save(wallet);

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

    public LoginResponse loginUser(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid email or password"
                        ));

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        if (!passwordMatches) {
            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        String token = jwtService.generateToken(user.getEmail());

        return new LoginResponse(
                token,
                user.getId()
        );
    }
}