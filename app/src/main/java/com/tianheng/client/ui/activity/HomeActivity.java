package com.tianheng.client.ui.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tianheng.client.App;
import com.tianheng.client.R;
import com.tianheng.client.base.BaseActivity;
import com.tianheng.client.broad.CabinetManager;
import com.tianheng.client.model.bean.AdBean;
import com.tianheng.client.model.bean.BatteryInfo;
import com.tianheng.client.model.event.DoorErrorEvent;
import com.tianheng.client.model.frame.BMSFrame;
import com.tianheng.client.model.netty.SClientManager;
import com.tianheng.client.presenter.HomePresenter;
import com.tianheng.client.presenter.contract.HomeContract;
import com.tianheng.client.service.SClientService;
import com.tianheng.client.service.SerialPortService;
import com.tianheng.client.ui.fragment.OperateFragment;
import com.tianheng.client.util.DataUtils;
import com.tianheng.client.util.GlideImageLoader;
import com.tianheng.client.util.ToastUtil;
import com.tianheng.client.wedget.CustomerVideoView;
import com.tianheng.client.wedget.loading.ShapeLoadingDialog;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import cn.jpush.android.api.JPushInterface;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by huyg on 2017/12/25.
 */

public class HomeActivity extends BaseActivity<HomePresenter> implements HomeContract.View, OperateFragment.OperateListener {


    @BindView(R.id.fragment)
    FrameLayout mFrame;
    @BindView(R.id.video)
    CustomerVideoView mVideoView;

    private OperateFragment mOperateFragment;
    private FragmentManager mFragmentManager;
    private SClientService mService;
    private Intent serviceIn = new Intent();
    private Intent portIn = new Intent();
    private Intent mIntent = new Intent();
    private TextView mSubView;
    private TextView mTitleView;
    private List<String> images = new ArrayList<>();
    private SerialPortService mPortService;
    private SClientService mClientService;
    private ShapeLoadingDialog mDialog;
    private ShapeLoadingDialog.Builder mBuilder;
    private CabinetManager mCabinetManager;
    private Disposable disposable;
    private Intent mDataIntent = new Intent();
    private List<Uri> mUris = new ArrayList<>();
    private int i = 0;
    private static final String TAG ="HomeActivity";

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
        initBroad();
        initIntent();
        initService();
        initData();
        initView();
        initCabinetManager();
        initDispose();
    }

    private void initDispose() {
        if (disposable == null || disposable.isDisposed()) {
            disposable = Observable.interval(0, 20, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            if (TextUtils.isEmpty(App.getInstance().getTicket())) {
                                mVideoView.setVisibility(View.VISIBLE);
                            } else {
                                mVideoView.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }

    private void initIntent() {
        mDataIntent.setAction("com.yunma.netty");

    }


    private void initBroad() {
        Intent intent = new Intent();
        intent.setAction("com.yunma.start");
        sendBroadcast(intent);
    }

    private void initCabinetManager() {
        mCabinetManager = new CabinetManager(this);
    }

    private void initData() {
        getImei();
        initVideoPath();
    }


    private void initVideoPath() {
        mUris.clear();
        mUris.add(Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.video_1_1));
        mUris.add(Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.video_1_2));
        mUris.add(Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.video_1_3));
        mUris.add(Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.video_1_4));
        mUris.add(Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.video_1_5));
        mUris.add(Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.video_1_6));
        mUris.add(Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.video_1_7));
        mUris.add(Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.video_1_8));
        mUris.add(Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.video_1_9));
        mUris.add(Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.video_2_1));
        mUris.add(Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.video_2_2));
        mUris.add(Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.video_2_3));
        mUris.add(Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.video_3_1));
        mUris.add(Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.video_3_2));
        mUris.add(Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.video_3_3));
        mUris.add(Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.video_3_4));
        mUris.add(Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.video_3_5));
        mUris.add(Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.video_3_6));
        mUris.add(Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.video_3_7));
        mUris.add(Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.video_3_8));
        mUris.add(Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.video_4_1));
        mUris.add(Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.video_4_2));
        mUris.add(Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.video_4_3));
        mUris.add(Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.video_4_4));
        mUris.add(Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.video_4_5));
        mUris.add(Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.video_4_10));
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
        initVideoView();
        initFragment();
        initDialog();
    }


    private void initVideoView() {
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d(TAG,"onPrepared");
                mVideoView.start();
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG,"onCompletion");
                i++;
                if (i == mUris.size()) {
                    i = 0;
                }
                mVideoView.setVideoURI(mUris.get(i));
                mVideoView.start();
            }
        });
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                stopPlaybackVideo();
                Log.d(TAG,"onError");
                return true;
            }
        });

        mVideoView.setVideoURI(mUris.get(i));

        mVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG,"onTouch");
                mVideoView.setVisibility(View.GONE);
                return false;
            }
        });
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
            mOperateFragment.initDispose();
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


    private void initDialog() {
        mBuilder = new ShapeLoadingDialog.Builder(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mVideoView.isPlaying()) {
            mVideoView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView.canPause()) {
            mVideoView.pause();
        }
    }

    @Override
    public void showDialog(String message) {
        mDialog = mBuilder.loadText(message).show();
    }

    @Override
    public void closeDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    @Override
    public void sendFrame(byte[] frame) {
        Log.d("HomeActivity", "sendFrame--->" + DataUtils.bytes2HexString(frame, frame.length));
        mPortService.sendData(frame);
    }

    @Override
    public void sendFrame(BMSFrame bmsFrame, String frame) {
        BatteryInfo batteryInfo = new BatteryInfo();
        batteryInfo.setType(1);
        batteryInfo.setCabinetNumber(App.getInstance().getImei());
        batteryInfo.setBatteryNumber(bmsFrame.code);
        batteryInfo.setBoxNumber(bmsFrame.pageNo);
        batteryInfo.setCurPower(getPower(bmsFrame));
        batteryInfo.setCurVoltage(sumVol(bmsFrame));
        batteryInfo.setChargerStatus(resolveStatus(bmsFrame));
        batteryInfo.setFrame(frame);
        String newFrame = new Gson().toJson(batteryInfo);
        mDataIntent.putExtra("data", newFrame);
        sendBroadcast(mDataIntent);
    }

    @Override
    public void sendEmptyBox(int boxNum) {
        BatteryInfo batteryInfo = new BatteryInfo();
        batteryInfo.setType(1);
        batteryInfo.setCabinetNumber(App.getInstance().getImei());
        batteryInfo.setBatteryNumber("");
        batteryInfo.setBoxNumber(boxNum);
        batteryInfo.setCurPower(0);
        batteryInfo.setCurVoltage(0);
        batteryInfo.setChargerStatus(2);
        batteryInfo.setFrame("");
        String frame = new Gson().toJson(batteryInfo);
        mDataIntent.putExtra("data", frame);
        sendBroadcast(mDataIntent);
    }


    @Override
    public void showContent(String message) {
        ToastUtil.showShort(this, message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlaybackVideo();
        if (disposable != null) {
            disposable.dispose();
        }

        // unbindService(mClientConn);
        //unbindService(mConnection);
    }

    private void stopPlaybackVideo() {
        try {
            mVideoView.stopPlayback();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public int getPower(BMSFrame bmsFrame) {
        float capacity = Integer.parseInt(bmsFrame.capacity, 16);
        float capacitySum = Integer.parseInt(bmsFrame.capacitySum, 16);
        int power = (int) (capacity / capacitySum * 100);
        return power;
    }


    public int resolveStatus(BMSFrame bmsFrame) {
        int status;
        String statusStr = bmsFrame.status;
        if ("00000000".equals(statusStr)) {
            status = 1;//充满
        } else if ("00000001".equals(statusStr)) {
            status = 0;//充电中
        } else {
            status = 2;
        }
        return status;
    }

    public double sumVol(BMSFrame bmsFrame) {
        double sum = 0;
        if (bmsFrame != null) {
            sum = Integer.parseInt(bmsFrame.voltage1, 16) +
                    Integer.parseInt(bmsFrame.voltage2, 16) +
                    Integer.parseInt(bmsFrame.voltage3, 16) +
                    Integer.parseInt(bmsFrame.voltage4, 16) +
                    Integer.parseInt(bmsFrame.voltage5, 16) +
                    Integer.parseInt(bmsFrame.voltage6, 16) +
                    Integer.parseInt(bmsFrame.voltage7, 16) +
                    Integer.parseInt(bmsFrame.voltage8, 16) +
                    Integer.parseInt(bmsFrame.voltage9, 16) +
                    Integer.parseInt(bmsFrame.voltage10, 16) +
                    Integer.parseInt(bmsFrame.voltage11, 16) +
                    Integer.parseInt(bmsFrame.voltage12, 16) +
                    Integer.parseInt(bmsFrame.voltage13, 16) +
                    Integer.parseInt(bmsFrame.voltage14, 16) +
                    Integer.parseInt(bmsFrame.voltage15, 16) +
                    Integer.parseInt(bmsFrame.voltage16, 16)
            ;
        }
        sum = sum / 1000.0;
        return sum;
    }
}
