package com.clinic.cms_backend.common;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/api/v1/health")
    public String health() {
        return "CMS backend is running";
    }

    @GetMapping("/api/v1/me")
    public String me(Authentication authentication) {
        return "You are authenticated as: " + authentication.getName() + " | authorities: " + authentication.getAuthorities();
    }

    @GetMapping("/api/v1/admin-only")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminOnly() {
        return "This endpoint is visible only to ADMIN role.";
    }

    @GetMapping("/api/v1/doctor-only")
    @PreAuthorize("hasRole('DOCTOR')")
    public String doctorOnly() {
        return "This endpoint is visible only to DOCTOR role.";
    }
}