package com.example.vickey;

import android.app.Application;

import com.navercorp.nid.NaverIdLoginSDK;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        NaverIdLoginSDK.INSTANCE.initialize(this, getString(R.string.naver_client_id), getString(R.string.naver_client_secret), getString(R.string.naver_client_name));// Naver SDK 초기화

    }
}
