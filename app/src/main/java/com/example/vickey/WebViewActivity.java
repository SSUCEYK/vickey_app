package com.example.vickey;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vickey.MainActivity;
import com.example.vickey.R;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;
    private boolean isPaymentCompleted = false; // 결제 완료 여부

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        SharedPreferences sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String uid = sharedPreferences.getString("uid", null);

        String url = getIntent().getStringExtra("url");
        String subscriptionType = getIntent().getStringExtra("subscriptionType");

        if (url != null) {
            url = "http://192.168.0.63:8080/pay?uid=" + uid + "&subscriptionType=" + subscriptionType;
            webView.setWebViewClient(new CustomWebViewClient());
            webView.loadUrl(url);
        }
    }

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // `intent://` URL 처리
            if (url.startsWith("intent://")) {
                try {
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    if (intent != null) {
                        startActivity(intent);
                        return true;
                    }
                } catch (Exception e) {
                    String fallbackUrl = Uri.parse(url).getQueryParameter("browser_fallback_url");
                    if (fallbackUrl != null) {
                        Intent fallbackIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl));
                        startActivity(fallbackIntent);
                    }
                }
                return true;
            }
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            // 결제 성공 URL 확인
            if (url.contains("kakao/success")) {
                isPaymentCompleted = true;
                navigateToMainActivity(); // 결제 성공 시 메인 페이지로 이동
            }
        }
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(WebViewActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack() && !isPaymentCompleted) {
            webView.goBack(); // WebView 뒤로 가기
        } else if (isPaymentCompleted) {
            navigateToMainActivity(); // 결제가 완료된 경우 메인 화면으로 이동
        } else {
            super.onBackPressed(); // 기본 뒤로 가기
        }
    }
}
