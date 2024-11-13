package com.example.ecommerce.controller;

import com.example.ecommerce.exception.EmailAlreadyExistsException;
import com.example.ecommerce.model.User;
import com.example.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class SignupController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody User user) {
        try {
            User savedUser = userService.registerUser(user);

            // Success response with customer ID
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Signup successful!");
            response.put("customerId", savedUser.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (EmailAlreadyExistsException ex) {
            // Email already exists response
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}