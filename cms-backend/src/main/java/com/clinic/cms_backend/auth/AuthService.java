package com.clinic.cms_backend.auth;

import com.clinic.cms_backend.user.Role;
import com.clinic.cms_backend.user.RoleRepository;
import com.clinic.cms_backend.user.User;
import com.clinic.cms_backend.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

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

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String accessToken = jwtService.generateAccessToken(user.getEmail(), user.getRole().getName());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return new LoginResponse(accessToken, refreshToken, user.getEmail(), user.getFullName(), user.getRole().getName());
    }
}