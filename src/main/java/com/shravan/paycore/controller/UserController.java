package com.shravan.paycore.controller;

import com.shravan.paycore.dto.LoginRequest;
import com.shravan.paycore.dto.LoginResponse;
import com.shravan.paycore.dto.RegisterUserRequest;
import com.shravan.paycore.dto.UserResponse;
import com.shravan.paycore.entity.User;
import com.shravan.paycore.service.AuthenticatedUserService;
import com.shravan.paycore.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AuthenticatedUserService authenticatedUserService;

    public UserController(
            UserService userService,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.userService = userService;
        this.authenticatedUserService = authenticatedUserService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(
            @Valid @RequestBody RegisterUserRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.registerUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(
            @Valid @RequestBody LoginRequest request
    ) {

        return ResponseEntity
                .ok(userService.loginUser(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id
    ) {

        return ResponseEntity
                .ok(userService.getUserById(id));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {

        User user = authenticatedUserService.getCurrentUser();

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());

        return ResponseEntity.ok(response);
    }
}