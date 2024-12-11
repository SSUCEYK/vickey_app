package com.example.vickey.signup;

import android.content.Context;

import com.example.vickey.R;

// 구독 타입. 외부 결제 sdk에 보내기 위함
public enum SubscriptionType {
    BASIC(R.string.pay_basic_name, R.string.pay_basic_price),
    STANDARD(R.string.pay_standard_name, R.string.pay_standard_price),
    PREMIUM(R.string.pay_premium_name, R.string.pay_premium_price);

    private final int nameResId;
    private final int priceResId;
    private String name;
    private int price;

    SubscriptionType(int nameResId, int priceResId) {
        this.nameResId = nameResId;
        this.priceResId = priceResId;
    }

    public void initialize(Context context) {
        this.name = context.getString(nameResId);

        String priceString = context.getString(priceResId);
        String numericPrice = priceString.replaceAll("[^0-9]", ""); // 숫자가 아닌 모든 문자를 제거
        this.price = Integer.parseInt(numericPrice);
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }
}

