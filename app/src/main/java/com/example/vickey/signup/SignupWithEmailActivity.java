package com.example.vickey.signup;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vickey.R;
import com.google.firebase.auth.FirebaseAuth;

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
                        Toast.makeText(SignupWithEmailActivity.this, "가입 성공", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, LoginWithEmailActivity.class));
                        finish(); // Activity 종료
                    } else {
                        // 가입 실패 처리
                        Toast.makeText(SignupWithEmailActivity.this, "가입 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
