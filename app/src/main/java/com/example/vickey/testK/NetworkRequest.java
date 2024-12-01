package com.example.vickey.testK;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NetworkRequest {

    private final OkHttpClient client = new OkHttpClient();

    // 동기식 요청으로 HashMap을 반환하는 함수
    public HashMap<String, String> sendGetRequest(String userId) throws IOException {
        String url = "http://25.17.248.210:8080/userinfo?id=" + userId;

        Request request = new Request.Builder()
                .url(url)
                .build();

        // 동기 요청 실행
        Call call = client.newCall(request);
        Response response = call.execute(); // 동기식으로 서버 응답 대기

        // 응답이 성공적일 경우
        if (response.isSuccessful()) {
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                String responseData = responseBody.string();

                try {
                    // JSON 데이터를 HashMap으로 변환
                    JSONObject jsonObject = new JSONObject(responseData);
                    HashMap<String, String> hashMap = new HashMap<>();

                    Iterator<String> keys = jsonObject.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        String value = jsonObject.getString(key);
                        hashMap.put(key, value);
                    }

                    return hashMap; // HashMap 반환
                } catch (Exception e) {
                    throw new IOException("JSON 파싱 오류", e);
                }
            } else {
                throw new IOException("응답 본문이 없습니다.");
            }
        } else {
            throw new IOException("서버 응답 실패: " + response.code());
        }
    }
}