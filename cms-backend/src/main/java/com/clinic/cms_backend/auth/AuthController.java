package com.clinic.cms_backend.auth;

import com.clinic.cms_backend.user.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Public - anyone can self-register as a PATIENT
    @PostMapping("/register")
    public ResponseEntity<?> registerPatient(@Valid @RequestBody RegisterRequest request) {
        User user = authService.registerPatient(request);
        return ResponseEntity.ok().body(toResponse(user));
    }

    // Admin-only - creates staff accounts (DOCTOR, RECEPTIONIST, BILLING, ADMIN)
    @PostMapping("/staff")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerStaff(@Valid @RequestBody RegisterRequest request) {
        User user = authService.registerStaff(request);
        return ResponseEntity.ok().body(toResponse(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshRequest request) {
        RefreshResponse response = authService.refresh(request);
        return ResponseEntity.ok(response);
    }

    private RegisterResponse toResponse(User user) {
        return new RegisterResponse(user.getId(), user.getEmail(), user.getFullName(), user.getRole().getName());
    }

    public record RegisterResponse(Long id, String email, String fullName, String role) {
    }
}