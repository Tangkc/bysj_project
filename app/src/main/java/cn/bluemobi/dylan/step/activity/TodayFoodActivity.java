package cn.bluemobi.dylan.step.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.utils.L;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import cn.bluemobi.dylan.step.R;
import cn.bluemobi.dylan.step.step.utils.SharedPreferencesUtils;
import cn.bluemobi.dylan.step.util.NetRequestUtil;
import cn.bluemobi.dylan.step.util.OkHttpUtil;
import cn.bluemobi.dylan.step.util.UploadUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static cn.bluemobi.dylan.step.util.NetRequestUtil.UPLOAD_FOODIMG_URL;

public class TodayFoodActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_1 = 10000;

    private ImageView imageView;
    private LinearLayout llSelect;
    private ImageView img_camera;
    private RelativeLayout camera;
    private Button submit;
    private TextView history;
    private ImageView titleBack;
    private ProgressBar progressBar;
    private ArrayList uploadFoodList = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_food);
        imageView = (ImageView) findViewById(R.id.img_food);
        llSelect = (LinearLayout) findViewById(R.id.select_food);
        img_camera = (ImageView) findViewById(R.id.img_food_camera);
        camera = (RelativeLayout) findViewById(R.id.rl_camera);
        submit = (Button) findViewById(R.id.submit);
        history = (TextView) findViewById(R.id.food_history);
        titleBack = (ImageView) findViewById(R.id.iv_left);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TodayFoodActivity.this, FootManagerActivity.class);
                startActivity(intent);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tempFile.exists()) {
                    Toast.makeText(TodayFoodActivity.this, "请上传图片", Toast.LENGTH_LONG).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                upImage();
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });
        getTodayFood();
    }

    /**
     * 获取今天的膳食处方
     */
    private void getTodayFood() {
        SharedPreferencesUtils sp = new SharedPreferencesUtils(this);
        HashMap hashMap = new HashMap();
        hashMap.put("userId", sp.getParam(LoginActivity.USERINFO_USERID, -1l));
        OkHttpUtil.getFormRequest(NetRequestUtil.GET_TODAY_FOOD, hashMap, new OkHttpUtil.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JSONObject json = new JSONObject(result);
                JSONObject returedata = (JSONObject) json.get("returdata");
                JSONObject data = (JSONObject) returedata.get("foodInfo");
                JSONArray foodList = (JSONArray) data.get("foodInfoList");
                for (int i = 0; i < foodList.length(); i++) {
                    JSONObject jsonObject = (JSONObject) foodList.get(i);
                    CheckBox checkBox = new CheckBox(TodayFoodActivity.this);
                    checkBox.setText(jsonObject.getString("name") + "*" + jsonObject.get("number"));
                    JSONObject jsonObject1 = new JSONObject();
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                try {
                                    jsonObject1.put("foodId", jsonObject.get("id"));
                                    jsonObject1.put("number", jsonObject.get("number"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                uploadFoodList.add(jsonObject1);
                            } else {
                                uploadFoodList.remove(jsonObject1);
                            }
                        }
                    });
                    llSelect.addView(checkBox);
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }

    private void upImage() {
        if (!tempFile.exists()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(TodayFoodActivity.this, "图片不存在", Toast.LENGTH_LONG).show();
            return;
        }
        OkHttpClient mOkHttpClent = new OkHttpClient();
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "HeadPortrait.jpg",
                        RequestBody.create(MediaType.parse("image/jpeg"), tempFile));

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(NetRequestUtil.UPLOAD_FOODIMG_URL)
                .post(requestBody)
                .build();
        Call call = mOkHttpClent.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TodayFoodActivity.this, "上传图片失败", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject json = null;
                try {
                    String result = response.body().string();
                    json = new JSONObject(result);
                    JSONObject returedata = (JSONObject) json.get("returdata");
                    int iconId = returedata.getInt("iconId");
                    uoloadTodayFood(iconId);
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(TodayFoodActivity.this, "上传图片失败", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    /**
     * 上传今日饮食数据
     *
     * @param iconId
     */
    private void uoloadTodayFood(int iconId) {
        HashMap hashMap = new HashMap();
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < uploadFoodList.size(); i++) {
            jsonArray.put((uploadFoodList.get(i)));
        }
        SharedPreferencesUtils sp = new SharedPreferencesUtils(this);
        hashMap.put("userId", sp.getParam(LoginActivity.USERINFO_USERID, -1l));
        hashMap.put("iconId", iconId);
        hashMap.put("foodInfos", jsonArray.toString());
        OkHttpUtil.postJsonRequest(NetRequestUtil.UPLOAD_TODAYFOOD_INFON, hashMap, new OkHttpUtil.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                if (TextUtils.isEmpty(result)) {
                    Toast.makeText(TodayFoodActivity.this, "上传失败", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                JSONObject json = new JSONObject(result);
                if (json.get("code").equals("10000")) {
                    Toast.makeText(TodayFoodActivity.this, "上传成功", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(TodayFoodActivity.this, "上传失败", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                Toast.makeText(TodayFoodActivity.this, "上传失败", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });

    }


    private static final int PHOTO_REQUEST_CAREMA = 1;// 拍照
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    private static final String PHOTO_FILE_NAME = "temp_photo1.jpg";
    private File tempFile;

    private void openCamera() {
        //打开相机的意图
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            tempFile = new File(Environment.getExternalStorageDirectory(),//Environment.getExternalStorageDirectory()获取SD卡的内容
                    PHOTO_FILE_NAME);
            // 从文件中创建uri
            Uri uri = Uri.fromFile(tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
        startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }


    /**
     * 判断sdcard是否被挂载
     */
    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 剪切图片
     */
    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 80);
        intent.putExtra("outputY", 80);

        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_CAREMA) {
            // 从相机返回的数据
            if (hasSdcard()) {
                crop(Uri.fromFile(tempFile));
            } else {
                Toast.makeText(TodayFoodActivity.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PHOTO_REQUEST_CUT) {
            // 从剪切图片返回的数据
            if (data != null) {
                Bitmap bitmap = data.getParcelableExtra("data"); // 将data中的信息流解析为Bitmap类型
                imageView.setImageBitmap(bitmap);// 显示图片
                img_camera.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}


