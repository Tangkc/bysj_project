package cn.bluemobi.dylan.step.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mob.MobSDK;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bluemobi.dylan.step.R;
import cn.bluemobi.dylan.step.bean.UserInfo;
import cn.bluemobi.dylan.step.util.NetRequestUtil;
import cn.bluemobi.dylan.step.util.OkHttpUtil;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Request;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    @BindView(R.id.input_address)
    EditText _addressText;
    @BindView(R.id.input_mobile)
    EditText _mobileText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.input_reEnterPassword)
    EditText _reEnterPasswordText;
    @BindView(R.id.get_number)
    Button _getPhoneCode;
    @BindView(R.id.btn_signup)
    Button _signupButton;
    @BindView(R.id.link_login)
    TextView _loginLink;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        MobSDK.init(this);
        ButterKnife.bind(this);

        /**
         * 注册完成
         */
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(_addressText.getText().toString())) {
                    Toast.makeText(SignupActivity.this, "验证码不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                // signup();
                submitCode("86", _mobileText.getText().toString(), _addressText.getText().toString());
            }
        });

        /**
         * 登陆界面
         */
        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        /**
         * 获取验证码
         */
        _getPhoneCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCode("86", _mobileText.getText().toString());
            }
        });
    }

    /**
     * 注册
     */
    public void signup() {
        if (!validate()) {
            onSignupFailed();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String address = _addressText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (!TextUtils.isEmpty(address) && !TextUtils.isEmpty(mobile) && !TextUtils.isEmpty(password) && password.equals(reEnterPassword)) {
            HashMap hashMap = new HashMap();
            hashMap.put("userName", mobile);
            hashMap.put("userPassword", password);
            OkHttpUtil.postJsonRequest(NetRequestUtil.REGEST_URL, hashMap, new OkHttpUtil.DataCallBack() {
                @Override
                public void requestSuccess(String result) throws Exception {
                    JSONObject json = new JSONObject(result);
                    if (json.get("code").equals("10003")) {
                        Toast.makeText(SignupActivity.this, "该用户已存在", Toast.LENGTH_LONG).show();
                        return;
                    }
                    JSONObject returedata = (JSONObject) json.get("returdata");
                    String data = returedata.get("userInfo").toString();
                    Gson gson = new Gson();
                    UserInfo userInfo = gson.fromJson(data, UserInfo.class);
                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    intent.putExtra(LoginActivity.USERINFO_USERID, userInfo.getUserId());
                    startActivity(intent);
                    finish();
                }

                @Override
                public void requestFailure(Request request, IOException e) {
                    Toast.makeText(SignupActivity.this, "注册失败~请重新尝试", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(SignupActivity.this, "注册失败~请重新尝试", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }

        // TODO: Implement your own signup logic here.

//        new android.os.Handler().postDelayed(
//                new Runnable() {
//                    public void run() {
//                        // On complete call either onSignupSuccess or onSignupFailed
//                        // depending on success
//                        onSignupSuccess();
//                        // onSignupFailed();
//                        progressDialog.dismiss();
//                    }
//                }, 3000);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
        String address = _addressText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();
        if (address.isEmpty()) {
            _addressText.setError("验证码不能为空");
            valid = false;
        } else {
            _addressText.setError(null);
        }

        if (mobile.isEmpty()) {
            _mobileText.setError("Enter Valid Mobile Number");
            valid = false;
        } else {
            _mobileText.setError(null);
        }

        if (password.isEmpty()) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }

    public void sendCode(String country, String phone) {
        // 注册一个事件回调，用于处理发送验证码操作的结果
        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // TODO 处理成功得到验证码的结果
                    signup();
                    // 请注意，此时只是完成了发送验证码的请求，验证码短信还需要几秒钟之后才送达
                } else {
                    // TODO 处理错误的结果
                    Toast.makeText(SignupActivity.this, "请重新尝试", Toast.LENGTH_LONG).show();
                }

            }
        });
        // 触发操作
        SMSSDK.getVerificationCode(country, phone);
    }


    // 提交验证码，其中的code表示验证码，如“1357”
    public void submitCode(String country, String phone, String code) {
        // 注册一个事件回调，用于处理提交验证码操作的结果
        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // TODO 处理验证成功的结果
                    signup();
                } else {
                    // TODO 处理错误的结果
                    Toast.makeText(SignupActivity.this, "验证码错误", Toast.LENGTH_LONG).show();
                }
            }
        });
        // 触发操作
        SMSSDK.submitVerificationCode(country, phone, code);
    }

    protected void onDestroy() {
        super.onDestroy();
        //用完回调要注销掉，否则可能会出现内存泄露
        SMSSDK.unregisterAllEventHandler();
    }


}