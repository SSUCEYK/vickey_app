package com.example.vickey.ui.profile;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.vickey.R;
import com.example.vickey.api.ApiClient;
import com.example.vickey.api.ApiService;
import com.example.vickey.api.models.User;
import com.example.vickey.databinding.FragmentProfileBinding;
import com.example.vickey.signup.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.sdk.user.UserApiClient;
import com.navercorp.nid.NaverIdLoginSDK;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "ProfileFragment";
    private static final int REQUEST_IMAGE_PICK = 101;
    private FragmentProfileBinding binding;
    private Button login_btn, logout_btn;

    private ImageView profile_image;
    private TextView profile_username;
    private TextView profile_uid;
    private User user = new User();
    private String userId;
    private ApiService apiService;
    private LinearLayout language_setting;
    private LinearLayout inquiry_request;

    private ProfileViewModel viewModel;
    private SharedPreferences prefs;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        apiService = ApiClient.getApiService(requireContext()); // 싱글톤 ApiService 사용
        setBind();

        // ViewModel 초기화
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // SharedPreferences에서 userId 가져오기
        prefs = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        userId = prefs.getString("userId", null);
        profile_uid.setText("UID: " + userId);

        // ViewModel의 LiveData를 관찰하여 UI 업데이트
        viewModel.getUser().observe(getViewLifecycleOwner(), this::updateUserProfileUI);

        // 사용자 데이터 로드
        viewModel.loadUser(userId, apiService);

        return binding.getRoot();
    }

    private void setBind(){
        login_btn = binding.loginBtn;
        logout_btn = binding.logoutBtn;
        login_btn.setOnClickListener(this);
        logout_btn.setOnClickListener(this);
        profile_uid = binding.profileUid;
        profile_image = binding.profileImage;
        profile_username = binding.profileUsername;
        profile_image.setOnClickListener(this);
        profile_username.setOnClickListener(this);
        language_setting = binding.languageSetting;
        language_setting.setOnClickListener(this);
        inquiry_request = binding.inquiryRequest;
        inquiry_request.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 로그인 정보
        boolean isLoginned = prefs.getBoolean("isLoginned", false);
        Log.d(TAG, "onViewCreated: isLoginned="+isLoginned);

        // 로그인 상태에 따른 버튼의 visibility 설정
        if (isLoginned) {
            login_btn.setVisibility(View.GONE);
            logout_btn.setVisibility(View.VISIBLE);

            Log.d(TAG, "onViewCreated: login_method=" + prefs.getString("login_method", "default"));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showLanguageDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_language_selection, null);

        view.findViewById(R.id.language_korean).setOnClickListener(v -> {
            setLocale("ko");
            dialog.dismiss();
        });

        view.findViewById(R.id.language_english).setOnClickListener(v -> {
            setLocale("en");
            dialog.dismiss();
        });

        dialog.setContentView(view);
        dialog.show();
    }


    private void setLocale(String langCode) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("language", langCode);
        editor.apply();

        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        config.setLocale(locale);
        resources.updateConfiguration(config, displayMetrics);

        // 앱 재시작
        Intent intent = new Intent(getContext(), getActivity().getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.login_btn) {
            startActivity(new Intent(requireActivity(), LoginActivity.class));
        } else if (id == R.id.logout_btn) {
            logout();
        } else if (id == R.id.profile_image) {
            pickProfileImage();
        } else if (id == R.id.profile_username) {
            changeUsername();
        } else if (id == R.id.language_setting){
            showLanguageDialog();
        } else if (id == R.id.inquiry_request) {
            showInquiryDialog();
        }
    }
    private void showInquiryDialog() {
        // BottomSheetDialog 생성
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_inquiry, null);

        EditText emailInput = view.findViewById(R.id.inquiry_email);
        EditText messageInput = view.findViewById(R.id.inquiry_message);
        Button sendButton = view.findViewById(R.id.inquiry_send_button);
        Button cancelButton = view.findViewById(R.id.inquiry_cancel_button);

        // 전송 버튼 클릭 이벤트
        sendButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String message = messageInput.getText().toString().trim();

            if (!email.isEmpty() && !message.isEmpty()) {
                sendInquiryEmail(email, message);
                bottomSheetDialog.dismiss();
            } else {
                Toast.makeText(requireContext(), getString(R.string.inquiry_error_empty), Toast.LENGTH_SHORT).show();
            }
        });

        // 취소 버튼 클릭 이벤트
        cancelButton.setOnClickListener(v -> bottomSheetDialog.dismiss());

        // 다이얼로그 설정 및 표시
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    private void sendInquiryEmail(String email, String message) {
        // 이메일 인텐트 생성
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.admin_email)}); // 관리자 이메일
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.inquiry_subject));
        emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.from
        )+ ": " + email + "\n\n" + getString(R.string.message) +":\n" + message);

        try {
            startActivity(Intent.createChooser(emailIntent, getString(R.string.choose_email_client)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(requireContext(), getString(R.string.no_email_client), Toast.LENGTH_SHORT).show();
        }
    }

    public void setUserProfile(){
        user.setUserId(userId);
        apiService.getUserProfile(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user = response.body();
                    updateUserProfileUI(user);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Failed to fetch user profile", t);

                // 기본값 설정
                user.setUserId(userId);
                user.setUsername(getString(R.string.default_user_name));
                user.setProfilePictureUrl(getString(R.string.default_user_profileImg)); // 기본 프로필 이미지 URL 설정
                updateUserProfileUI(user);
            }
        });
    }

    private void updateUserProfileUI(User user) {
        if (user == null) {
            Log.e(TAG, "User is null, cannot update UI");
            return;
        }

        String userName = user.getUsername();
        if (userName == null || userName.isEmpty()){
            profile_username.setText(R.string.default_user_name);
        } else {
            profile_username.setText(userName);
        }

        String userId = user.getUserId();
        userId = userId.length() > 10 ? userId.substring(0, 10) : userId;
        profile_uid.setText("UID: " + userId);

        String profileUrl = user.getProfilePictureUrl();
        if ((profileUrl == null) || (profileUrl.isEmpty())){
            user.setProfilePictureUrl(getString(R.string.default_user_profileImg)); // 기본 프로필 이미지 URL 설정
        }
        else {
            Glide.with(requireContext())
                    .load(profileUrl)
                    .apply(new RequestOptions()
                            .transform(new CenterCrop(), new RoundedCorners(32))) // 코너 라운드 처리
                    .into(profile_image);
        }
    }

    private void updateUserProfileThumbnailUI(String url) {
        if (url == null) {
            Log.e(TAG, "url is null, cannot update UI");
            return;
        }
        Glide.with(requireContext())
                .load(url)
                .apply(new RequestOptions()
                        .transform(new CenterCrop(), new RoundedCorners(32))) // 코너 라운드 처리
                .into(profile_image);
    }

    private void logout(){
        prefs = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String loginMethod = prefs.getString("login_method", "");

//        if (loginMethod.equals("kakao")){
//            UserApiClient.getInstance().logout(error -> {
//                if (error != null) {
//                    makeLogoutToastMsg(false);
//                    Log.e("Logout", "카카오 로그아웃 실패", error);
//                } else {
//                    makeLogoutToastMsg(true);
//                }
//                return null;
//            });
//        } else if (loginMethod.equals("google")){
//            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), GoogleSignInOptions.DEFAULT_SIGN_IN);
//            mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(), task -> {
//                makeLogoutToastMsg(true);
//            });
//        }
        if (loginMethod.equals("naver")){
            try {
                NaverIdLoginSDK.INSTANCE.logout();
                makeLogoutToastMsg(true);
            } catch (Exception e){
                makeLogoutToastMsg(false);
                Log.e("Logout", "네이버 로그아웃 실패", e);
            }
        }
        else if (loginMethod.equals("email")){
            try {
                FirebaseAuth.getInstance().signOut();  // Firebase에서 로그아웃
                makeLogoutToastMsg(true);
            } catch (Exception e){
                makeLogoutToastMsg(false);
                Log.e("Logout", getString(R.string.logout_fail), e);
            }
        }
        else{
            makeLogoutToastMsg(false);
            Log.e("Logout", "로그아웃 실패: no login session");
            return;
        }

        login_btn.setVisibility(View.VISIBLE);
        logout_btn.setVisibility(View.GONE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isLoginned", false);
        editor.putLong("last_login_time", System.currentTimeMillis());
        editor.apply();

        // 메인 로그인 화면으로 이동
        startActivity(new Intent(getContext(), LoginActivity.class));
        getActivity().finish();
    }

    private void makeLogoutToastMsg(boolean success){
        if (success){
            Toast.makeText(getActivity(), getString(R.string.logout_success), Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getActivity(), getString(R.string.logout_fail), Toast.LENGTH_SHORT).show();
        }
    }

    private void changeUsername() {
        // 클릭 시 유저 네임 변경할 수 있도록 설정
        // 변경 시 DB에 저장까지
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getString(R.string.change_user_name));

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton(getString(R.string.save), (dialog, which) -> {
            String newUsername = input.getText().toString();
            updateUsername(userId, newUsername);
        });
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateUsername(String userId, String newUsername) {
        apiService.updateUsername(userId, newUsername).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    profile_username.setText(newUsername);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, getString(R.string.user_name_update_error), t);
            }
        });
    }

    private void pickProfileImage() {
        // 프로필 사진 디바이스에서 업로드해 변경할 수 있도록 설정
        // 변경 시 DB에 저장까지
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            uploadProfileImage(selectedImage);
        }
    }

    private void uploadProfileImage(Uri imageUri) {
        File file = new File(getRealPathFromURI(imageUri));
        RequestBody requestFile = RequestBody.create(file, okhttp3.MediaType.parse("image/*"));
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        apiService.uploadProfileImage(userId, body).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    String profileUrl = response.body().get("profileImageUrl");

                    Log.d(TAG, "onResponse: profileUrl=" + profileUrl);

                    updateUserProfileThumbnailUI(profileUrl); // 프로필 다시 불러오기
                } else {
                    Log.e(TAG, "Failed to upload profile image: " + response.message());
                    Toast.makeText(requireContext(), getString(R.string.profileImg_upload_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Log.e(TAG, "Error uploading profile image", t);
                Toast.makeText(requireContext(), getString(R.string.profileImg_upload_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getRealPathFromURI(Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri)) {
                File tempFile = File.createTempFile("upload", ".jpg", requireContext().getCacheDir());
                try (FileOutputStream outStream = new FileOutputStream(tempFile)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, bytesRead);
                    }
                }
                return tempFile.getAbsolutePath();
            } catch (IOException e) {
                Log.e(TAG, "Error retrieving file path", e);
            }
        } else {
            String[] projection = {MediaStore.Images.Media.DATA};
            try (Cursor cursor = requireContext().getContentResolver().query(uri, projection, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    return cursor.getString(columnIndex);
                }
            }
        }
        return null;
    }
}