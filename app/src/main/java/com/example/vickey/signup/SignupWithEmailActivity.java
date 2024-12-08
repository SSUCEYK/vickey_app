package com.example.vickey.signup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vickey.R;
import com.example.vickey.api.ApiClient;
import com.example.vickey.api.ApiService;
import com.example.vickey.api.dto.LoginResponse;
import com.example.vickey.api.dto.SignupRequest;
import com.example.vickey.api.models.User;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupWithEmailActivity extends AppCompatActivity {

    private EditText email_edit_text;
    private EditText password_edit_text;
    private Button signup_btn;
    private FirebaseAuth auth;
    private ApiService apiService;
    private final String TAG = "SignupWithEmailActivity";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_with_email);

        email_edit_text = findViewById(R.id.email_edit_text);
        password_edit_text = findViewById(R.id.password_edit_text);
        signup_btn = findViewById(R.id.signup_btn);
        auth = FirebaseAuth.getInstance();

        apiService = ApiClient.getApiService(getApplicationContext()); // 싱글톤 ApiService 사용


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
                    // 가입 성공
                    if (task.isSuccessful()) {
                        // 가입 성공 후 처리
                        String uid = auth.getCurrentUser().getUid();
                        SignupRequest signupRequest = new SignupRequest(uid, email, password);
                        Call<LoginResponse> call = apiService.signupWithEmail(signupRequest);

                        call.enqueue(new Callback<LoginResponse>() {
                            @Override
                            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                                if (response.isSuccessful()) {
                                    // 회원가입 성공 시 처리
                                    LoginResponse loginResponse = response.body();
                                    Log.d(TAG, "onResponse: " + loginResponse.getUserId()
                                            + ", " + loginResponse.getUsername()
                                            + ", " + loginResponse.getEmail());
                                    
                                    Toast.makeText(SignupWithEmailActivity.this, "가입 성공: 로그인 화면으로 이동", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignupWithEmailActivity.this, LoginWithEmailActivity.class));
                                    finish(); // Activity 종료
                                    
//                                    // 회원가입 성공 후 자동 로그인 처리
//                                    auth.signInWithEmailAndPassword(email, password)
//                                            .addOnCompleteListener(signInTask -> {
//                                                if (signInTask.isSuccessful()) {
//
//                                                    SharedPreferences sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
//                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                                                    editor.putString("login_method", "email");
//                                                    editor.putString("userId", uid);
//                                                    editor.apply();
//
//                                                    // 자동 로그인 성공 시 MainActivity로 이동
//                                                    Toast.makeText(SignupWithEmailActivity.this, "가입 및 로그인 성공", Toast.LENGTH_SHORT).show();
//                                                    startActivity(new Intent(SignupWithEmailActivity.this, MainActivity.class)
//                                                            .putExtra("isLoginned", true));
//                                                    finish(); // Activity 종료
//
//                                                } else {
//                                                    // 자동 로그인 실패 시 처리
//                                                    Toast.makeText(SignupWithEmailActivity.this, "가입 성공/ 자동 로그인 실패: " + signInTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                                    startActivity(new Intent(SignupWithEmailActivity.this, LoginWithEmailActivity.class));
//                                                    // -> 가입하기 버튼 비활성화?
//                                                    finish(); // Activity 종료
//                                                }
//                                            });

                                }
                            }

                            @Override
                            public void onFailure(Call<LoginResponse> call, Throwable t) {
                                // 네트워크 오류 처리
                            }
                        });

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
