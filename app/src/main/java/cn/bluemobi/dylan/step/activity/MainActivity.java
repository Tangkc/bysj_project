package cn.bluemobi.dylan.step.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import cn.bluemobi.dylan.step.R;
import cn.bluemobi.dylan.step.bean.ExerciseInfo;
import cn.bluemobi.dylan.step.bean.UserInfo;
import cn.bluemobi.dylan.step.step.UpdateUiCallBack;
import cn.bluemobi.dylan.step.step.service.StepService;
import cn.bluemobi.dylan.step.step.utils.SharedPreferencesUtils;
import cn.bluemobi.dylan.step.util.OkHttpUtil;
import cn.bluemobi.dylan.step.view.StepArcView;
import okhttp3.Request;

import static cn.bluemobi.dylan.step.util.NetRequestUtil.GET_TODAY_SPORTS;
import static cn.bluemobi.dylan.step.util.NetRequestUtil.GET_USER_INFO;
import static cn.bluemobi.dylan.step.util.NetRequestUtil.UPDATA_USER_INFO;
import static cn.bluemobi.dylan.step.util.NetRequestUtil.UPLOAD_STEP_URL;

/**
 * 记步主页
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int DEFAULT_IMAGELOAD_TIME = 60 * 1000;


    private TextView tv_data;
    private StepArcView cc;
    private TextView tv_set;
    private SharedPreferencesUtils sp;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private UserInfo userInfo;
    private FloatingActionButton button;
    private TextView heatValue;
    private TextView aimStepNum;
    private long userId;
    private ExerciseInfo exerciseInfo;
    private String planWalk_QTY;
    private int heatVal;

    private void assignViews() {
        heatValue = (TextView) findViewById(R.id.heat_value);
        aimStepNum = (TextView) findViewById(R.id.aim_stepNum);
        tv_data = (TextView) findViewById(R.id.tv_data);
        cc = (StepArcView) findViewById(R.id.cc);
        tv_set = (TextView) findViewById(R.id.tv_set);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        button = (FloatingActionButton) findViewById(R.id.btn_fab);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadStepNum();
            }
        });

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

    }

    private void getData() {
        sp = new SharedPreferencesUtils(this);
        Intent intent = getIntent();
        userId = intent.getLongExtra(LoginActivity.USERINFO_USERID, -1);
        sp.setParam(LoginActivity.USERINFO_USERID, userId);
        getUserInfo();
        getTodayExerciseInfo();
    }

    /**
     * 同步步数到服务器
     */
    private void uploadStepNum() {
        HashMap hashMap = new HashMap();
        hashMap.put("userId", userId);
        hashMap.put("stepNumber", Integer.parseInt(cc.getStepNum()));
        hashMap.put("heat", heatVal);
        OkHttpUtil.getFormRequest(UPLOAD_STEP_URL, hashMap, new OkHttpUtil.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                Toast.makeText(MainActivity.this, "已同步步数至服务器", Toast.LENGTH_LONG).show();
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                Toast.makeText(MainActivity.this, "同步失败", Toast.LENGTH_LONG).show();

            }
        });
    }

    /**
     * 获取用户信息
     */
    private void getUserInfo() {
        HashMap hashMap = new HashMap();
        hashMap.put("userId", userId);
        OkHttpUtil.getFormRequest(GET_USER_INFO, hashMap, new OkHttpUtil.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JSONObject json = new JSONObject(result);
                JSONObject returedata = (JSONObject) json.get("returdata");
                String data = returedata.get("userInfo").toString();
                Gson gson = new Gson();
                userInfo = gson.fromJson(data, UserInfo.class);
                setupDrawerContent(navigationView);
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                Log.e("MainActivity", "getUserInfo error");
            }
        });
    }


    /**
     * 获取今天的运动处方
     */
    private void getTodayExerciseInfo() {
        HashMap hashMap = new HashMap();
        hashMap.put("userId", userId);
        OkHttpUtil.getFormRequest(GET_TODAY_SPORTS, hashMap, new OkHttpUtil.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JSONObject json = new JSONObject(result);
                JSONObject returedata = (JSONObject) json.get("returdata");
                String data = returedata.get("exerciseInfo").toString();
                Gson gson = new Gson();
                exerciseInfo = gson.fromJson(data, ExerciseInfo.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        aimStepNum.setText("目标步数：" + exerciseInfo.getStepNumber());
                        try {
                            int stepNum = Integer.parseInt(cc.getStepNum() == null ? "0" : cc.getStepNum());
                            heatVal = 60 * (stepNum / 1000) * 1;
                            heatValue.setText("已消耗热量" + heatVal + "");
                        } catch (Exception e) {
                            heatValue.setText(0);
                        }
                    }
                });
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
            }
        });
    }

    protected DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true).considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565).build();

    private void setupDrawerContent(NavigationView navigationView) {
        View view = navigationView.getHeaderView(0);
        TextView name = (TextView) view.findViewById(R.id.name);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        ImageLoader.getInstance().displayImage(userInfo.getIconUrl(), icon, options);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showIsEdit();
            }
        });
        if (userInfo != null) {
            name.setText(userInfo.getUserNick());
            navigationView.getMenu().getItem(0).setTitle(userInfo.getUserName() + "");
            navigationView.getMenu().getItem(1).setTitle(userInfo.getUserSex() + "");
            navigationView.getMenu().getItem(2).setTitle(userInfo.getUserAge() + "");
            navigationView.getMenu().getItem(3).setTitle(userInfo.getConstellation() + "");
            navigationView.getMenu().getItem(4).getSubMenu().getItem(0).setTitle(userInfo.getJob() + "");
            navigationView.getMenu().getItem(4).getSubMenu().getItem(1).setTitle(userInfo.getUserAddress() + "");
        }
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        showIsEdit();
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }


    EditText name = null, mobile = null, sex = null, age = null, conlocal = null, job = null, local = null;

    private void showIsEdit() {
        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.layout_edit,
                null);
        name = (EditText) layout.findViewById(R.id.edit_name);
        sex = (EditText) layout.findViewById(R.id.edit_sex);
        age = (EditText) layout.findViewById(R.id.edit_age);
        conlocal = (EditText) layout.findViewById(R.id.edit_conlacal);
        job = (EditText) layout.findViewById(R.id.edit_job);
        local = (EditText) layout.findViewById(R.id.edit_local);
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setView(layout)
                .setTitle("编辑")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (userInfo == null) {
                            userInfo = new UserInfo();
                        }
                        if (name != null && !TextUtils.isEmpty(name.getText().toString())) {
                            userInfo.setUserNick(name.getText().toString());
                        }
                        if (sex != null && !TextUtils.isEmpty(sex.getText().toString())) {
                            userInfo.setUserSex(sex.getText().toString());
                        }
                        if (age != null && !TextUtils.isEmpty(age.getText().toString())) {
                            userInfo.setUserAge(Integer.parseInt(age.getText().toString()));
                        }
                        if (conlocal != null && !TextUtils.isEmpty(conlocal.getText().toString())) {
                            userInfo.setConstellation(conlocal.getText().toString());
                        }
                        if (job != null && !TextUtils.isEmpty(job.getText().toString())) {
                            userInfo.setJob(job.getText().toString());
                        }
                        if (local != null && !TextUtils.isEmpty(local.getText().toString())) {
                            userInfo.setUserAddress(local.getText().toString());
                        }
                        dialog.dismiss();
                        Gson gson = new Gson();
                        String json = gson.toJson(userInfo);
                        updaUserInfo(json);
                    }

                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    /**
     * 更新用户信息
     *
     * @param userInfo
     */
    private void updaUserInfo(String userInfo) {

        OkHttpUtil.postJson(UPDATA_USER_INFO, userInfo, new OkHttpUtil.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                setupDrawerContent(navigationView);
                Toast.makeText(MainActivity.this, "更新成功", Toast.LENGTH_LONG).show();
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                Toast.makeText(MainActivity.this, "更新失败", Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).imageDownloader(
                new BaseImageDownloader(this, DEFAULT_IMAGELOAD_TIME, DEFAULT_IMAGELOAD_TIME)) // connectTimeout超时时间
                .build();
        ImageLoader.getInstance().init(config);
        getData();
        assignViews();
        initData();
        addListener();
    }


    private void addListener() {
        tv_set.setOnClickListener(this);
        tv_data.setOnClickListener(this);
    }

    private void initData() {
        //获取用户设置的计划锻炼步数，没有设置过的话默认7000
        planWalk_QTY = (String) sp.getParam("planWalk_QTY", "7000");
        //设置当前步数为0
        //60×8×1.036
        cc.setCurrentCount(Integer.parseInt(planWalk_QTY), 0);
        setupService();
    }


    private boolean isBind = false;

    /**
     * 开启计步服务
     */
    private void setupService() {
        Intent intent = new Intent(this, StepService.class);
        isBind = bindService(intent, conn, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 用于查询应用服务（application Service）的状态的一种interface，
     * 更详细的信息可以参考Service 和 context.bindService()中的描述，
     * 和许多来自系统的回调方式一样，ServiceConnection的方法都是进程的主线程中调用的。
     */
    ServiceConnection conn = new ServiceConnection() {
        /**
         * 在建立起于Service的连接时会调用该方法，目前Android是通过IBind机制实现与服务的连接。
         * @param name 实际所连接到的Service组件名称
         * @param service 服务的通信信道的IBind，可以通过Service访问对应服务
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            StepService stepService = ((StepService.StepBinder) service).getService();
            //设置初始化数据
            String planWalk_QTY = (String) sp.getParam("planWalk_QTY", "7000");
            cc.setCurrentCount(Integer.parseInt(planWalk_QTY), stepService.getStepCount());

            //设置步数监听回调
            stepService.registerCallback(new UpdateUiCallBack() {
                @Override
                public void updateUi(int stepCount) {
                    String planWalk_QTY = (String) sp.getParam("planWalk_QTY", "7000");
                    cc.setCurrentCount(Integer.parseInt(planWalk_QTY), stepCount);
                }
            });
        }

        /**
         * 当与Service之间的连接丢失的时候会调用该方法，
         * 这种情况经常发生在Service所在的进程崩溃或者被Kill的时候调用，
         * 此方法不会移除与Service的连接，当服务重新启动的时候仍然会调用 onServiceConnected()。
         * @param name 丢失连接的组件名称
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_set:
                startActivity(new Intent(this, TodayFoodActivity.class));
                break;
            case R.id.tv_data:
                startActivity(new Intent(this, HistoryActivity.class));
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isBind) {
            this.unbindService(conn);
        }
    }
}
