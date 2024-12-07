package com.example.vickey.signup;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vickey.R;
import com.example.vickey.api.ApiClient;
import com.example.vickey.api.ApiService;
import com.example.vickey.api.models.User;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Response;

public class SignupWithEmailActivity extends AppCompatActivity {

    private EditText email_edit_text;
    private EditText password_edit_text;
    private Button signup_btn;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_with_email);

        email_edit_text = findViewById(R.id.email_edit_text);
        password_edit_text = findViewById(R.id.password_edit_text);
        signup_btn = findViewById(R.id.signup_btn);
        auth = FirebaseAuth.getInstance();

        signup_btn.setOnClickListener(v -> signupUser());
    }

    private void signupUser() {
        String email = email_edit_text.getText().toString();
        String password = password_edit_text.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "이메일과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // 가입 성공 후 처리
                        String uid = auth.getCurrentUser().getUid();
                        saveUserToServer(email, uid);

                        Toast.makeText(SignupWithEmailActivity.this, "가입 성공", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, LoginWithEmailActivity.class));
                        finish(); // Activity 종료
                    } else {
                        // 가입 실패 처리
                        Toast.makeText(SignupWithEmailActivity.this, "가입 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToServer(String email, String uid) {
        ApiService apiService = ApiClient.getApiService(this);

        User user = new User(uid, email); // User 객체 생성
        apiService.registerUser(user).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SignupWithEmailActivity.this, "서버에 사용자 저장 완료", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignupWithEmailActivity.this, "서버 저장 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(SignupWithEmailActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
