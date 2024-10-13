package com.example.vickey.ui.mylist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.vickey.databinding.FragmentMylistBinding;

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
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}