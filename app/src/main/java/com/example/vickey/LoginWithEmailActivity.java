package com.example.vickey;

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

import com.example.vickey.APIService;
import com.example.vickey.RetrofitClient;
import com.example.vickey.Subscription;
import com.example.vickey.UserStatus;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;


public class LoginWithEmailActivity extends AppCompatActivity {

    private EditText email_edit_text;
    private EditText password_edit_text;
    private Button login_btn;
    private FirebaseAuth auth;
    private TextView sign_up_text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_with_email);

        email_edit_text = findViewById(R.id.email_edit_text);
        password_edit_text = findViewById(R.id.passwordEditText);
        login_btn = findViewById(R.id.login_btn);
        auth = FirebaseAuth.getInstance();
        sign_up_text = findViewById(R.id.sign_up_text);

        login_btn.setOnClickListener(v -> loginUser());

        sign_up_text.setOnClickListener(v -> {
            startActivity(new Intent(LoginWithEmailActivity.this, SignupWithEmailActivity.class));
        });
    }

    private void loginUser() {
        String email = email_edit_text.getText().toString();
        String password = password_edit_text.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "이메일과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // 로그인 성공
                        String uid = auth.getCurrentUser().getUid(); // Firebase UID 가져오기
                        Toast.makeText(LoginWithEmailActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();

                        // 사용자 상태 저장
                        SharedPreferences sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("login_method", "email");
                        editor.putString("uid", uid); // UID 저장
                        editor.apply();

                        Log.d("FirebaseUID", "User UID: " + uid);


                        // 결제 상태 확인
                        checkSubscriptionStatus(uid);

                    } else {
                        // 로그인 실패
                        Toast.makeText(LoginWithEmailActivity.this, "로그인 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkSubscriptionStatus(String uid) {
        APIService apiService = RetrofitClient.getApiService();

        apiService.getUserStatus(uid).enqueue(new retrofit2.Callback<UserStatus>() {
            @Override
            public void onResponse(Call<UserStatus> call, retrofit2.Response<UserStatus> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserStatus userStatus = response.body();
                    if (userStatus.isSubscribed()) {
                        // 결제 완료 -> 메인 페이지로 이동
                        Intent intent = new Intent(LoginWithEmailActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // 결제 미완료 -> 구독 선택 화면으로 이동
                        Intent intent = new Intent(LoginWithEmailActivity.this, Subscription.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(LoginWithEmailActivity.this, "결제 상태 확인 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserStatus> call, Throwable t) {
                Toast.makeText(LoginWithEmailActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
