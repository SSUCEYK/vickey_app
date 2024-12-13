package com.example.vickey.signup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import com.example.vickey.api.ApiClient;
import com.example.vickey.api.ApiService;
import com.example.vickey.api.dto.LoginResponse;
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

import java.util.Locale;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private Button kakao_login_btn;
    private Button naver_login_btn;
//    private Button google_login_btn;
    private Button email_login_btn;
    private TextView sign_up_text;

    private SignInClient oneTapClient;
    private ApiService apiService;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // SubscriptionType 초기화
        initializeSubscriptionTypes();

        // 언어 초기화
        setAppLocale();

        setContentView(R.layout.activity_login);


        // 로그인
        kakao_login_btn = findViewById(R.id.kakao_login_btn);
        naver_login_btn = findViewById(R.id.naver_login_btn);
//        google_login_btn = findViewById(R.id.google_login_btn);
        email_login_btn = findViewById(R.id.email_login_btn);
        sign_up_text = findViewById(R.id.sign_up_text);

        apiService = ApiClient.getApiService(getApplicationContext());
        KakaoSdk.init(this, getString(R.string.kakao_app_key)); // Kakao SDK 초기화

        kakao_login_btn.setOnClickListener(v -> {
            doKakaoLogin();
        });
        naver_login_btn.setOnClickListener(v -> {
            doNaverLogin();
        });
//        google_login_btn.setOnClickListener(v -> {
//            doGoogleLogin();
//        });
        email_login_btn.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, LoginWithEmailActivity.class));
        });
        sign_up_text.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupWithEmailActivity.class));
        });

    }


    // 언어 초기화
    private void setAppLocale() {
        SharedPreferences prefs = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String langCode = prefs.getString("language", "ko"); // 기본 언어: 한국어
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    // SubscriptionType 초기화
    private void initializeSubscriptionTypes() {
        for (SubscriptionType type : SubscriptionType.values()) {
            type.initialize(this); // Context 전달
        }
    }

    /*KAKAO*/
    private final Function2<OAuthToken, Throwable, Unit> kakaoCallback = new Function2<OAuthToken, Throwable, Unit>()
    {
        @Override
        public Unit invoke(OAuthToken token, Throwable error) {

            Log.d(TAG, "loginAccountCallback E");

            if (error != null) {
                Log.e(TAG, getString(R.string.kakao_login_fail), error);
            } else if (token != null) {
                Log.d(TAG, getString(R.string.kakao_login_success) + token.getAccessToken() + " " + token.getIdToken());
                doSigninKakaoToken(token.getAccessToken());
            } else {
                Log.e(TAG, "카카오 로그인: 토큰이 null입니다.");
            }
            return Unit.INSTANCE;
        }

    };


    private void doKakaoLogin() {

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(this)) {
            Log.d(TAG, "doKakaoLogin: 카카오톡으로 로그인");

            UserApiClient.getInstance().loginWithKakaoTalk(this, (token, error) -> {
                if (error != null) {
                    Log.e(TAG, getString(R.string.kakao_login_fail), error);

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우
                    if (error instanceof ClientError && ((ClientError) error).getReason() == ClientErrorCause.Cancelled) {
                        return null;
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    //UserApiClient.getInstance().loginWithKakaoAccount(this, kakaoCallback);
                    Toast.makeText(LoginActivity.this, getString(R.string.kakaoTalk_login_fail)+": "+getString(R.string.no_kakaoTalk), Toast.LENGTH_SHORT).show();

                } else if (token != null) {

                    Log.d(TAG, getString(R.string.kakao_login_success) + token.getAccessToken() + " " + token.getIdToken());
                    doSigninKakaoToken(token.getAccessToken());

                }
                return null;
            });
        } else {
            Log.d(TAG, "doKakaoLogin: 카카오톡 없음. 카카오 계정으로 로그인");
            Toast.makeText(LoginActivity.this, getString(R.string.kakaoTalk_login_fail)+": "+getString(R.string.no_kakaoTalk), Toast.LENGTH_SHORT).show();
            //UserApiClient.getInstance().loginWithKakaoAccount(this, kakaoCallback);
        }
    }

    private void doSigninKakaoToken(String accessToken) {
        // 카카오 로그인 후 처리할 작업
        Log.d(TAG, "카카오 로그인 토큰 처리: " + accessToken);

        // 서버로 카카오 토큰 전송하여 사용자 인증 처리
        apiService.authenticateKakao(accessToken).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse user = response.body();
                    Log.d(TAG, "Kakao Login: User authenticated");
                    Log.d(TAG, "User ID: " + user.getUserId());
                    Log.d(TAG, "User Name: " + user.getUsername());
                    Toast.makeText(LoginActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();

                    // 세션 저장
                    saveLoginSession("kakao", user.getUserId());

                    //테스트용
                    user.setSubscribed(true);

                    if (user.isSubscribed()) {
                        // 결제 완료 -> 메인 페이지로 이동
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        // 결제 미완료 -> 구독 선택 화면으로 이동
                        Intent intent = new Intent(LoginActivity.this, SubscriptionActivity.class);
                        startActivity(intent);
                    }

                    finish();

                } else {
                    Log.e(TAG, "Authentication failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e(TAG, "Server error: " + t.getMessage());
            }
        });
    }

    /*NAVER*/
    private final ActivityResultLauncher<Intent> naverLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                switch (result.getResultCode()) {
                    case Activity.RESULT_OK:
                        // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                        String token = NaverIdLoginSDK.INSTANCE.getAccessToken();
                        Log.e(TAG, "accessToken=" + token);
                        doSigninNaverToken(token);
                        break;
                    case Activity.RESULT_CANCELED:
                        // 실패 or 에러
                        String errorDescription = NaverIdLoginSDK.INSTANCE.getLastErrorDescription();
                        doToastMakeAppend(R.string.login_fail, errorDescription);
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

        // 서버로 네이버 토큰 전송하여 사용자 인증 처리
        apiService.getNaverUser(accessToken).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    //로그인(DB 저장) 성공 시 처리
                    LoginResponse loginResponse = response.body();
                    Log.d(TAG, "onResponse: " + loginResponse);
                    Toast.makeText(LoginActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();

                    // 세션 저장
                    saveLoginSession("naver", loginResponse.getUserId());

                    //테스트용
                    loginResponse.setSubscribed(true);

                    //DB 저장 이후 결제 처리
                    if (loginResponse.isSubscribed()) {
                        // 결제 완료 -> 메인 페이지로 이동
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        // 결제 미완료 -> 구독 선택 화면으로 이동
                        Intent intent = new Intent(LoginActivity.this, SubscriptionActivity.class);
                        startActivity(intent);
                    }
                    finish();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // 네트워크 오류 처리
            }
        });
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

        // 로그인 방법 저장 후 홈 화면 복귀
//        saveLoginSession("google");
        startActivity(new Intent(this, MainActivity.class));
        finish();
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

    private void saveLoginSession(String loginMethod, String userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("login_method", loginMethod);
        editor.putBoolean("isLoginned", true);
        editor.putString("userId", userId);
        editor.apply();
    }

}
