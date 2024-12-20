package com.example.vickey.ui.mylist;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.vickey.adapter.MyListPagerAdapter;
import com.example.vickey.R;
import com.example.vickey.databinding.FragmentMylistBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MyListFragment extends Fragment {

    private FragmentMylistBinding binding;
    //    private MyListViewModel myListViewModel;
    private final String TAG = "MyListFragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Log.e(TAG, "onCreateView: in");

        binding = FragmentMylistBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TabLayout tabLayout = root.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = root.findViewById(R.id.viewPager);

        MyListPagerAdapter pagerAdapter = new MyListPagerAdapter(requireActivity());
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText(getString(R.string.like_tab));
            } else {
                tab.setText(getString(R.string.history_tab));
            }
        }).attach();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}