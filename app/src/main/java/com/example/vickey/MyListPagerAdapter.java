package com.example.vickey;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.vickey.ui.mylist.HistoryFragment;
import com.example.vickey.ui.mylist.LikesFragment;

public class MyListPagerAdapter extends FragmentStateAdapter {
    public MyListPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new LikesFragment(); // 좋아요 프래그먼트
        } else {
            return new HistoryFragment(); // 시청 기록 프래그먼트
        }
    }

    @Override
    public int getItemCount() {
        return 2; // 두 개의 탭
    }
}
