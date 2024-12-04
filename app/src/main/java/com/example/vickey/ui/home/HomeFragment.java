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

import com.example.vickey.ContentItem;
import com.example.vickey.ImageSliderAdapter;
import com.example.vickey.ParentAdapter;
import com.example.vickey.R;
import com.example.vickey.api.ApiClient;
import com.example.vickey.api.ApiService;
import com.example.vickey.databinding.FragmentHomeBinding;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private ApiService apiService;
    private List<String> imageUrls = new ArrayList<>();
    private List<Long> ids = new ArrayList<>();

    private FragmentHomeBinding binding;
    private ViewPager2 sliderViewPager;
    private LinearLayout layoutIndicator;

    private RecyclerView mainRecyclerView; // contents List - 상위 RecyclerView

    private final String TAG = "HomeFragment";

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

        // ApiClient를 통해 Retrofit 인스턴스 가져오기
        apiService = ApiClient.getClient(requireActivity().getApplicationContext()).create(ApiService.class);

        sliderViewPager = view.findViewById(R.id.sliderViewPager);
        layoutIndicator = view.findViewById(R.id.layoutIndicators);
        mainRecyclerView = view.findViewById(R.id.mainRecyclerView);
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));


        // Fetch
        // 랜덤 10개 에피소드 가져오기 호출
        fetchRandomEpisodes(5);

        //검색
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.actionbar_menu, menu);
//                Log.d(TAG, "Menu created: " + menu.size()); // 메뉴 아이템 수 출력
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

    }



    private void fetchRandomEpisodes(int n) {
        ids = new ArrayList<>();
        apiService.getRandomEpisodeIds(n).enqueue(new Callback<List<Long>>() {
            @Override
            public void onResponse(Call<List<Long>> call, Response<List<Long>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ids = response.body();
                    Log.d("RandomEpisodeIds", "Fetched IDs: " + ids);

                    // ID 리스트로부터 thumbnailUrls 가져오기
                    fetchEpisodeThumbnails(ids);


                } else {
                    Log.e("RandomEpisodeIds", "Response failed with code: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<List<Long>> call, Throwable t) {
                Log.e("RandomEpisodeIds", "API call failed", t);
            }
        });
    }
    private void fetchEpisodeThumbnails(List<Long> episodeIds) {

        Log.d("RequestBody", new Gson().toJson(episodeIds)); // 요청 바디 확인
        apiService.getEpisodeThumbnails(episodeIds).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    imageUrls = response.body();
                    Log.d("ThumbnailUrls", "Fetched URLs: " + imageUrls);

                    // 데이터 로드 이후에 셋업
                    setupSlider();
                    setupIndicators(imageUrls.size());
                    setupContentsList();

                } else {
                    Log.e("ThumbnailUrls", "Response failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e("ThumbnailUrls", "API call failed", t);
            }
        });
    }


    private void setupContentsList() {
        //콘텐츠 리스트
        List<ContentItem> contentItems = new ArrayList<>();
        contentItems.add(new ContentItem("인기콘텐츠", imageUrlShuffle()));
        contentItems.add(new ContentItem("재미있는 콘텐츠", imageUrlShuffle()));
        contentItems.add(new ContentItem("추천 콘텐츠", imageUrlShuffle()));

        // 어댑터 설정
        ParentAdapter mainAdapter = new ParentAdapter(contentItems, requireContext());
        mainRecyclerView.setAdapter(mainAdapter);
    }



    private void setupSlider() {
        // 가짜 페이지를 추가하기 위해 ImageUrls 수정
        List<String> modifiedImageUrls = new ArrayList<>(imageUrls);

        if (!imageUrls.isEmpty()) {
            modifiedImageUrls.add(0, imageUrls.get(imageUrls.size() - 1)); // 마지막 이미지를 첫 번째로 복사
            modifiedImageUrls.add(imageUrls.get(0)); // 첫 번째 이미지를 마지막으로 복사
        }
        else {
            Log.e(TAG, "No image URLs to set up slider.");
            return; // 데이터가 없으면 초기화하지 않고 리턴
        }

        Log.d(TAG, "imageUrls: " + imageUrls);
        Log.d(TAG, "modifiedImageUrls: " + modifiedImageUrls);

        // Image Adapter 설정
        sliderViewPager.setAdapter(new ImageSliderAdapter(requireContext(), modifiedImageUrls));
        sliderViewPager.setOffscreenPageLimit(3); // 좌, 우까지

        // 초기 페이지를 실제 첫 번째 콘텐츠로 설정
        sliderViewPager.post(() -> sliderViewPager.setCurrentItem(1, false));

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

                    // 첫 번째 가짜 페이지에 도달한 경우
                    if (currentItem == 0) {
                        sliderViewPager.setCurrentItem(itemCount - 2, false); // 마지막 실제 페이지로 이동
                    }

                    // 마지막 가짜 페이지에 도달한 경우
                    if (currentItem == itemCount - 1) {
                        sliderViewPager.setCurrentItem(1, false); // 첫 번째 실제 페이지로 이동
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                int actualPosition = (position == 0) ? imageUrls.size() - 1 :
                        (position == modifiedImageUrls.size() - 1) ? 0 : position - 1;

                Log.d(TAG, "position: " + position + ", actualPosition: " + actualPosition);

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
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        requireContext(), R.drawable.bg_indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        requireContext(), R.drawable.bg_indicator_inactive));
            }
        }
    }


    //임시
    private List<String> imageUrlShuffle() {
        List<String> contentList = new ArrayList<>();
        contentList.add("https://placehold.co/132x180");
        contentList.add("https://placehold.co/132x180");
        contentList.add("https://placehold.co/132x180");
        contentList.add("https://placehold.co/132x180");
        contentList.add("https://placehold.co/132x180");
        contentList.add("https://placehold.co/132x180");
        Collections.shuffle(contentList);
        return contentList;
    }


    private List<Integer> imageShuffle() {
        List<Integer> contentList = new ArrayList<>();
        contentList.add(R.raw.thumbnail_goblin);
        contentList.add(R.raw.thumbnail_lovefromstar);
        contentList.add(R.raw.thumbnail_ohmyghost);
        contentList.add(R.raw.thumbnail_ourbelovedsummer);
        contentList.add(R.raw.thumbnail_vincenzo);
        contentList.add(R.raw.thumbnail_signal);

        Collections.shuffle(contentList);
        return contentList;
    }


}