package com.example.vickey.signup;

import android.content.ContentValues;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vickey.R;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.model.ClientError;
import com.kakao.sdk.common.model.ClientErrorCause;
import com.kakao.sdk.user.UserApiClient;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class AuthCodeHandlerActivity extends AppCompatActivity {

    private final Function2<OAuthToken, Throwable, Unit> kakaoCallback = (token, error) -> {
        if (error != null) {
            Log.e(ContentValues.TAG, getString(R.string.kakao_login_fail), error);
        } else if (token != null) {
            Log.d(ContentValues.TAG, getString(R.string.kakao_login_success)+ token.getAccessToken() + " " + token.getIdToken());
            doSigninKakaoToken(token.getAccessToken());
        }
        return Unit.INSTANCE;
    };

    public void doKakaoLogin() {
        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(this)) {
            UserApiClient.getInstance().loginWithKakaoTalk(this, (token, error) -> {
                if (error != null) {
                    Log.e(ContentValues.TAG, getString(R.string.kakaoTalk_login_fail), error);

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우
                    if (error instanceof ClientError && ((ClientError) error).getReason() == ClientErrorCause.Cancelled) {
                        return null;
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.getInstance().loginWithKakaoAccount(this, kakaoCallback);
                } else if (token != null) {
                    Log.d(ContentValues.TAG, getString(R.string.kakaoTalk_login_success) + token.getAccessToken() + " " + token.getIdToken());
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
        Log.d("AuthCodeHandlerActivity", "doSigninKakaoToken: accessToken: " + accessToken);
    }
}
