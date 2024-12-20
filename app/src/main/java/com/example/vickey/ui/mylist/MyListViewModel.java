package com.example.vickey.ui.mylist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vickey.api.ApiService;
import com.example.vickey.api.models.CheckWatched;
import com.example.vickey.api.models.Like;

import java.util.List;


public class MyListViewModel extends ViewModel {

    private final String TAG = "MyListFragment";
    private ApiService apiService;
    private MutableLiveData<List<Like>> likesLiveData = new MutableLiveData<>();
    private MutableLiveData<List<CheckWatched>> historyLiveData = new MutableLiveData<>();

    public void setApiService(ApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<List<Like>> getLikes() {
        return likesLiveData;
    }

    public LiveData<List<CheckWatched>> getHistory() {
        return historyLiveData;
    }

}