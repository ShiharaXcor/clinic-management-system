package com.clinic.cms_backend.auth;

import com.clinic.cms_backend.user.Role;
import com.clinic.cms_backend.user.RoleRepository;
import com.clinic.cms_backend.user.User;
import com.clinic.cms_backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${security.lockout.max-attempts}")
    private int maxAttempts;

    @Value("${security.lockout.lockout-duration-minutes}")
    private long lockoutDurationMinutes;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        Role role = roleRepository.findByName(request.getRoleName())
                .orElseThrow(() -> new IllegalArgumentException("Invalid role: " + request.getRoleName()));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(role);

        return userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!user.isEnabled()) {
            throw new IllegalArgumentException("Account is disabled");
        }

        if (user.getLockedUntil() != null) {
            if (user.getLockedUntil().isAfter(LocalDateTime.now())) {
                throw new IllegalArgumentException(
                        "Account is locked. Try again after " + user.getLockedUntil());
            } else {
                // Lock has expired naturally -> reset the counter
                user.setFailedLoginAttempts(0);
                user.setLockedUntil(null);
            }
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            handleFailedLogin(user);
            throw new IllegalArgumentException("Invalid email or password");
        }

        // Successful login -> reset failure tracking
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user.getEmail(), user.getRole().getName());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return new LoginResponse(accessToken, refreshToken, user.getEmail(), user.getFullName(), user.getRole().getName());
    }

    private void handleFailedLogin(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= maxAttempts) {
            user.setLockedUntil(LocalDateTime.now().plusMinutes(lockoutDurationMinutes));
        }

        userRepository.save(user);
    }

    public RefreshResponse refresh(RefreshRequest request) {
        String token = request.getRefreshToken();

        if (jwtService.isTokenExpired(token)) {
            throw new IllegalArgumentException("Refresh token has expired. Please log in again.");
        }

        String email = jwtService.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User no longer exists"));

        if (!user.isEnabled()) {
            throw new IllegalArgumentException("Account is disabled");
        }

        String newAccessToken = jwtService.generateAccessToken(user.getEmail(), user.getRole().getName());
        return new RefreshResponse(newAccessToken);
    }
}