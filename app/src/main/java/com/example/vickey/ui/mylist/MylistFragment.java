package com.example.vickey.ui.mylist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.vickey.MyListPagerAdapter;
import com.example.vickey.R;
import com.example.vickey.databinding.FragmentMylistBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MylistFragment extends Fragment {

    private FragmentMylistBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        MylistViewModel dashboardViewModel =
//                new ViewModelProvider(this).get(MylistViewModel.class);

        binding = FragmentMylistBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textMylist;
//        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        TabLayout tabLayout = root.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = root.findViewById(R.id.viewPager);

        MyListPagerAdapter pagerAdapter = new MyListPagerAdapter(requireActivity());
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("좋아요");
            } else {
                tab.setText("시청 내역");
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