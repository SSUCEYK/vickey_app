package com.example.vickey;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.vickey.databinding.ActivityMainBinding;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // 툴바
        Toolbar toolbar = findViewById(R.id.search_bar);
        toolbar.setTitle(R.string.app_name); // 제목 변경

        // NavController 초기화
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // onDestinationChanged를 사용해 Toolbar 초기화
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {

            // Toolbar를 Fragment에 따라 변경하며 관리
            if (destination.getId() == R.id.navigation_home) {
                setSupportActionBar(toolbar);
                toolbar.setVisibility(View.VISIBLE);
            } else if (destination.getId() == R.id.navigation_mylist) {
                toolbar.setVisibility(View.VISIBLE);
            }
            else {
                toolbar.setVisibility(View.GONE);
            }
        });
//        getHashKey();
    }

    public Toolbar getToolbar() {
        return findViewById(R.id.search_bar);
    }

    // BottomNavigationView 제어를 위한 메서드 추가
    public void setBottomNavVisibility(int visibility) {
        binding.navView.setVisibility(visibility);
    }

    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }

}