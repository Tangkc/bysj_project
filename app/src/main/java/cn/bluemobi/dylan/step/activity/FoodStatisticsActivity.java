package cn.bluemobi.dylan.step.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.BarChart;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.bluemobi.dylan.step.R;
import cn.bluemobi.dylan.step.bean.FoodHeatList;
import cn.bluemobi.dylan.step.bean.FoodHistoryList;
import cn.bluemobi.dylan.step.bean.SportHistoryList;
import cn.bluemobi.dylan.step.step.utils.SharedPreferencesUtils;
import cn.bluemobi.dylan.step.util.BarChartManager;
import cn.bluemobi.dylan.step.util.NetRequestUtil;
import cn.bluemobi.dylan.step.util.OkHttpUtil;
import okhttp3.Request;

public class FoodStatisticsActivity extends AppCompatActivity {

    private BarChart barChart1;
    private FoodHeatList foodHistoryList;
    private ImageView titleBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_statistics);
        titleBack = (ImageView) findViewById(R.id.iv_left);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        barChart1 = (BarChart) findViewById(R.id.bar_chart1);
        getData();
    }

    private void getData() {
        HashMap hashMap = new HashMap();
        SharedPreferencesUtils sp = new SharedPreferencesUtils(this);
        hashMap.put("userId", sp.getParam(LoginActivity.USERINFO_USERID, -1l));
        OkHttpUtil.getFormRequest(NetRequestUtil.GET_FOOD_JILU, hashMap, new OkHttpUtil.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JSONObject json = new JSONObject(result);
                String returedata = json.get("returdata").toString();
                Gson gson = new Gson();
                foodHistoryList = gson.fromJson(returedata, FoodHeatList.class);
                List arrayList = foodHistoryList.getList();
                ArrayList arrayList1 = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                for (int i = 0; i < arrayList.size(); i++) {
                    arrayList1.add(((FoodHeatList.FoodHeatBean) arrayList.get(i)).getHeat());
                    arrayList2.add(((FoodHeatList.FoodHeatBean) arrayList.get(i)).getShouldHeat());
                }
                setHeatData(arrayList1, arrayList2);
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }

    private void setHeatData(ArrayList<Float> arrayList1, ArrayList<Float> arrayList2) {
        // BarChartManager barChartManager1 = new BarChartManager(barChart1);
        BarChartManager barChartManager2 = new BarChartManager(barChart1);
        //设置x轴的数据
        ArrayList<Float> xValues = new ArrayList<>();
        for (int i = 0; i <= arrayList1.size(); i++) {
            xValues.add((float) i);
        }

        //设置y轴的数据()
        List<List<Float>> yValues = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            List<Float> yValue = new ArrayList<>();
            if (i == 0) {
                for (int j = 0; j <= arrayList1.size(); j++) {
                    yValue.add(arrayList1.get(i));
                }
            } else {
                for (int j = 0; j <= arrayList2.size(); j++) {
                    yValue.add(arrayList2.get(i));
                }
            }
            yValues.add(yValue);
        }

        //颜色集合
        List<Integer> colours = new ArrayList<>();
        colours.add(Color.GREEN);
        colours.add(Color.BLUE);


        //线的名字集合
        List<String> names = new ArrayList<>();
        names.add("实际热量");
        names.add("计划热量");
        //  barChartManager1.showBarChart(xValues, yValues.get(0), names.get(1), colours.get(3));
        barChartManager2.showBarChart(xValues, yValues, names, colours);
        barChartManager2.setXAxis(7f, 0f, 7);

    }


}
