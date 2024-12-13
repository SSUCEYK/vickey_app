package com.example.vickey;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

public class CustomSearchView extends androidx.appcompat.widget.SearchView {

    public CustomSearchView(Context context) {
        super(context);
        init();
    }

    public CustomSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        this.setOnSearchClickListener(v -> applyCustomStyles());
    }

    private void applyCustomStyles() {
        int searchTextId = getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        EditText searchEditText = findViewById(searchTextId);

        if (searchEditText != null) {
            searchEditText.setTextColor(Color.WHITE); // 텍스트 색상
            searchEditText.setHintTextColor(ContextCompat.getColor(getContext(), R.color.grey_1)); // 힌트 색상
        }
    }
}
