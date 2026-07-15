package com.clinic.cms_backend.common;

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
}