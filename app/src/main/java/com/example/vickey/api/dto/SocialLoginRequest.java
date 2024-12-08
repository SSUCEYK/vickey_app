package com.example.vickey.api.dto;

public class SocialLoginRequest {
//    private String username;
    private String email;
    private String profilePictureUrl; // 선택 사항

    public SocialLoginRequest(String email, String profilePictureUrl) {
//        this.username = username;
        this.email = email;
        this.profilePictureUrl = profilePictureUrl;
    }

    // Getters and Setters
//
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}