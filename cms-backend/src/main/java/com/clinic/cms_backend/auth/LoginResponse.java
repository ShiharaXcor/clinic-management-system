package com.clinic.cms_backend.auth;

public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private String email;
    private String fullName;
    private String role;

    public LoginResponse(String accessToken, String refreshToken, String email, String fullName, String role) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }
}