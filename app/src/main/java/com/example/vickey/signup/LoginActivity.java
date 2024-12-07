package com.example.vickey.signup;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vickey.MainActivity;
import com.example.vickey.R;
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.common.model.ClientError;
import com.kakao.sdk.common.model.ClientErrorCause;
import com.kakao.sdk.user.UserApiClient;
import com.navercorp.nid.NaverIdLoginSDK;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class LoginActivity extends AppCompatActivity {

    private Button kakao_login_btn;
    private Button naver_login_btn;
    private Button google_login_btn;
    private Button email_login_btn;
    private TextView sign_up_text;

    private SignInClient oneTapClient;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        kakao_login_btn = findViewById(R.id.kakao_login_btn);
        naver_login_btn = findViewById(R.id.naver_login_btn);
        google_login_btn = findViewById(R.id.google_login_btn);
        email_login_btn = findViewById(R.id.email_login_btn);
        sign_up_text = findViewById(R.id.sign_up_text);

        kakao_login_btn.setOnClickListener(v -> {
            doKakaoLogin();
        });
        naver_login_btn.setOnClickListener(v -> {
            doNaverLogin();
        });
        google_login_btn.setOnClickListener(v -> {
            doGoogleLogin();
        });
        email_login_btn.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, LoginWithEmailActivity.class));
        });
        sign_up_text.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupWithEmailActivity.class));
        });

    }

    /*KAKAO*/
    private final Function2<OAuthToken, Throwable, Unit> kakaoCallback = (token, error) -> {
        if (error != null) {
            Log.e(TAG, "카카오계정으로 로그인 실패", error);
        } else if (token != null) {
            Log.e(TAG, "카카오계정으로 로그인 성공 " + token.getAccessToken() + " " + token.getIdToken());
            doSigninKakaoToken(token.getAccessToken());
        }
        return Unit.INSTANCE;
    };

    private void doKakaoLogin() {
        KakaoSdk.init(this, getString(R.string.kakao_app_key)); // Kakao SDK 초기화

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(this)) {
            UserApiClient.getInstance().loginWithKakaoTalk(this, (token, error) -> {
                if (error != null) {
                    Log.e(ContentValues.TAG, "카카오톡으로 로그인 실패", error);

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우
                    if (error instanceof ClientError && ((ClientError) error).getReason() == ClientErrorCause.Cancelled) {
                        return null;
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.getInstance().loginWithKakaoAccount(this, kakaoCallback);
                } else if (token != null) {
                    Log.e(ContentValues.TAG, "카카오톡으로 로그인 성공 " + token.getAccessToken() + " " + token.getIdToken());
                    doSigninKakaoToken(token.getAccessToken());
                }
                return null;
            });
        } else {
            UserApiClient.getInstance().loginWithKakaoAccount(this, kakaoCallback);
        }
    }

    private void doSigninKakaoToken(String accessToken) {
        // 카카오 로그인 후 처리할 작업
        // 서버로 카카오 토큰 전송하여 사용자 인증 처리
        Log.d(TAG, "카카오 로그인 토큰 처리: " + accessToken);

        SharedPreferences sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("login_method", "kakao");
        editor.apply();

        startActivity((new Intent(this, MainActivity.class)).putExtra("isLoginned", true));
    }


    /*NAVER*/
    private final ActivityResultLauncher<Intent> naverLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                switch (result.getResultCode()) {
                    case Activity.RESULT_OK:
                        // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                        String token = NaverIdLoginSDK.INSTANCE.getAccessToken();
                        Log.e("", "accessToken=" + token);
                        doSigninNaverToken(token);
                        break;
                    case Activity.RESULT_CANCELED:
                        // 실패 or 에러
                        String errorDescription = NaverIdLoginSDK.INSTANCE.getLastErrorDescription();
                        doToastMakeAppend(R.string.titleFailure, errorDescription);
                        break;
                }
            }
    );

    private void doNaverLogin() {
        NaverIdLoginSDK.INSTANCE.initialize(this, getString(R.string.naver_client_id), getString(R.string.naver_client_secret), getString(R.string.naver_client_name));// Naver SDK 초기화
        NaverIdLoginSDK.INSTANCE.authenticate(this, naverLauncher);
    }

    // 네이버 로그인 후 처리할 작업
    private void doSigninNaverToken(String accessToken) {
        // 네이버 로그인 토큰 처리
        Log.d(TAG, "네이버 로그인 토큰 처리: " + accessToken);
        Log.d(TAG, "client id : " + getString(R.string.naver_client_id));
        Log.d(TAG, "ReFreshToken : " + NaverIdLoginSDK.INSTANCE.getRefreshToken());
        Log.d(TAG, "Expires : " + String.valueOf(NaverIdLoginSDK.INSTANCE.getExpiresAt()));
        Log.d(TAG, "TokenType : " + NaverIdLoginSDK.INSTANCE.getTokenType());
        Log.d(TAG, "State : " + String.valueOf(NaverIdLoginSDK.INSTANCE.getState()));

        SharedPreferences sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("login_method", "naver");
        editor.apply();

        startActivity((new Intent(this, MainActivity.class)).putExtra("isLoginned", true));
    }

    private void doToastMakeAppend(int titleResourceId, String message) {
        // 토스트 메시지 구현
        String title = getString(titleResourceId);
        String fullMessage = title + ": " + message;
        Toast.makeText(this, fullMessage, Toast.LENGTH_LONG).show();
    }


    /*GOOGLE*/
    private final ActivityResultLauncher<IntentSenderRequest> intentSenderRequestActivityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartIntentSenderForResult(),
                    result -> {
                        Log.e(TAG, "Google Sign-in Result code: " + result.getResultCode());
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();

                            Log.e(TAG, "Google Sign-in Result data: " + data);

                            if (data != null) {
                                try {
                                    // 로그인 성공 시, 구글 계정 정보 가져오기
                                    SignInCredential signInCredential = oneTapClient.getSignInCredentialFromIntent(data);
                                    String username = signInCredential.getId();
                                    String displayName = signInCredential.getDisplayName();

                                    Log.e(TAG, "Google Sign-in Login success: " + username + ", " + displayName);
                                    doSigninGoogleToken(username, displayName);

                                } catch (ApiException e) {
                                    Log.e(TAG, "Google Sign-in error: " + e.getStatusCode() + ", " + e.getMessage());
                                    handleApiException(e);
                                }
                            }
                        }
                    }
            );

    // Google One-Tap 로그인 시작
    private void doGoogleLogin() {
        Log.e(TAG, "Google login start ...");

        oneTapClient = Identity.getSignInClient(this);

        GetSignInIntentRequest getSignInIntentRequest = GetSignInIntentRequest.builder()
                .setServerClientId(getString(R.string.google_client_id))
                .build();

        oneTapClient.getSignInIntent(getSignInIntentRequest)
                .addOnSuccessListener(pendingIntent -> {
                    Log.e(TAG, "Google Sign-in One Tap success");
                    IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(pendingIntent.getIntentSender()).build();
                    intentSenderRequestActivityResultLauncher.launch(intentSenderRequest);
                })
                .addOnFailureListener(exception -> {
                    Log.d(TAG, "Google Sign-in failed: " + exception.getMessage());
                });
    }

    // Google 로그인 후 토큰을 사용하여 처리하는 로직 (ex: 서버에 토큰 전송)
    private void doSigninGoogleToken(String username, String displayName) {
        Log.d(TAG, "Sign in with Google: " + username + ", " + displayName);

        SharedPreferences sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("login_method", "google");
        editor.apply();

        startActivity((new Intent(this, MainActivity.class)).putExtra("isLoginned", true));
    }

    // API 예외 처리
    private void handleApiException(ApiException e) {
        int statusCode = e.getStatusCode();
        if (statusCode == CommonStatusCodes.CANCELED) {
            Log.d(TAG, "One-tap dialog was closed: " + e.getMessage());
        } else if (statusCode == CommonStatusCodes.NETWORK_ERROR) {
            Log.d(TAG, "Network error occurred: " + e.getMessage());
        } else {
            Log.d(TAG, "Couldn't get credential from result: " + e.getLocalizedMessage());
        }
    }

}
