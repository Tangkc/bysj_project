package cn.bluemobi.dylan.step.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.bluemobi.dylan.step.R;
import cn.bluemobi.dylan.step.adapter.FoodAdapter;
import cn.bluemobi.dylan.step.bean.FoodHistoryList;
import cn.bluemobi.dylan.step.step.utils.SharedPreferencesUtils;
import cn.bluemobi.dylan.step.util.NetRequestUtil;
import cn.bluemobi.dylan.step.util.OkHttpUtil;
import okhttp3.Request;

public class FootManagerActivity extends AppCompatActivity {

    /**
     * ImageLoad默认参数
     */
    private static final int DEFAULT_IMAGELOAD_TIME = 60 * 1000;

    ListView listView;
    FoodAdapter foodAdapter;
    FoodHistoryList foodHistoryList;
    TextView textView;
    List<FoodHistoryList.FoodHistory> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foot_manager);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).imageDownloader(
                new BaseImageDownloader(this, DEFAULT_IMAGELOAD_TIME, DEFAULT_IMAGELOAD_TIME)) // connectTimeout超时时间
                .build();
        ImageLoader.getInstance().init(config);
        listView = (ListView) findViewById(R.id.food_list);
        textView = (TextView) findViewById(R.id.food_tongji);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FootManagerActivity.this,FoodStatisticsActivity.class);
                startActivity(intent);
            }
        });
        foodAdapter = new FoodAdapter(FootManagerActivity.this, arrayList);
        listView.setAdapter(foodAdapter);
        getFoodHistory();
    }

    private void getFoodHistory() {
        SharedPreferencesUtils sp = new SharedPreferencesUtils(this);
        HashMap hashMap = new HashMap();
        hashMap.put("userId", sp.getParam(LoginActivity.USERINFO_USERID, -1l));
        OkHttpUtil.getFormRequest(NetRequestUtil.GET_FOOD_HISTORY, hashMap, new OkHttpUtil.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JSONObject json = new JSONObject(result);
                String returedata = json.get("returdata").toString();
                Gson gson = new Gson();
                foodHistoryList = gson.fromJson(returedata, FoodHistoryList.class);
                arrayList.addAll(foodHistoryList.getList());
                foodAdapter.notifyDataSetChanged();
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                Toast.makeText(FootManagerActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
