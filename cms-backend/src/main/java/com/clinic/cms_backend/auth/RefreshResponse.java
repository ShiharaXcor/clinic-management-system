package com.clinic.cms_backend.auth;

public class RefreshResponse {

    private String accessToken;

    public RefreshResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
}