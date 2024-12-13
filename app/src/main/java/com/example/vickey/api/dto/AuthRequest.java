package com.example.vickey.api.dto;

public class AuthRequest {
    private String accessToken;

    public AuthRequest(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
