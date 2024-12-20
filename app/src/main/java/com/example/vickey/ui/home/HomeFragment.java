package com.example.vickey.ui.home;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.vickey.MainActivity;
import com.example.vickey.R;
import com.example.vickey.adapter.ImageSliderAdapter;
import com.example.vickey.adapter.ParentAdapter;
import com.example.vickey.adapter.SearchAdapter;
import com.example.vickey.api.ApiClient;
import com.example.vickey.api.ApiService;
import com.example.vickey.api.dto.EpisodeDTO;
import com.example.vickey.api.models.Episode;
import com.example.vickey.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private MenuProvider menuProvider;
    private ApiService apiService;
    private FragmentHomeBinding binding;
    private ViewPager2 sliderViewPager;
    private LinearLayout layoutIndicator;
    private RecyclerView mainRecyclerView; // contents List - 상위 RecyclerView
    private final String TAG = "HomeFragment";
    List<ContentItem> contentItems = new ArrayList<>();
    private HomeViewModel homeViewModel;

    private RecyclerView searchRecyclerView; // 검색을 위한 RecyclerView
    private SearchAdapter searchAdapter; // 그에 해당하는 어댑터

    private final int sliderSize = 5;
    private final int contentsListSize = 6;

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

        // MenuProvider 제거
        if (menuProvider != null) {
            requireActivity().removeMenuProvider(menuProvider);
            menuProvider = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // 검색 상태 초기화
        hideSearchUI();
        if (searchAdapter != null) {
            searchAdapter.updateEpisodes(new ArrayList<>());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Toolbar 설정
        Toolbar toolbar = ((MainActivity) requireActivity()).getToolbar();

        // ApiClient 및 ViewModel 초기화
        apiService = ApiClient.getApiService(requireContext()); // 싱글톤 ApiService 사용
        homeViewModel.setApiService(apiService);

        sliderViewPager = view.findViewById(R.id.sliderViewPager);
        layoutIndicator = view.findViewById(R.id.layoutIndicators);
        mainRecyclerView = view.findViewById(R.id.mainRecyclerView);
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mainRecyclerView.setAdapter(new ParentAdapter(new ArrayList<>()));

        // ViewModel에 데이터를 로드하고 관찰
        observeViewModel(); // ViewModel 관찰
        loadDataOnce(); // 초기 데이터 로드 메서드

        // MenuProvider 등록
        makeMenuProvider();
        requireActivity().addMenuProvider(menuProvider, getViewLifecycleOwner());

        // 검색 RecyclerView 설정
        setupSearchRecyclerView();
    }

    private void setupSearchRecyclerView() {
        // 검색 결과를 위한 RecyclerView 설정
        searchRecyclerView = binding.searchResultRecyclerView;
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        searchAdapter = new SearchAdapter(new ArrayList<>());
        searchRecyclerView.setAdapter(searchAdapter);
    }

    private void observeViewModel() {
        homeViewModel.getSliderEpisodes().observe(getViewLifecycleOwner(), episodes -> {
            if (episodes != null && !episodes.isEmpty()) {
                Log.d(TAG, "Slider 에피소드 데이터 업데이트: " + episodes.size());

                setupSlider(episodes);
                setupIndicators(sliderSize);
            }
        });

        homeViewModel.getContentItems().observe(getViewLifecycleOwner(), contentItems -> {
            Log.d(TAG, "콘텐츠 데이터 업데이트: " + contentItems.size());
            if (contentItems != null && !contentItems.isEmpty()) {
                setupRecyclerView(contentItems);
            }
        });
    }

    private void logSearchViewChildren(View view, String indent) {
        Log.d(TAG, indent + "View: " + view.getClass().getName() + ", ID: " + view.getId());
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                logSearchViewChildren(group.getChildAt(i), indent + "  ");
            }
        }
    }


    // 색상 설정
    @SuppressLint({"RestrictedApi", "ResourceAsColor"})
    private void applySearchTextColor(SearchView searchView) {

//        int searchTextId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        // SearchAutoComplete 뷰의 ID
        int searchTextId = 2131362345; // 커스텀 ID 사용
        EditText searchEditText = searchView.findViewById(searchTextId);

        // 확인된 클래스에 접근
        if (searchEditText instanceof androidx.appcompat.widget.SearchView.SearchAutoComplete) {
            searchEditText.setTextColor(Color.WHITE); // 텍스트 색상
//            searchEditText.setBackgroundColor(Color.BLACK); // 배경 색상
            searchEditText.setBackgroundColor(Color.TRANSPARENT);
            searchEditText.setHintTextColor(Color.LTGRAY); // 힌트 색상

            // 디버깅 로그
            Log.d(TAG, "EditText instance: " + searchEditText);
            Log.d(TAG, "Text color: " + searchEditText.getCurrentTextColor());
            Log.d(TAG, "Hint color: " + searchEditText.getHintTextColors());
        } else {
            Log.d(TAG, "applySearchTextColor: searchEditText is null or not a valid instance.");
        }
    }


    // 초기 데이터 로드
    private void loadDataOnce() {
        if (homeViewModel.isDataLoaded()) return;

        String[] names = {getString(R.string.content_list_name1),
                getString(R.string.content_list_name2),
                getString(R.string.content_list_name3)
        };

        homeViewModel.loadSliderEpisodes(sliderSize);
        homeViewModel.loadContentItems(contentsListSize, names);
    }

    private void setupRecyclerView(List<ContentItem> contentItems) {
        ParentAdapter mainAdapter = new ParentAdapter(contentItems);
        mainRecyclerView.setAdapter(mainAdapter);
    }

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

    private void setupSlider(List<Episode> episodes) {

        if (episodes == null || episodes.isEmpty()){
            Log.e(TAG, "setupSlider: No episodes to set slider up");
            return;
        }

        int episodesSize = episodes.size();
        List<Episode> modifiedEpisodes = new ArrayList<>(episodes); // 가짜 페이지를 추가하기 위해
        modifiedEpisodes.add(0, episodes.get(episodesSize - 1)); // 마지막 이미지를 첫 번째로 복사
        modifiedEpisodes.add(episodes.get(0)); // 첫 번째 이미지를 마지막으로 복사

        // Image Adapter 설정
        sliderViewPager.setAdapter(new ImageSliderAdapter(requireContext(), modifiedEpisodes));
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
            private int itemCount = modifiedEpisodes.size();

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);

                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    int currentItem = sliderViewPager.getCurrentItem();
                    Log.d(TAG, "onPageScrollStateChanged: currentItem=" + currentItem);

                    if (currentItem == 0) {
                        // 첫 번째 가짜 페이지에 도달한 경우
                        Log.d(TAG, "onPageScrollStateChanged: currentItem=" + currentItem + ", set="+(itemCount-2));
                        sliderViewPager.setCurrentItem(itemCount - 2, false); // 마지막 실제 페이지로 이동
                    }
                    else if (currentItem == itemCount - 1) {
                        // 마지막 가짜 페이지에 도달한 경우
                        Log.d(TAG, "onPageScrollStateChanged: currentItem=" + currentItem + ", set=1");
                        sliderViewPager.setCurrentItem(1, false); // 첫 번째 실제 페이지로 이동
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                int actualPosition = (position == 0) ? episodes.size() - 1 :
                        (position == modifiedEpisodes.size() - 1) ? 0 : position - 1;

                setCurrentIndicator(actualPosition);
            }
        });
    }

    private void makeMenuProvider(){
        menuProvider = new MenuProvider() {
            @SuppressLint("ResourceType")
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {

                menuInflater.inflate(R.menu.actionbar_menu, menu);
                MenuItem menuItem = menu.findItem(R.id.search);
                SearchView searchView = (SearchView) menuItem.getActionView();
                logSearchViewChildren(searchView, "");

                // 힌트 텍스트 설정
                searchView.setQueryHint(getString(R.string.search_query_hint));
                searchView.setBackgroundColor(Color.DKGRAY);

                // SearchView 내부의 EditText 색상을 초기화 시점에 설정
                searchView.post(() -> applySearchTextColor(searchView));

                menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        applySearchTextColor(searchView);
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        return true;
                    }
                });


                // SearchView 리스너 설정
                searchView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        searchView.getViewTreeObserver().removeOnPreDrawListener(this);
                        applySearchTextColor(searchView);
                        return true;
                    }
                });

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                    private Handler handler = new Handler();
                    private Runnable searchRunnable;

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        applySearchTextColor(searchView);
                        performSearch(query);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (searchRunnable != null) {
                            handler.removeCallbacks(searchRunnable);
                        }

                        applySearchTextColor(searchView);

                        // 검색어가 비어있으면 즉시 결과 초기화
                        if (newText.trim().isEmpty()) {
                            searchAdapter.updateEpisodes(new ArrayList<>());
                        }

                        searchRunnable = new Runnable() {
                            @Override
                            public void run() {
                                performSearch(newText);
                            }
                        };

                        handler.postDelayed(searchRunnable, 300);
                        return true;
                    }
                });

                // SearchView 확장/축소 리스너
                menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // 검색 시작할 때의 UI 처리
                        showSearchUI();
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        // 검색 종료할 때의 UI 처리
                        hideSearchUI();
                        return true;
                    }
                });
            }
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        };

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

    // 검색 UI 관련 메서드들
    private void showSearchUI() {
        // 검색 결과를 표시할 RecyclerView를 보이게 하고
        // 기존 컨텐츠(슬라이더, 리사이클러뷰 등)를 숨김
        // ! 만약 제대로 안숨겨진다면 여기서 binding, View.GONE로 숨김
        binding.searchResultRecyclerView.setVisibility(View.VISIBLE);
        binding.sliderViewPager.setVisibility(View.GONE);
        binding.layoutIndicators.setVisibility(View.GONE);
        binding.mainRecyclerView.setVisibility(View.GONE);

        ((MainActivity) requireActivity()).setBottomNavVisibility(View.GONE);
    }

    private void hideSearchUI() {
        // 검색 결과를 숨기고 기존 컨텐츠를 다시 보이게 함
        // ! 만약 제대로 안보여진다면 여기서 binding, View.Visible로 보여줌
        binding.searchResultRecyclerView.setVisibility(View.GONE);
        binding.sliderViewPager.setVisibility(View.VISIBLE);
        binding.layoutIndicators.setVisibility(View.VISIBLE);
        binding.mainRecyclerView.setVisibility(View.VISIBLE);
        ((MainActivity) requireActivity()).setBottomNavVisibility(View.VISIBLE);
    }

    private void performSearch(String query) {
        if (query.trim().isEmpty()) {
            // 검색어가 비어있을 때 처리
            return;
        }

        // API 호출 및 결과 처리
        ApiService apiService = ApiClient.getApiService(requireContext());
        apiService.searchEpisodes(query).enqueue(new Callback<List<EpisodeDTO>>() {
            @Override
            public void onResponse(Call<List<EpisodeDTO>> call, Response<List<EpisodeDTO>> response) {
                if (response.isSuccessful() && isAdded()) {

                    List<EpisodeDTO> episodeDTOs = response.body();

                    List<Episode> episodes = new ArrayList<>();
                    for (EpisodeDTO dto : episodeDTOs) {
                        episodes.add(dto.getEpisode());
                    }

                    // 검색 결과를 RecyclerView에 표시
                    updateSearchResults(episodes);
                }
            }

            @Override
            public void onFailure(Call<List<EpisodeDTO>> call, Throwable t) {
                if (isAdded()) {
                    Log.e("API_ERROR", "API 호출 실패", t);  // 구체적인 에러 로깅
                    String errorMessage = getString(R.string.search_error) +": " + t.getMessage();
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateSearchResults(List<Episode> episodes) {
        if (searchAdapter != null) {
            searchAdapter.updateEpisodes(episodes);
        }
    }

}