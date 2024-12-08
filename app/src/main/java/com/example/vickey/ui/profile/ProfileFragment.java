package com.example.vickey.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.vickey.signup.LoginActivity;
import com.example.vickey.R;
import com.example.vickey.databinding.FragmentProfileBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.sdk.user.UserApiClient;
import com.navercorp.nid.NaverIdLoginSDK;

public class ProfileFragment extends Fragment implements View.OnClickListener{

    private FragmentProfileBinding binding;
    private Button login_btn, logout_btn;
    private static final String TAG = "ProfileFragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView called");
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        login_btn = binding.loginBtn;
        logout_btn = binding.logoutBtn;
        login_btn.setOnClickListener(this);
        logout_btn.setOnClickListener(this);

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Intent로부터 로그인 여부 받기 (필요에 따라 Activity에서 전달)
        boolean isLoginned = getActivity().getIntent().getBooleanExtra("isLoginned", false);
        Log.d(TAG, "isLoginned: " + isLoginned); // 로그 추가

        // view에서 버튼 참조

        // 로그인 상태에 따른 버튼의 visibility 설정
        if (isLoginned) {
            login_btn.setVisibility(View.GONE);
            logout_btn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.login_btn) {
            startActivity(new Intent(requireActivity(), LoginActivity.class));
        } else if (id == R.id.logout_btn) {
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE);
            String loginMethod = sharedPreferences.getString("login_method", "");

            if (loginMethod.equals("kakao")){
                UserApiClient.getInstance().logout(error -> {
                    if (error != null) {
                        Log.e("Logout", "카카오 로그아웃 실패", error);
                    } else {
                        Toast.makeText(getActivity(), "카카오 로그아웃 성공", Toast.LENGTH_SHORT).show();
                    }
                    return null;
                });
            }
            else if (loginMethod.equals("naver")){
                try {
                    NaverIdLoginSDK.INSTANCE.logout();
                    Toast.makeText(getActivity(), "네이버 로그아웃 성공", Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    Log.e("Logout", "네이버 로그아웃 실패", e);
                }
            }
            else if (loginMethod.equals("google")){
                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), GoogleSignInOptions.DEFAULT_SIGN_IN);
                mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(), task -> {
                    Toast.makeText(getActivity(), "구글 로그아웃 성공", Toast.LENGTH_SHORT).show();
                });
            }
            else if (loginMethod.equals("email")){
                try {
                    FirebaseAuth.getInstance().signOut();  // Firebase에서 로그아웃
                    Toast.makeText(getActivity(), "이메일 로그아웃 성공", Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    Log.e("Logout", "로그아웃 실패", e);
                }
            }

            login_btn.setVisibility(View.VISIBLE);
            logout_btn.setVisibility(View.GONE);
        }
    }
}