package com.example.ecommerce.controller;

import com.example.ecommerce.exception.EmailAlreadyExistsException;
import com.example.ecommerce.model.User;
import com.example.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    // Signup Endpoint
    @PostMapping("/signup")
    public ResponseEntity<Object> signUp(@RequestBody User user) {
        // Manual validations for empty fields
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Name cannot be empty."));
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Email cannot be empty."));
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Password cannot be empty."));
        }

        // Advanced password strength validation
        if (!user.getPassword().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", "Password must be at least 8 characters long, include uppercase, lowercase, number, and special character."
            ));
        }

        try {
            User registeredUser = userService.registerUser(user);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Signup successful!");
            response.put("customerId", registeredUser.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (EmailAlreadyExistsException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    // Signin Endpoint
    @PostMapping("/signin")
    public ResponseEntity<Object> signIn(@RequestParam String email, @RequestParam String password) {
        try {
            // Authenticate the user
            User user = userService.authenticateUser(email, password);

            // Generate JWT Token
            String token = userService.generateToken(user);

            // Create response for successful signin
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Signin successful!");
            response.put("token", token);
            response.put("userId", user.getId());
            response.put("name", user.getName());

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            // Handle invalid credentials
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}