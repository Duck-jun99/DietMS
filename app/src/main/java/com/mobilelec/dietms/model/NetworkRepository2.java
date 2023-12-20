package com.mobilelec.dietms.model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.mobilelec.dietms.adapter.ItemList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkRepository2 {
    private final MutableLiveData<List<ItemList>> responseData = new MutableLiveData<>();

    public LiveData<List<ItemList>> getResponseData() {
        return responseData;
    }

    public void fetchDataFromApi() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                //.url("http://192.168.0.102:8000/api_root/Post/")
                .url("http://192.168.0.102:8000/post_today_app/")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle network error
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //Log.e("NetworkRepository", response.body().string());
                if (response.isSuccessful()) {
                    final String data = response.body().string();
                    Log.e("NetworkRepository", data);
                    List<ItemList> itemList = parseData(data);
                    responseData.postValue(itemList); // Update the LiveData with response data
                }
            }
        });
    }

    // data를 파싱하여 ItemList 객체 리스트로 반환하는 메서드
    private List<ItemList> parseData(String data) {
        try {
            // Gson 라이브러리를 활용해 JSON 파싱
            Gson gson = new Gson();
            return gson.fromJson(data, new TypeToken<List<ItemList>>() {}.getType());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return new ArrayList<>(); // 오류 시, 빈 리스트를 반환
        }
    }
}
