package com.mobilelec.dietms.view;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.ViewModelProvider;

import com.anychart.charts.Pie;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.mobilelec.dietms.R;
import com.mobilelec.dietms.viewmodel.NetworkViewModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class DetailActivity extends AppCompatActivity {

    NetworkViewModel networkViewModel;
    TextView dateDetail;
    TextView foodDetail;
    TextView nutr1;
    TextView nutr2;
    TextView nutr3;
    TextView nutr4;
    TextView nutr5;
    TextView nutr6;
    TextView nutr7;
    TextView nutr8;
    TextView nutr9;
    ImageView imgDetail;
    TextView info;
    TextView info2;

    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initializeView();

        pieChart = (PieChart)findViewById(R.id.piechart);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();

        yValues.add(new PieEntry(4f,"Today"));


        Description description = new Description();
        description.setText("총 2400Kcal"); //라벨
        description.setTextSize(15);
        pieChart.setDescription(description);


        PieDataSet dataSet = new PieDataSet(yValues,"음식 종류");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);

        pieChart.setData(data);


        networkViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(NetworkViewModel.class);

        Intent intent = getIntent();
        //String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");
        text = text.replace(",","");
        String createdDate = intent.getStringExtra("created_date");
        //String publishedDate = intent.getStringExtra("published_date");
        String formattedDate = formatDateString(createdDate);

        String image = intent.getStringExtra("image");
        //HtmlCompat.fromHtml("{createdDate}에 대한 식단을 알려줄게요");
        String Htmldate = "<p style=\"color:#495C9F;\"><strong>"+createdDate+"</strong> 의 식단</p>";
        String Htmlfood = "<p style=\"color:#495C9F;\"><strong>"+text+"</strong> 에 대한 영양정보 입니다.</p>";



        dateDetail.setText(HtmlCompat.fromHtml(Htmldate,HtmlCompat.FROM_HTML_MODE_COMPACT));
        foodDetail.setText(HtmlCompat.fromHtml(Htmlfood,HtmlCompat.FROM_HTML_MODE_COMPACT));
        Picasso.get().load(getResources().getString(R.string.BASE_URL)+image).into(imgDetail);


        new NetworkRequestTask(text).execute();


    }

    void initializeView() {
        dateDetail = findViewById(R.id.date_detail);
        foodDetail = findViewById(R.id.food_detail);
        imgDetail = findViewById(R.id.img_detail);
        nutr1 = findViewById(R.id.nutr1);
        nutr2 = findViewById(R.id.nutr2);
        nutr3 = findViewById(R.id.nutr3);
        nutr4 = findViewById(R.id.nutr4);
        nutr5 = findViewById(R.id.nutr5);
        nutr6 = findViewById(R.id.nutr6);
        nutr7 = findViewById(R.id.nutr7);
        nutr8 = findViewById(R.id.nutr8);
        nutr9 = findViewById(R.id.nutr9);
        info = findViewById(R.id.info);
        info2 = findViewById(R.id.info2);


    }

    class NetworkRequestTask extends AsyncTask<Void, Void, String> {
        Resources resources = getResources();
        private String text; // text 변수를 저장할 필드

        public NetworkRequestTask(String text) {
            this.text = text;
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
                    JSONObject jsonObject = new JSONObject(result);
                    // "row" 키 아래에 있는 JSONArray 가져오기
                    JSONArray rowArray = jsonObject.getJSONObject("I2790").getJSONArray("row");

                    // 배열의 첫 번째 요소 가져오기
                    JSONObject firstObject = rowArray.getJSONObject(0);

                    // 해당 "NUTR_CONT" 키에 대한 값을 가져오기
                    nutr1.setText("열량: " + firstObject.getString("NUTR_CONT1") + " kcal");
                    nutr2.setText("탄수화물: " + firstObject.getString("NUTR_CONT2") + " g");
                    nutr3.setText("단백질: " + firstObject.getString("NUTR_CONT3") + " g");
                    nutr4.setText("지방: " + firstObject.getString("NUTR_CONT4") + " g");
                    nutr5.setText("당류: " + firstObject.getString("NUTR_CONT5") + " g");
                    nutr6.setText("나트륨: " + firstObject.getString("NUTR_CONT6") + " mg");
                    nutr7.setText("콜레스테롤: " + firstObject.getString("NUTR_CONT7") + " mg");
                    nutr8.setText("포화지방: " + firstObject.getString("NUTR_CONT8") + " g");
                    nutr9.setText("트랜스지방: " + firstObject.getString("NUTR_CONT9") + " g");


                    float nutrCont1Float;
                    float nutrCont6Float;
                    try {
                        nutrCont1Float = Float.parseFloat(firstObject.getString("NUTR_CONT1"));
                        nutrCont6Float = Float.parseFloat(firstObject.getString("NUTR_CONT6"));

                        updateChart(nutrCont6Float);

                    } catch (NumberFormatException e) {
                        nutrCont1Float = 0;
                        nutrCont6Float = 0;
                    } catch (JSONException e) {
                        nutrCont1Float = 0;
                        nutrCont6Float = 0;
                    }

                    String and = "또한, \n";
                    String text1 = "사용자님의 식단은 "+firstObject.getString("NUTR_CONT1") + " kcal 이상입니다.\n";
                    String text2 = "나트륨 함량이 "+firstObject.getString("NUTR_CONT6") + " mg 이상입니다.\n";
                    String text1Warnig = "\n";
                    String text2Warnig = "\n";

                    if(nutrCont1Float > 500){

                        text1Warnig = "고열량의 음식을 섭취하신 만큼, 남은 끼니에 대한 각별한 주의가 필요합니다.\n";
                    }

                    if(nutrCont6Float > 450 && nutrCont6Float<900){

                        text2Warnig = "나트륨 섭취에 주의가 필요합니다.\n";
                    }
                    else if(nutrCont6Float >= 900){
                        text2Warnig = "나트륨 함량이 매우 높은 음식이므로,\n" +
                                "음식 섭취에 각별한 주의가 필요합니다.\n";
                    }

                    info.setText(text1 +
                            text1Warnig +
                            and + text2);

                    info2.setText(text2Warnig +
                            "이 점을 참고하여 건강한 식사하시길 바랍니다.\n" +
                            "아래에서 세부사항을 확인해주세요.\n");

                    /*
                    NUTR_CONT1	열량(kcal)(1회제공량당)
                    NUTR_CONT2	탄수화물(g)(1회제공량당)
                    NUTR_CONT3	단백질(g)(1회제공량당)
                    NUTR_CONT4	지방(g)(1회제공량당)
                    NUTR_CONT5	당류(g)(1회제공량당)
                    NUTR_CONT6	나트륨(mg)(1회제공량당)
                    NUTR_CONT7	콜레스테롤(mg)(1회제공량당)
                    NUTR_CONT8	포화지방산(g)(1회제공량당)
                    NUTR_CONT9	트랜스지방(g)(1회제공량당)
                     */

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // 오류 처리
            }
        }
    }

    private String formatDateString(String dateString) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy년MM월dd일", Locale.US);

        try {
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void updateChart(Float nutrCont6Float) {
        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();
        yValues.add(new PieEntry(nutrCont6Float,"Today"));
        yValues.add(new PieEntry(2000-nutrCont6Float,"남은 나트륨"));

        Description description = new Description();
        description.setText("나트륨"); //라벨
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

}