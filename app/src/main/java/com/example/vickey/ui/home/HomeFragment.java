package com.example.vickey.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.vickey.ContentItemAdapter;
import com.example.vickey.ImageSliderAdapter;
import com.example.vickey.R;
import com.example.vickey.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ViewPager2 sliderViewPager;
    private LinearLayout layoutIndicator;
    private final String TAG = "HomeFragment";

    private int[] images = new int[] {
            R.drawable.thumbnail_goblin,
            R.drawable.thumbnail_lovefromstar,
            R.drawable.thumbnail_ohmyghost,
            R.drawable.thumbnail_ourbelovedsummer,
            R.drawable.thumbnail_signal,
            R.drawable.thumbnail_vincenzo
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //콘텐츠 리스트
        // 어댑터 설정
        RecyclerView recyclerView1 = view.findViewById(R.id.contentRecyclerView1);
        RecyclerView recyclerView2 = view.findViewById(R.id.contentRecyclerView2);

        // LayoutManager 설정
        recyclerView1.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView2.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));

        // 데이터 준비
        List<Integer> contentImages1 = imageShuffle();
        List<Integer> contentImages2 = imageShuffle();

        // 어댑터 설정
        recyclerView1.setAdapter(new ContentItemAdapter(contentImages1));
        recyclerView2.setAdapter(new ContentItemAdapter(contentImages2));

        Log.d(TAG, "Content for RecyclerView 1: " + contentImages1.size());
        Log.d(TAG, "Content for RecyclerView 2: " + contentImages2.size());


        //검색
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.actionbar_menu, menu);
                Log.d(TAG, "Menu created: " + menu.size()); // 메뉴 아이템 수 출력
                MenuItem menuItem = menu.findItem(R.id.search);
                SearchView searchView = (SearchView) menuItem.getActionView();
                searchView.setQueryHint(getString(R.string.search_query_hint));
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.search) {
                    // Search action
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner());

        //슬라이더
        // Initialize views using the 'view' parameter
        sliderViewPager = view.findViewById(R.id.sliderViewPager);
        layoutIndicator = view.findViewById(R.id.layoutIndicators);

        // Image Adapter 설정
        sliderViewPager.setAdapter(new ImageSliderAdapter(requireContext(), images));

        // Stack Animation 설정
        sliderViewPager.setOffscreenPageLimit(3); //좌, 우까지

        sliderViewPager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float scaleFactor = 0.85f; // 좌우 이미지 크기 비율
                float alphaFactor = 0.5f;  // 좌우 이미지 투명도 비율
                float absPos = Math.abs(position);

                if (position <= -1 || position >= 1) {
                    // 화면 밖의 페이지는 보이지 않음
                    page.setAlpha(0f);
                } else if (position == 0) {
                    // 중앙 페이지는 크기 100%와 투명도 100%
                    page.setAlpha(1f);
                    page.setScaleY(1f);
                } else {
                    // 양옆 페이지는 크기를 줄이고 투명도를 낮춤
                    page.setAlpha(1 - absPos * (1 - alphaFactor)); // 중앙에서 멀어질수록 투명하게
                    page.setScaleY(1 - absPos * (1 - scaleFactor)); // 중앙에서 멀어질수록 작아지게
                }
            }
        });
        
        // 페이지 변경 시 Indicator Update
        sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);

                // 마지막 페이지에 도달했을 때 첫 페이지로 돌아가기
                if (position == images.length - 1) {
                    sliderViewPager.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sliderViewPager.setCurrentItem(0, false); // 애니메이션 없이 첫 페이지로 이동
                        }
                    }, 2000); // 2초 후에 이동
                }
            }
        });

        //Indicator 설정
        setupIndicators(images.length);
    }



    private List<Integer> imageShuffle() {
        List<Integer> contentList = new ArrayList<>();
        contentList.add(R.drawable.thumbnail_goblin);
        contentList.add(R.drawable.thumbnail_lovefromstar);
        contentList.add(R.drawable.thumbnail_ohmyghost);
        contentList.add(R.drawable.thumbnail_ourbelovedsummer);
        contentList.add(R.drawable.thumbnail_vincenzo);
        contentList.add(R.drawable.thumbnail_signal);

        Collections.shuffle(contentList);
        return contentList;
    }


    private void setupIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(8, 8, 8, 8);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(requireContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    requireContext(), R.drawable.bg_indicator_inactive));
            indicators[i].setLayoutParams(params);
            layoutIndicator.addView(indicators[i]);
        }

        setCurrentIndicator(0);
    }

    private void setCurrentIndicator(int position) {
        int childCount = layoutIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutIndicator.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        requireContext(), R.drawable.bg_indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        requireContext(), R.drawable.bg_indicator_inactive));
            }
        }
    }


}