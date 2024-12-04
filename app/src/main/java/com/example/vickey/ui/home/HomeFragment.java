package com.example.vickey.ui.home;

import android.annotation.SuppressLint;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.vickey.ContentItem;
import com.example.vickey.ImageSliderAdapter;
import com.example.vickey.ParentAdapter;
import com.example.vickey.R;
import com.example.vickey.api.ApiClient;
import com.example.vickey.api.ApiService;
import com.example.vickey.api.models.Episode;
import com.example.vickey.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private ApiService apiService;
    private FragmentHomeBinding binding;
    private ViewPager2 sliderViewPager;
    private LinearLayout layoutIndicator;
    private RecyclerView mainRecyclerView; // contents List - 상위 RecyclerView
    private final String TAG = "HomeFragment";
    List<ContentItem> contentItems = new ArrayList<>();
    private HomeViewModel homeViewModel;

    private final int sliderSize = 5;
    private final int contentsListSize = 10;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class); // ViewModel 초기화
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

        // ApiClient를 통해 Retrofit 인스턴스 가져오기
        apiService = ApiClient.getClient(requireActivity().getApplicationContext()).create(ApiService.class);

        // ViewModel에 ApiService 전달
        homeViewModel.setApiService(apiService);

        sliderViewPager = view.findViewById(R.id.sliderViewPager);
        layoutIndicator = view.findViewById(R.id.layoutIndicators);
        mainRecyclerView = view.findViewById(R.id.mainRecyclerView);
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // ViewModel에 데이터를 로드하고 관찰
        observeViewModel(); // ViewModel 관찰
        loadDataOnce(); // 초기 데이터 로드 메서드
        setupMenuProvider(view); // (검색 액션바) 메뉴 설정

    }

    private void observeViewModel() {
        homeViewModel.getSliderEpisodes().observe(getViewLifecycleOwner(), episodes -> {
            if (episodes != null && !episodes.isEmpty()) {
                setupSliderFromEpisodes(episodes);
                setupIndicators(sliderSize);
            }
        });

        homeViewModel.getContentItems().observe(getViewLifecycleOwner(), contentItems -> {
            if (contentItems != null && !contentItems.isEmpty()) {
                setupRecyclerView(contentItems);
            }
        });
    }

    // 초기 데이터 로드
    private void loadDataOnce() {
        if (homeViewModel.isDataLoaded()) return;
        homeViewModel.loadSliderEpisodes(sliderSize);
        homeViewModel.loadContentItems(contentsListSize);
    }

    private void setupRecyclerView(List<ContentItem> contentItems) {
        ParentAdapter mainAdapter = new ParentAdapter(contentItems);
        mainRecyclerView.setAdapter(mainAdapter);
    }

//    private void fetchRandomEpisodes(int n, String name) {
//        apiService.getRandomEpisodes(n).enqueue(new Callback<List<Episode>>() {
//            @Override
//            public void onResponse(Call<List<Episode>> call, Response<List<Episode>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    List<Episode> episodes = response.body();
//                    Log.d("fetchRandomEpisodes", "Fetched Episodes: " + episodes);
//
//                    if (episodes == null || episodes.isEmpty()) {
//                        Log.e("fetchEpisodesForHome", "No episodes received");
//                        return;
//                    }
//                    else if (name.isEmpty()) {
//                        // 슬라이더 설정
//                        setupSliderFromEpisodes(episodes);
//                        setupIndicators(n);
//                    }
//                    else {
//                        // 콘텐츠 리스트 설정
//                        setupContentsListFromEpisodes(episodes, name);
//                    }
//
//                } else {
//                    Log.e("fetchRandomEpisodes", "Response failed with code: " + response.code());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Episode>> call, Throwable t) {
//                Log.e("fetchRandomEpisodes", "API call failed", t);
//            }
//        });
//    }

    @SuppressLint("NotifyDataSetChanged")
    private void setupContentsListFromEpisodes(List<Episode> episodes, String name){
        // ContentItem 생성 및 추가
        ContentItem contentItem = new ContentItem(name, episodes);
        contentItems.add(contentItem);

        // RecyclerView 업데이트
        if (mainRecyclerView.getAdapter() == null) {
            ParentAdapter mainAdapter = new ParentAdapter(contentItems);
            mainRecyclerView.setAdapter(mainAdapter);
        } else {
            mainRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    private void setupSliderFromEpisodes(List<Episode> episodes) {
        List<String> thumbnails = new ArrayList<>();
        for (Episode episode : episodes) {
            thumbnails.add(episode.getThumbnailUrl());
        }
        setupSlider(thumbnails);
    }

    private void setupSlider(List<String> imageUrls) {

        if (imageUrls == null || imageUrls.isEmpty()) {
            Log.e(TAG, "No image URLs to set up slider.");
            return;
        }

        int imageUrlsSize = imageUrls.size();
        List<String> modifiedImageUrls = new ArrayList<>(imageUrls); // 가짜 페이지를 추가하기 위해 ImageUrls 수정

        modifiedImageUrls.add(0, imageUrls.get(imageUrlsSize - 1)); // 마지막 이미지를 첫 번째로 복사
        modifiedImageUrls.add(imageUrls.get(0)); // 첫 번째 이미지를 마지막으로 복사

        Log.d(TAG, "imageUrls: " + imageUrls);
        Log.d(TAG, "modifiedImageUrls: " + modifiedImageUrls);

        // Image Adapter 설정
        sliderViewPager.setAdapter(new ImageSliderAdapter(requireContext(), modifiedImageUrls));
        sliderViewPager.setOffscreenPageLimit(3); // 좌, 우까지
        sliderViewPager.post(() -> sliderViewPager.setCurrentItem(1, false)); // 초기 페이지를 실제 첫 번째 콘텐츠로 설정

        sliderViewPager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float scaleFactor = 0.85f;
                float alphaFactor = 0.5f;
                float absPos = Math.abs(position);

                if (position <= -1 || position >= 1) {
                    page.setAlpha(0f);
                } else if (position == 0) {
                    page.setAlpha(1f);
                    page.setScaleY(1f);
                } else {
                    page.setAlpha(1 - absPos * (1 - alphaFactor));
                    page.setScaleY(1 - absPos * (1 - scaleFactor));
                }
            }
        });

        sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            private int itemCount = modifiedImageUrls.size();

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);

                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    int currentItem = sliderViewPager.getCurrentItem();

                    if (currentItem == 0) {
                        // 첫 번째 가짜 페이지에 도달한 경우
                        sliderViewPager.setCurrentItem(itemCount - 2, false); // 마지막 실제 페이지로 이동
                    }
                    else if (currentItem == itemCount - 1) {
                        // 마지막 가짜 페이지에 도달한 경우
                        sliderViewPager.setCurrentItem(1, false); // 첫 번째 실제 페이지로 이동
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                int actualPosition = (position == 0) ? imageUrls.size() - 1 :
                        (position == modifiedImageUrls.size() - 1) ? 0 : position - 1;

                setCurrentIndicator(actualPosition);
            }
        });
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
            imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),
                    i == position ? R.drawable.bg_indicator_active : R.drawable.bg_indicator_inactive));
        }
    }

    private void setupMenuProvider(View view) {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.actionbar_menu, menu);
                MenuItem menuItem = menu.findItem(R.id.search);
                SearchView searchView = (SearchView) menuItem.getActionView();
                searchView.setQueryHint(getString(R.string.search_query_hint));
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.search) {
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner());
    }
}