package com.example.otp.service;

import com.example.otp.dao.UserDao;
import com.example.otp.model.User;
import com.example.otp.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.UUID;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserDao userDao;
    private final JwtUtil jwtUtil;

    public AuthService(UserDao userDao, JwtUtil jwtUtil) {
        this.userDao = userDao;
        this.jwtUtil = jwtUtil;
    }

    public void register(String username, String password, String role) throws SQLException {

        logger.info("Attempting to register user: {}", username);

        if (role.equals("ADMIN") && userDao.adminExists()) {
            logger.warn("Attempt to create second ADMIN blocked");
            throw new RuntimeException("Admin already exists");
        }

        if (userDao.findByUsername(username).isPresent()) {
            logger.warn("User already exists: {}", username);
            throw new RuntimeException("User already exists");
        }

        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());

        User user = new User(
                UUID.randomUUID(),
                username,
                hashed,
                role
        );

        userDao.save(user);

        logger.info("User registered successfully: {}", username);
    }

    public String login(String username, String password) throws SQLException {

        logger.info("Login attempt for user: {}", username);

        User user = userDao.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Login failed. User not found: {}", username);
                    return new RuntimeException("User not found");
                });

        if (!BCrypt.checkpw(password, user.getPasswordHash())) {
            logger.warn("Invalid password attempt for user: {}", username);
            throw new RuntimeException("Invalid password");
        }

        logger.info("User logged in successfully: {}", username);

        return jwtUtil.generateToken(user.getUsername(), user.getRole());
    }
}