package com.example.vickey.api.dto;

public class LoginResponse {
    private String userId;
    private String username;
    private String email;
    private String profilePictureUrl;
    private boolean isSubscribed;


    public LoginResponse(String userId, String username, String email, String profilePictureUrl, boolean isSubscribed) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.profilePictureUrl = profilePictureUrl;
        this.isSubscribed = isSubscribed;
    }

    // Getters and Setters

    public boolean isSubscribed() {
        return isSubscribed;
    }

    public void setSubscribed(boolean subscribed) {
        isSubscribed = subscribed;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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