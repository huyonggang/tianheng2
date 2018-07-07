package com.tianheng.client.ui.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianheng.client.App;
import com.tianheng.client.R;
import com.tianheng.client.base.BaseActivity;
import com.tianheng.client.broad.CabinetManager;
import com.tianheng.client.model.event.DoorErrorEvent;
import com.tianheng.client.model.frame.BMSFrame;
import com.tianheng.client.presenter.HomePresenter;
import com.tianheng.client.presenter.contract.HomeContract;
import com.tianheng.client.service.SClientService;
import com.tianheng.client.service.SerialPortService;
import com.tianheng.client.ui.fragment.BatteryFragment;
import com.tianheng.client.ui.fragment.OperateFragment;
import com.tianheng.client.util.DataUtils;
import com.tianheng.client.util.GlideImageLoader;
import com.tianheng.client.util.ToastUtil;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import cn.jpush.android.api.JPushInterface;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.netty.handler.codec.base64.Base64;

/**
 * Created by huyg on 2017/12/25.
 */

public class HomeActivity extends BaseActivity<HomePresenter> implements HomeContract.View, OperateFragment.OperateListener {


    @BindView(R.id.home_left)
    FrameLayout mLeft;
    @BindView(R.id.home_right)
    FrameLayout mRight;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.banner)
    Banner mBanner;

    private OperateFragment mOperateFragment;
    private BatteryFragment mBatteryFragment;
    private FragmentManager mFragmentManager;
    private SClientService mService;
    private Intent serviceIn = new Intent();
    private Intent portIn = new Intent();
    private Intent mIntent = new Intent();
    private TextView mSubView;
    private TextView mTitleView;
    private MyHandler mHander = new MyHandler();
    private List<String> images = null;
    private SerialPortService mPortService;
    private SClientService mClientService;
    private SweetAlertDialog mDialog;
    private CabinetManager mCabinetManager;

    @Override
    protected void initInject() {
        getActivityComponent().inject(this);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_home;
    }

    @Override
    protected void init() {
        mHander.sendEmptyMessageAtTime(0, 10 * 1000);
        initService();
        initData();
        initView();
        initCabinetManager();
    }

    private void initCabinetManager() {
        mCabinetManager = new CabinetManager(this);
    }

    private void initData() {
        getImei();
        mPresenter.getPicture(4);
    }

    private void getImei() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        String imei = telephonyManager.getDeviceId();
        App.getInstance().setImei(imei);
    }

    private void initService() {
        serviceIn.setClass(this, SClientService.class);
        portIn.setClass(this, SerialPortService.class);
        bindService(portIn, mConnection, BIND_AUTO_CREATE);
        bindService(serviceIn, mClientConn, BIND_AUTO_CREATE);
    }

    private void initFragment() {
        mOperateFragment = OperateFragment.newInstance();
        mBatteryFragment = BatteryFragment.newInstance();
        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(R.id.home_left, mOperateFragment);
        ft.replace(R.id.home_right, mBatteryFragment);
        ft.commit();
    }

    private void initView() {
        initToolbar();
        initFragment();
        initBanner();
        initDialog();
    }

    private ServiceConnection mClientConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SClientService.MyBind bind = (SClientService.MyBind) service;
            mClientService = bind.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SerialPortService.MyBind bind = (SerialPortService.MyBind) service;
            mPortService = bind.getService();
            mOperateFragment.schedule();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Subscribe
    public void onEvent(DoorErrorEvent errorEvent) {
        closeDialog();
        ToastUtil.showShort(this, "开启箱门失败");
    }


    private void initBanner() {
        mBanner.setImageLoader(new GlideImageLoader());
        //设置自动轮播，默认为true
        mBanner.isAutoPlay(true);
        //设置轮播时间
        mBanner.setDelayTime(2 * 1000);
        //设置指示器位置（当banner模式中有指示器时）
        mBanner.setIndicatorGravity(BannerConfig.RIGHT);
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        mBanner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                mBanner.setVisibility(View.GONE);
                mCabinetManager.getDoorsStatus(0,8);
            }
        });
    }

    private void initToolbar() {
        mSubView = new TextView(this);
        mSubView.setTextSize(40);
        mSubView.setTextColor(Color.WHITE);
        mSubView.setText("设置");
        //设定布局的各种参数
        Toolbar.LayoutParams params = new Toolbar.LayoutParams(
                Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT,
                Gravity.RIGHT);
        params.rightMargin = 30;
        mSubView.setLayoutParams(params);
        mTitleView = new TextView(this);
        mTitleView.setTextSize(40);
        mTitleView.setTextColor(Color.WHITE);
        mTitleView.setText("天恒新能源");
        //设定布局的各种参数
        Toolbar.LayoutParams titleParams = new Toolbar.LayoutParams(
                Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);
        mTitleView.setLayoutParams(titleParams);

        mToolbar.addView(mSubView);
        mToolbar.addView(mTitleView);
//        mSubView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
//                startActivity(intent);
//            }
//        });
        mSubView.setVisibility(View.GONE);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        finish();
    }


    private void initDialog() {
        mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        mDialog.setCancelable(false);

    }

    @Override
    protected void onResume() {
        super.onResume();
//        mSubView.setVisibility(View.VISIBLE);
//        if (App.getInstance().isRoot()) {
//            mSubView.setVisibility(View.VISIBLE);
//        } else {
//            mSubView.setVisibility(View.GONE);
//        }
    }

    @Override
    public void showDialog(String message) {
        mDialog.setTitleText(message);
        mDialog.show();
    }

    @Override
    public void closeDialog() {
        mDialog.dismiss();
    }

    @Override
    public void sendFrame(byte[] frame) {
        Log.d("HomeActivity","sendFrame--->"+ DataUtils.bytes2HexString(frame,frame.length));
        mPortService.sendData(frame);
    }

    @Override
    public void sendFrame(BMSFrame bmsFrame) {
        mClientService.sendFrame(bmsFrame);
    }



    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (TextUtils.isEmpty(App.getInstance().getTicket()) && images != null && images.size() > 0) {
                        mBanner.setVisibility(View.VISIBLE);
                    } else {

                    }
                    break;
            }


        }
    }

    @Override
    public void showContent(String message) {
        ToastUtil.showShort(this, message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mClientConn);
        unbindService(mConnection);
    }

    @Override
    public void showImage(List<String> images) {
        if (images.size() > 0 && TextUtils.isEmpty(App.getInstance().getTicket())) {
            this.images = images;
            mBanner.setImages(images);
            mBanner.start();
        }
    }

}
