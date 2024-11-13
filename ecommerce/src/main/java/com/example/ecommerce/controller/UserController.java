package com.example.ecommerce.controller;

import com.example.ecommerce.exception.EmailAlreadyExistsException;
import com.example.ecommerce.model.User;
import com.example.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Validated  // Enable validation in the controller class
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Object> signUp(@Valid @RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);

            // Success response with customer ID
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Signup successful!");
            response.put("customerId", registeredUser.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (EmailAlreadyExistsException ex) {
            // Email already exists error
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }
}
