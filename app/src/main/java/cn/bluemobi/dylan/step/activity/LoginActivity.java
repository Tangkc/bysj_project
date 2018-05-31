package cn.bluemobi.dylan.step.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bluemobi.dylan.step.R;
import cn.bluemobi.dylan.step.bean.UserInfo;
import cn.bluemobi.dylan.step.util.NetRequestUtil;
import cn.bluemobi.dylan.step.util.OkHttpUtil;
import okhttp3.Request;

public class LoginActivity extends AppCompatActivity {

    public static final String USERINFO_USERID = "USERINFO_USERID";

    private static final int DEFAULT_IMAGELOAD_TIME = 60 * 1000;

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.btn_login)
    Button _loginButton;
    @BindView(R.id.link_signup)
    TextView _signupLink;
    private UserInfo userInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).imageDownloader(
                new BaseImageDownloader(this, DEFAULT_IMAGELOAD_TIME, DEFAULT_IMAGELOAD_TIME)) // connectTimeout超时时间
                .build();
        ImageLoader.getInstance().init(config);
        // TODO: 2018/5/27  
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        HashMap hashMap = new HashMap();
        hashMap.put("userName", email);
        hashMap.put("password", password);
        OkHttpUtil.postJsonRequest(NetRequestUtil.LOGIN_URL, hashMap, new OkHttpUtil.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JSONObject json = new JSONObject(result);
                JSONObject returedata = (JSONObject) json.get("returdata");
                String data = returedata.get("userInfo").toString();
                Gson gson = new Gson();
                userInfo = gson.fromJson(data, UserInfo.class);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra(USERINFO_USERID, userInfo.getUserId());
                startActivity(intent);
                onLoginSuccess();
                // onLoginFailed();
                progressDialog.dismiss();
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !Patterns.PHONE.matcher(email).matches()) {
            _emailText.setError("enter a valid phoneNum");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
