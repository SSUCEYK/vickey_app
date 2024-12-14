package com.example.vickey.signup;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vickey.MainActivity;
import com.example.vickey.R;

// 구독 선택 후 결제가 이루어지는 액티비티
public class WebViewActivity extends AppCompatActivity {

    private WebView webView;
    private boolean isPaymentCompleted = false; // 결제 완료 여부
    private static final String TAG = "WebViewActivity"; // 로그 태그

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        webView = findViewById(R.id.webView);
        setupWebView();
        loadPaymentUrl();
        setupTouchListener();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Enable popup support
        webSettings.setSupportMultipleWindows(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        // Cookie settings
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        webView.setWebViewClient(new CustomWebViewClient());
        webView.setWebChromeClient(new CustomWebChromeClient());
    }

    private void loadPaymentUrl() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String uid = sharedPreferences.getString("userId", null);
        String subscriptionType = getIntent().getStringExtra("subscriptionType");

        String url = getString(R.string.api_base_url)+"pay?uid=" + uid + "&subscriptionType=" + subscriptionType;
        Log.d(TAG, "Loading URL: " + url);
        webView.loadUrl(url);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupTouchListener() {
        webView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP && isPaymentCompleted) {
                navigateToMainActivity();
                return true;
            }
            return false;
        });
    }

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG, "shouldOverrideUrlLoading: " + url);

            if (url.startsWith("intent://")) {
                try {
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    if (intent != null && !isFinishing()) {
                        startActivity(intent);
                        return true;
                    }
                } catch (Exception e) {
                    String fallbackUrl = Uri.parse(url).getQueryParameter("browser_fallback_url");
                    if (fallbackUrl != null && !isFinishing()) {
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
            Log.d(TAG, "Page finished loading: " + url);

            if (url.contains("/success") || url.contains("kakao/success")) {
                isPaymentCompleted = true;
                Toast.makeText(WebViewActivity.this, R.string.pay_success, Toast.LENGTH_SHORT).show();
                navigateToMainActivity();
            }
            else if (url.contains("/fail")) {
                Toast.makeText(WebViewActivity.this, R.string.pay_fail, Toast.LENGTH_SHORT).show();
                navigateToSubscriptionActivity(); // 결제 실패 시 SubscriptionActivity로 리다이렉트
            }
        }
    }

    private class CustomWebChromeClient extends WebChromeClient {
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog,
                                      boolean isUserGesture, Message resultMsg) {
            if (isFinishing()) return false;

            WebView newWebView = new WebView(WebViewActivity.this);
            setupNewWebViewSettings(newWebView);

            Dialog dialog = createWebViewDialog(newWebView);

            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(newWebView);
            resultMsg.sendToTarget();

            return true;
        }

        @SuppressLint("SetJavaScriptEnabled")
        private void setupNewWebViewSettings(WebView webView) {
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setSupportMultipleWindows(true);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        }

        private Dialog createWebViewDialog(WebView webView) {
            Dialog dialog = new Dialog(WebViewActivity.this);
            dialog.setContentView(webView);

            dialog.setOnDismissListener(dialogInterface -> {
                if (!isFinishing()) {
                    webView.destroy();
                }
            });

            if (!isFinishing()) {
                dialog.show();
            }

            return dialog;
        }
    }

    private void navigateToMainActivity() {
        if (!isFinishing()) {
            Intent intent = new Intent(WebViewActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void navigateToSubscriptionActivity() {
        if (!isFinishing()) {
            Intent intent = new Intent(WebViewActivity.this, SubscriptionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack() && !isPaymentCompleted) {
            webView.goBack();
        } else if (isPaymentCompleted) {
            navigateToMainActivity();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        if (webView != null) {
            webView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.clearHistory();
            webView.clearCache(true);
            webView.loadUrl("about:blank");
            webView.onPause();
            webView.removeAllViews();
            webView.destroyDrawingCache();
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}