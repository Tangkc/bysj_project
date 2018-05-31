package cn.bluemobi.dylan.step.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.bluemobi.dylan.step.R;
import cn.bluemobi.dylan.step.bean.SportHistoryList;
import cn.bluemobi.dylan.step.step.utils.SharedPreferencesUtils;
import cn.bluemobi.dylan.step.util.BarChartManager;
import cn.bluemobi.dylan.step.util.OkHttpUtil;
import okhttp3.Request;

import static cn.bluemobi.dylan.step.util.NetRequestUtil.GET_SPORTS_HISTORY;

/**
 * Created by yuandl on 2016-10-18.
 */

public class HistoryActivity extends AppCompatActivity implements
        OnChartValueSelectedListener {

    private LinearLayout layout_titlebar;
    private ImageView iv_left;
    private BarChart barChart1;
    private BarChart barChart2;
    private SportHistoryList sportHistoryList;
    //protected Typeface mTfLight;

    private void assignViews() {
        layout_titlebar = (LinearLayout) findViewById(R.id.layout_titlebar);
        iv_left = (ImageView) findViewById(R.id.iv_left);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_history);
        assignViews();
        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        barChart1 = (BarChart) findViewById(R.id.bar_chart1);
        barChart2 = (BarChart) findViewById(R.id.bar_chart2);


        // add data
        getData();


    }

    private void setStepData(ArrayList<Float> arrayList1, ArrayList<Float> arrayList2) {
        BarChartManager barChartManager1 = new BarChartManager(barChart1);
        //设置x轴的数据
        ArrayList<Float> xValues = new ArrayList<>();
        for (int i = 0; i < arrayList1.size(); i++) {
            xValues.add((float) i);
        }

        //设置y轴的数据()
        List<List<Float>> yValues = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            List<Float> yValue = new ArrayList<>();
            if (i == 0) {
                for (int j = 0; j < arrayList1.size(); j++) {
                    yValue.add(arrayList1.get(i));
                }
            } else {
                for (int j = 0; j < arrayList2.size(); j++) {
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
        names.add("实际步数");
        names.add("计划步数");
        barChartManager1.showBarChart(xValues, yValues, names, colours);
        barChartManager1.setXAxis(7f, 0f, 7);

    }


    private void setHeatData(ArrayList<Float> arrayList1, ArrayList<Float> arrayList2) {
        // BarChartManager barChartManager1 = new BarChartManager(barChart1);
        BarChartManager barChartManager2 = new BarChartManager(barChart2);
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
        names.add("实际卡路里");
        names.add("计划卡路里");
        //  barChartManager1.showBarChart(xValues, yValues.get(0), names.get(1), colours.get(3));
        barChartManager2.showBarChart(xValues, yValues, names, colours);
        barChartManager2.setXAxis(7f, 0f, 7);

    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());

//        mChart.centerViewToAnimated(e.getX(), e.getY(), mChart.getData().getDataSetByIndex(h.getDataSetIndex())
//                .getAxisDependency(), 500);
//
//        mChart1.centerViewToAnimated(e.getX(), e.getY(), mChart.getData().getDataSetByIndex(h.getDataSetIndex())
//                .getAxisDependency(), 500);

    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    /**
     * 获取步数历史
     */
    private void getData() {
        HashMap hashMap = new HashMap();
        SharedPreferencesUtils sp = new SharedPreferencesUtils(this);
        hashMap.put("userId", sp.getParam(LoginActivity.USERINFO_USERID, -1l));
        OkHttpUtil.getFormRequest(GET_SPORTS_HISTORY, hashMap, new OkHttpUtil.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JSONObject json = new JSONObject(result);
                String returedata = json.get("returdata").toString();
                Gson gson = new Gson();
                sportHistoryList = gson.fromJson(returedata, SportHistoryList.class);
                List arrayList = sportHistoryList.getList();
                ArrayList arrayList1 = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                for (int i = 0; i < arrayList.size(); i++) {
                    arrayList1.add(((SportHistoryList.SportHistoryBean) arrayList.get(i)).getHeat());
                    arrayList2.add(((SportHistoryList.SportHistoryBean) arrayList.get(i)).getShouldHeat());
                }
                setHeatData(arrayList1, arrayList2);
                ArrayList arrayList3 = new ArrayList();
                ArrayList arrayList4 = new ArrayList();
                for (int i = 0; i < arrayList.size(); i++) {
                    arrayList3.add(((SportHistoryList.SportHistoryBean) arrayList.get(i)).getStepNumber());
                    arrayList4.add(((SportHistoryList.SportHistoryBean) arrayList.get(i)).getShouldStepNumber());
                }
                setHeatData(arrayList1, arrayList2);
                setStepData(arrayList3, arrayList4);
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                Toast.makeText(HistoryActivity.this,"获取数据失败",Toast.LENGTH_LONG).show();
            }
        });
    }


}
