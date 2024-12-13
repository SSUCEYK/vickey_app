package com.example.vickey.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vickey.api.models.User;

public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<User> user = new MutableLiveData<>();

    public LiveData<User> getUser() {
        return user;
    }

    public void setUser(User newUser) {
        user.setValue(newUser);
    }
}
