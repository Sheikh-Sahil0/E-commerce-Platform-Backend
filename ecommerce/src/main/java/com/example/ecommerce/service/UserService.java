package com.example.ecommerce.service;

import com.example.ecommerce.exception.EmailAlreadyExistsException;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Use a secure key for JWT signing
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // JWT expiration time (e.g., 24 hours)
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    public User registerUser(User user) throws EmailAlreadyExistsException {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new EmailAlreadyExistsException("Email is already taken.");
        }

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    public User authenticateUser(String email, String password) throws Exception {
        User user = userRepository.findByEmail(email);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new Exception("Invalid email or password.");
        }
        return user;
    }

    // Generate JWT Token
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .claim("name", user.getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }
}
