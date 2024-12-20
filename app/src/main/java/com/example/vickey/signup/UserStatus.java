package com.example.vickey.signup;

// 사용자의 구독 상태 체크
public class UserStatus {
    private boolean subscribed; // Match the JSON key "subscribed"

    public boolean isSubscribed() {
        return this.subscribed;
    }

    public boolean getSubscribed() {return this.subscribed;}

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }
}
