package com.example.vickey.signup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vickey.MainActivity;
import com.example.vickey.R;
import com.example.vickey.api.ApiClient;
import com.example.vickey.api.ApiService;
import com.example.vickey.api.dto.LoginRequest;
import com.example.vickey.api.dto.LoginResponse;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginWithEmailActivity extends AppCompatActivity {

    private EditText email_edit_text;
    private EditText password_edit_text;
    private Button login_btn;
    private FirebaseAuth auth;
    private TextView sign_up_text;
    private ApiService apiService;
    private final String TAG = "LoginWithEmailActivity";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_email);

        email_edit_text = findViewById(R.id.email_edit_text);
        password_edit_text = findViewById(R.id.passwordEditText);
        login_btn = findViewById(R.id.login_btn);
        auth = FirebaseAuth.getInstance();
        sign_up_text = findViewById(R.id.sign_up_text);

        apiService = ApiClient.getApiService(getApplicationContext()); // 싱글톤 ApiService 사용


        login_btn.setOnClickListener(v -> loginUser());
        //check uid, pw 기능 추가 필요
        
        sign_up_text.setOnClickListener(v -> {
            startActivity(new Intent(LoginWithEmailActivity.this, SignupWithEmailActivity.class));
        });
    }

    private void loginUser() {
        String email = email_edit_text.getText().toString();
        String password = password_edit_text.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.get_input_email_pw), Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                // 로그인 성공
                if (task.isSuccessful()) {
                    Toast.makeText(LoginWithEmailActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                    String uid = auth.getCurrentUser().getUid();
                    Log.d(TAG, "loginUser: uid="+ uid);

                    // 사용자 상태 저장
                    saveLoginSession("email", uid);

                    // DB 저장
                    updateUserDB(new LoginRequest(uid, email, password));

                } else {
                    // 로그인 실패
                    Toast.makeText(LoginWithEmailActivity.this, getString(R.string.login_fail) + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void saveLoginSession(String loginMethod, String userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("login_method", loginMethod);
        editor.putBoolean("isLoginned", true);
        editor.putString("userId", userId);
        editor.apply();
        Log.d(TAG, "saveLoginSession: saved: " + loginMethod + ", " + userId);
    }

    private void updateUserDB(LoginRequest loginRequest){
        // 로그인 성공 시 DB 업데이트 후 결제 처리 코드 호출
        apiService.loginWithEmail(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    //로그인(DB 저장) 성공 시 처리
                    LoginResponse loginResponse = response.body();
                    Log.d(TAG, "onResponse: " + loginResponse);

                    //테스트용
//                    loginResponse.setSubscribed(true);

                    //DB 저장 이후 결제 처리
                    if (loginResponse.isSubscribed()) {
                        // 결제 완료 -> 메인 페이지로 이동
                        Intent intent = new Intent(LoginWithEmailActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        // 결제 미완료 -> 구독 선택 화면으로 이동
                        Intent intent = new Intent(LoginWithEmailActivity.this, SubscriptionActivity.class);
                        startActivity(intent);
                    }

                    Log.d(TAG, "onResponse: user.isSubscribed=" + loginResponse.isSubscribed());
                    finish();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // 네트워크 오류 처리
            }
        });
    }
}
