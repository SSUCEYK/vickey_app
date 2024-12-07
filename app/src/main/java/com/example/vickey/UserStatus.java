package com.example.vickey;

public class UserStatus {
    private boolean subscribed; // Match the JSON key "subscribed"

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }
}
