package com.example.vickey.signup;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vickey.R;

// 가입한 사용자가 구독방식을 선택하는 액티비티
public class SubscriptionActivity extends AppCompatActivity {
    private Button basicBtn, standardBtn, premiumBtn, paymentBtn;
    private ImageButton backButton;
    private TextView basicPrice, stdPrice, premPrice, basicHD, stdHD, premHD;
    private TextView basicUHD, stdUHD, premUHD, basicAccess, stdAccess, premAccess;
    private TextView basicWatch, stdWatch, premWatch;

    private int selected_subscription_type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_subscription); // XML 파일 연결

        // 타이틀 바 숨기기
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        EdgeToEdge.enable(this);

        // 버튼 및 텍스트뷰 초기화
        basicBtn = findViewById(R.id.basic_btn);
        standardBtn = findViewById(R.id.std_btn);
        premiumBtn = findViewById(R.id.prem_btn);
        paymentBtn = findViewById(R.id.button_payment);
        basicPrice = findViewById(R.id.basic_price);
        stdPrice = findViewById(R.id.std_price);
        premPrice = findViewById(R.id.prem_price);
        basicHD = findViewById(R.id.basic_hd);
        stdHD = findViewById(R.id.std_hd);
        premHD = findViewById(R.id.prem_hd);
        basicUHD = findViewById(R.id.basic_uhd);
        stdUHD = findViewById(R.id.std_uhd);
        premUHD = findViewById(R.id.prem_uhd);
        basicAccess = findViewById(R.id.basic_access);
        stdAccess = findViewById(R.id.std_access);
        premAccess = findViewById(R.id.prem_access);
        basicWatch = findViewById(R.id.basic_watch);
        stdWatch = findViewById(R.id.std_watch);
        premWatch = findViewById(R.id.prem_watch);
        backButton = findViewById(R.id.backButton);


        // 베이직 버튼 클릭 시 처리
        basicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                basicBtn.setBackgroundColor(getColor(R.color.pay_btn));
                standardBtn.setBackgroundColor(getColor(R.color.white));
                premiumBtn.setBackgroundColor(getColor(R.color.white));
                animateTextColorChange(basicPrice, basicHD, basicUHD, basicAccess, basicWatch, stdPrice, stdHD, stdUHD, stdAccess, stdWatch, premPrice, premHD, premUHD, premAccess, premWatch, Color.parseColor("#FF4E88"));
                selected_subscription_type = 1;
            }
        });

        // 스탠다드 버튼 클릭 시 처리
        standardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                standardBtn.setBackgroundColor(getColor(R.color.pay_btn));
                premiumBtn.setBackgroundColor(getColor(R.color.white));
                basicBtn.setBackgroundColor(getColor(R.color.white));
                animateTextColorChange(stdPrice, stdHD, stdUHD, stdAccess, stdWatch, basicPrice, basicHD, basicUHD, basicAccess, basicWatch, premPrice, premHD, premUHD, premAccess, premWatch, Color.parseColor("#FF4E88"));
                selected_subscription_type = 2;
            }
        });

        // 프리미엄 버튼 클릭 시 처리
        premiumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                premiumBtn.setBackgroundColor(getColor(R.color.pay_btn));
                basicBtn.setBackgroundColor(getColor(R.color.white));
                standardBtn.setBackgroundColor(getColor(R.color.white));
                animateTextColorChange(premPrice, premHD, premUHD, premAccess, premWatch, basicPrice, basicHD, basicUHD, basicAccess, basicWatch, stdPrice, stdHD, stdUHD, stdAccess, stdWatch, Color.parseColor("#FF4E88"));
                selected_subscription_type = 3;
            }
        });

        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SubscriptionType selectedSubscription = null;

                switch (selected_subscription_type) {
                    case 1:
                        selectedSubscription = SubscriptionType.BASIC;
                        break;
                    case 2:
                        selectedSubscription = SubscriptionType.STANDARD;
                        break;
                    case 3:
                        selectedSubscription = SubscriptionType.PREMIUM;
                        break;
                    default:
                        // 기본 값 혹은 에러 처리
                        break;
                }


                if (selectedSubscription != null) {

                    Intent intent = new Intent(SubscriptionActivity.this, WebViewActivity.class);
                    intent.putExtra("url", getString(R.string.api_base_url)+"pay");
                    intent.putExtra("subscriptionType", selectedSubscription.name());

                    startActivity(intent);
                    finish();


                } else {
                    // 구독 유형이 선택되지 않았을 경우 처리
                    Log.d("Subscription", "No subscription type selected.");
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SubscriptionActivity.this, LoginActivity.class);
                intent.putExtra("autoLoginCancelled", true);
                Toast.makeText(SubscriptionActivity.this, R.string.auto_login_cancel, Toast.LENGTH_SHORT).show();
                startActivity(intent);
                finish();
            }
        });

    }

    // 애니메이션으로 색상 변경 함수
    private void animateTextColorChange(TextView selectedText1, TextView selectedText2, TextView selectedText3,
                                        TextView selectedText4, TextView selectedText5, TextView selectedText6,
                                        TextView selectedText7, TextView selectedText8, TextView selectedText9,
                                        TextView selectedText10, TextView selectedText11, TextView selectedText12,
                                        TextView selectedText13, TextView selectedText14, TextView selectedText15, int selectedColor) {

        int defaultColor = getColor(R.color.white); // 기본 색상: 흰색
        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), defaultColor, selectedColor);
        colorAnimator.setDuration(300); // 500ms 동안 색상 변경
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int animatedColor = (int) animator.getAnimatedValue();

                // 선택된 텍스트뷰 색상 변경
                selectedText1.setTextColor(animatedColor);
                selectedText2.setTextColor(animatedColor);
                selectedText3.setTextColor(animatedColor);
                selectedText4.setTextColor(animatedColor);
                selectedText5.setTextColor(animatedColor);
                selectedText6.setTextColor(defaultColor);
                selectedText7.setTextColor(defaultColor);
                selectedText8.setTextColor(defaultColor);
                selectedText9.setTextColor(defaultColor);
                selectedText10.setTextColor(defaultColor);
                selectedText11.setTextColor(defaultColor);
                selectedText12.setTextColor(defaultColor);
                selectedText13.setTextColor(defaultColor);
                selectedText14.setTextColor(defaultColor);
                selectedText15.setTextColor(defaultColor);
            }
        });
        colorAnimator.start();
    }

    @Override
    public void onBackPressed() {
        // backButton 클릭 이벤트와 동일한 동작 수행
        Intent intent = new Intent(SubscriptionActivity.this, LoginActivity.class);
        intent.putExtra("autoLoginCancelled", true);
        Toast.makeText(SubscriptionActivity.this, R.string.auto_login_cancel, Toast.LENGTH_SHORT).show();
        startActivity(intent);
        finish(); // 현재 Activity 종료
    }

}

