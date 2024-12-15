package com.example.vickey.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vickey.api.ApiService;
import com.example.vickey.api.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private boolean isUserLoaded = false;

    public LiveData<User> getUser() {
        return user;
    }

    public void loadUser(String userId, ApiService apiService) {
        if (isUserLoaded) return;

        apiService.getUserProfile(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user.setValue(response.body());
                    isUserLoaded = true;
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // 기본값 설정
                User defaultUser = new User();
                defaultUser.setUserId(userId);
                defaultUser.setUsername("Default User");
                defaultUser.setProfilePictureUrl(""); // 기본 프로필 이미지 URL 설정
                user.setValue(defaultUser);
            }
        });
    }
}
