package com.tianheng.client.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dd.processbutton.FlatButton;
import com.tianheng.client.App;
import com.tianheng.client.R;
import com.tianheng.client.base.BaseActivity;
import com.tianheng.client.model.bean.MemberBean;
import com.tianheng.client.model.bean.UserBean;
import com.tianheng.client.model.event.LoginEvent;
import com.tianheng.client.model.event.MemberEvent;
import com.tianheng.client.presenter.UserPresenter;
import com.tianheng.client.presenter.contract.UserContract;
import com.tianheng.client.wedget.NumberKeyboardView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by huyg on 2018/1/6.
 */

public class UserActivity extends BaseActivity<UserPresenter> implements UserContract.View {


    @BindView(R.id.user_code)
    EditText mCode;
    @BindView(R.id.user_get_code)
    TextView mGetCode;
    @BindView(R.id.user_login)
    FlatButton mLogin;
    @BindView(R.id.user_phone)
    EditText mPhone;
    @BindView(R.id.user_qr_code)
    ImageView mQRCode;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.keyboard)
    NumberKeyboardView mKeyboard;


    @OnClick({R.id.user_get_code, R.id.user_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_get_code:
                getCode();
                break;
            case R.id.user_login:
                login();
                break;
        }
    }

    private int type;

    private void login() {
        String phone = mPhone.getText().toString().trim();
        String code = mCode.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            showContent("请输入手机号");
            return;
        }

        if (TextUtils.isEmpty(code)) {
            showContent("请输入验证码");
            return;
        }
        mPresenter.login(phone, code);


    }

    private void getCode() {
        String phone = mPhone.getText().toString().trim();
        if (!TextUtils.isEmpty(phone)) {
            mPresenter.getCode(phone);
            mGetCode.setClickable(false);
            mTimer.start();

        } else {
            showContent("请输入手机号");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void initInject() {
        getActivityComponent().inject(this);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_user;
    }

    @Override
    protected void init() {
        initView();
        initData();
    }

    private void initData() {
        String imei = App.getInstance().getImei();
        JPushInterface.setAlias(this, 0, imei);
        mPresenter.getQRCode(imei, 200, 200, "png");
    }

    private void initView() {
        getWindow().setLayout(1000, 800);
        initToolbar();
        initKeyboard();
        initEditText();
    }

    private void initEditText() {
        if (android.os.Build.VERSION.SDK_INT > 10) {//4.0以上 danielinbiti
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus",
                        boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(mPhone, false);
                setShowSoftInputOnFocus.invoke(mCode, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    type = 0;
                    //隐藏系统软键盘
                    InputMethodManager imm = (InputMethodManager) UserActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mPhone.getWindowToken(), 0);
                }
            }
        });

        mCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    type = 1;
                    //隐藏系统软键盘
                    InputMethodManager imm = (InputMethodManager) UserActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mCode.getWindowToken(), 0);
                }
            }
        });
    }

    private void initKeyboard() {
        mKeyboard.setOnNumberClickListener(new NumberKeyboardView.OnNumberClickListener() {
            @Override
            public void onNumberReturn(String number) {
                switch (type) {
                    case 0:
                        setTextContent(mPhone.getText().toString() + number);
                        break;
                    case 1:
                        setTextContent(mCode.getText().toString() + number);
                        break;
                }
            }

            @Override
            public void onNumberDelete() {
                switch (type) {
                    case 0:
                        int length = mPhone.getText().length();
                        if (length <= 1) {
                            setTextContent("");
                        } else {
                            setTextContent(mPhone.getText().toString().substring(0, length - 1));
                        }

                        break;
                    case 1:
                        int codeLength = mCode.getText().length();
                        if (codeLength <= 1) {
                            setTextContent("");
                        } else {
                            setTextContent(mCode.getText().toString().substring(0, codeLength - 1));
                        }
                        break;
                }
            }

            @Override
            public void onNumberDeleteAll() {
                setTextContent("");
            }
        });
    }

    private void setTextContent(String content) {
        switch (type) {
            case 0:
                mPhone.setText(content);
                mPhone.setSelection(content.length());
                break;
            case 1:
                mCode.setText(content);
                mCode.setSelection(content.length());
                break;
        }
    }

    private void initToolbar() {
        mToolbar.setTitle("请登录");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void showContent(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showQRImg(String url) {
        Glide.with(this).load(url).into(mQRCode);
    }

    @Override
    public void loginSuccess(UserBean userBean) {
        String ticket = userBean.getTicket();
        if (!TextUtils.isEmpty(ticket)) {
            App.getInstance().setTicket(ticket);
            EventBus.getDefault().post(new LoginEvent(userBean));
        }
        if (userBean.getManager() != null) {
            App.getInstance().setRoot(true);
        } else {
            App.getInstance().setRoot(false);
        }
        finish();
    }

    @Subscribe
    public void onEvent(MemberEvent event) {
        MemberBean memberBean = event.memberBean;
        if (memberBean != null) {
            String ticket = memberBean.getTicket();
            if (!TextUtils.isEmpty(ticket)) {
                App.getInstance().setTicket(ticket);
            }
            finish();
        }
    }

    private CountDownTimer mTimer = new CountDownTimer(60 * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            mGetCode.setText(millisUntilFinished / 1000 + "秒");
        }

        @Override
        public void onFinish() {
            mTimer.cancel();
            mGetCode.setText("获取验证码");
            mGetCode.setClickable(true);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
    }
}
