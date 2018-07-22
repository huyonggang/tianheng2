package com.tianheng.client.ui.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
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

/**
 * Created by huyg on 2017/12/25.
 */

public class HomeActivity extends BaseActivity<HomePresenter> implements HomeContract.View, OperateFragment.OperateListener {


    @BindView(R.id.fragment)
    FrameLayout mFrame;
    @BindView(R.id.banner)
    Banner mBanner;

    private OperateFragment mOperateFragment;
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
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
            return;
        }
        String imei = telephonyManager.getDeviceId();
        App.getInstance().setImei(imei);
        JPushInterface.setAlias(this, 0, imei);
    }

    private void initService() {
        serviceIn.setClass(this, SClientService.class);
        portIn.setClass(this, SerialPortService.class);
        //bindService(portIn, mConnection, BIND_AUTO_CREATE);
        //bindService(serviceIn, mClientConn, BIND_AUTO_CREATE);
    }

    private void initFragment() {
        mOperateFragment = OperateFragment.newInstance();
        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(R.id.fragment, mOperateFragment);
        ft.commit();
    }

    private void initView() {
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
            }
        });
    }


    private void initDialog() {
        mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        mDialog.setCancelable(false);

    }

    @Override
    protected void onResume() {
        super.onResume();
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

    @Override
    public void sendEmptyBox(int boxNum) {
        mClientService.sendEmptyBox(boxNum);
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
