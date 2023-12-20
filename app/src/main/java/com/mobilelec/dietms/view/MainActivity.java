package com.mobilelec.dietms.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.mobilelec.dietms.adapter.ItemList;
import com.mobilelec.dietms.R;
import com.mobilelec.dietms.adapter.MyAdapter;
import com.mobilelec.dietms.viewmodel.DBViewModel;
import com.mobilelec.dietms.viewmodel.NetworkViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    DBViewModel userViewModel;
    NetworkViewModel networkViewModel;
    RecyclerView rvUser;
    MyAdapter adapter;
    Button btnAdd;

    PieChart pieChart;

    ImageView imgMorning;
    ImageView imgLunch;
    ImageView imgDinner;

    Float kcal = 0f;
    TextView tvClinic;
    Integer currentTaskCount = 0;
    ArrayList<String> mealList;
    TextView tvUser;
    TextView tvUserAge;
    TextView tvUserGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mealList = new ArrayList<String>();

        initializeView();

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();

        yValues.add(new PieEntry(kcal,"Today"));


        Description description = new Description();
        description.setText("총 2400Kcal"); //라벨
        description.setTextSize(15);
        pieChart.setDescription(description);


        PieDataSet dataSet = new PieDataSet(yValues,"음식 종류");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);

        pieChart.setData(data);

        //OkHttp 통신을 위한 뷰모델
        networkViewModel = new ViewModelProvider(this).get(NetworkViewModel.class);

        networkViewModel.fetchDataFromApi(); // Trigger the API request


        MyAdapter.OnClickListener clickListener = new MyAdapter.OnClickListener() {
            @Override
            public void onClick(ItemList user) {
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra("title", user.title);
                intent.putExtra("text", user.text);
                intent.putExtra("created_date", user.created_date);
                intent.putExtra("published_date", user.published_date);
                intent.putExtra("image", user.image);
                startActivity(intent);
            }
        };

        MyAdapter.OnLongClickListener longClickListener = new MyAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(ItemList user) {
                /*showAlertDialog(user);*/
            }
        };

        adapter = new MyAdapter(clickListener, longClickListener);
        rvUser.setAdapter(adapter);
        rvUser.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvUser.setHasFixedSize(true);



        networkViewModel.getResponseData().observe(this, itemList ->  {

            adapter.setUsers(itemList);
            int totalTaskCount = itemList.size();

            for(int i=0; i<itemList.size(); i++){
                mealList.add(itemList.get(i).meal);
                Log.e("MainActivity",itemList.get(i).meal);
                if(Objects.equals(itemList.get(i).meal, "0")){
                    imgMorning.setVisibility(View.VISIBLE);
                }
                else if(Objects.equals(itemList.get(i).meal, "1")){
                    imgLunch.setVisibility(View.VISIBLE);

                }
                else if(Objects.equals(itemList.get(i).meal, "2")){
                    imgDinner.setVisibility(View.VISIBLE);
                }


                new NetworkRequestTask(itemList.get(i).text, itemList.get(i).meal, totalTaskCount).execute();
                pieChart.invalidate();

            }


        });



        userViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(DBViewModel.class);

/*
        userViewModel.getAll().observe(this, new Observer<List<UserEntity>>() {
            @Override
            public void onChanged(List<UserEntity> userEntities) {
                adapter.setUsers(userEntities);
            }
        });
*/

    }

    void initializeView() {

        rvUser = findViewById(R.id.rvUser);
        pieChart = (PieChart)findViewById(R.id.piechart);
        imgMorning = findViewById(R.id.ImgMorning);
        imgLunch = findViewById(R.id.ImgLunch);
        imgDinner = findViewById(R.id.ImgDinner);
        tvClinic = findViewById(R.id.tvClinic);

        tvUser = findViewById(R.id.tvUser);
        tvUserAge = findViewById(R.id.tvUserAge);
        tvUserGender = findViewById(R.id.tvUserGender);

        tvClinic.setText("잠시만 기다려주세요.\n영양정보를 분석중이에요.");

        tvUser.setText(getResources().getString(R.string.name));
        tvUserAge.setText("나이: " + getResources().getString(R.string.age));
        tvUserGender.setText("성별: " + getResources().getString(R.string.gender));

    }

    class NetworkRequestTask extends AsyncTask<Void, Void, String> {

        Resources resources = getResources();
        private String text; // text 변수를 저장할 필드
        private final String meal;
        private final Integer totalTaskCount;

        public NetworkRequestTask(String text,String meal,Integer totalTaskCount) {

            this.text = text;
            this.meal = meal;
            this.totalTaskCount = totalTaskCount;
        }
        @Override
        protected String doInBackground(Void... params) {
            // 네트워크 요청을 백그라운드에서 수행
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://openapi.foodsafetykorea.go.kr/api/"+ resources.getString(R.string.API_KEY) + "/I2790/json/1/1/DESC_KOR=" + text)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // 네트워크 요청 완료 후 UI 업데이트
            if (result != null) {
                try {

                    currentTaskCount++;

                    JSONObject jsonObject = new JSONObject(result);
                    // "row" 키 아래에 있는 JSONArray 가져오기
                    JSONArray rowArray = jsonObject.getJSONObject("I2790").getJSONArray("row");

                    // 배열의 첫 번째 요소 가져오기
                    JSONObject firstObject = rowArray.getJSONObject(0);

                    kcal += Float.parseFloat(firstObject.getString("NUTR_CONT1"));
                    Log.e("kcal",kcal.toString());


                    if(Objects.equals(currentTaskCount, totalTaskCount)){
                        updateChart();
                        updateUI(meal);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // 오류 처리
            }
        }
    }
    private void updateChart() {
        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();
        yValues.add(new PieEntry(kcal,"Today"));
        yValues.add(new PieEntry(2400-kcal,"남은 Kcal"));

        Description description = new Description();
        description.setText("총 2400Kcal"); //라벨
        description.setTextSize(15);
        pieChart.setDescription(description);

        PieDataSet dataSet = new PieDataSet(yValues,"");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);

        pieChart.setData(data);
        pieChart.invalidate(); // 차트 업데이트
    }

    private void updateUI(String meal){
        String str1 = "";
        String str2 = "";

        if(!mealList.contains("0") || !mealList.contains("1") || !mealList.contains("2")){
            String str1_1 = "";
            if(!mealList.contains("0")){
                str1_1 = str1_1 + "아침 ";
            }
            if(!mealList.contains("1")){
                str1_1 = str1_1 + " 점심 ";
            }
            if(!mealList.contains("2")){
                str1_1 = str1_1 + " 저녁 ";
            }
            str1=str1_1 + "식사를 안하셨군요.\n삼시세끼를 챙겨 드시는 습관을 가지시길 바랍니다.\n사용자님은 당뇨 질환을 앓고 계시기에 더욱\n" +
                    "유념하셔야 합니다.";
        }
        else{
            str1="삼시세끼를 잘 챙겨 드셨군요.";
        }

        Float remainingKcal = 2400 - kcal;
        tvClinic.setText(str1 + "\n현재까지 " + kcal.toString() + " kcal 섭취하셨고, \n목표 칼로리까지 "+ remainingKcal.toString()+" kcal 남았습니다.");
    }
}