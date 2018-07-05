package com.tianheng.client.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;
import com.rey.material.widget.Button;
import com.tianheng.client.App;
import com.tianheng.client.R;
import com.tianheng.client.base.BaseFragment;
import com.tianheng.client.broad.CabinetManager;
import com.tianheng.client.global.Const;
import com.tianheng.client.model.bean.BatteryBean;
import com.tianheng.client.model.bean.ExchangeBean;
import com.tianheng.client.model.bean.MemberBean;
import com.tianheng.client.model.bean.UserBean;
import com.tianheng.client.model.event.DoorStatusEvent;
import com.tianheng.client.model.event.DoorsStatusEvent;
import com.tianheng.client.model.event.LoginEvent;
import com.tianheng.client.model.event.MemberEvent;
import com.tianheng.client.model.event.OpenDoorEvent;
import com.tianheng.client.model.frame.BMSFrame;
import com.tianheng.client.model.frame.BatteryFrame;
import com.tianheng.client.model.frame.ForwardFrame;
import com.tianheng.client.presenter.OperatePresenter;
import com.tianheng.client.presenter.contract.OperateContract;
import com.tianheng.client.ui.activity.UserActivity;
import com.tianheng.client.util.CheckUtil;
import com.tianheng.client.util.CommonGlideImageLoader;
import com.tianheng.client.util.DataUtils;
import com.tianheng.client.util.DecodeFrame;
import com.tianheng.client.util.EncodeFrame;
import com.tianheng.client.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by huyg on 2017/12/26.
 * status
 * 0：初始状态
 * 1：打开空箱子
 */

public class OperateFragment extends BaseFragment<OperatePresenter> implements OperateContract.View {

    @BindView(R.id.message)
    TextView mMessage;
    @BindView(R.id.control)
    Button mControl;
    @BindView(R.id.avatar)
    ImageView mAvatar;
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.wallet)
    TextView mWallet;
    @BindView(R.id.login_out)
    Button mLoginOut;
    @BindView(R.id.user_detail)
    LinearLayout mDetail;


    @OnClick({R.id.control, R.id.login_out})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.control:
                if (TextUtils.isEmpty(App.getInstance().getTicket())) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), UserActivity.class);
                    startActivity(intent);
                } else {
                    //更换
                    mPresenter.exchange();
                }
                break;
            case R.id.login_out:
                mPresenter.loginOut(App.getInstance().getTicket());
                break;
        }
    }

    private CabinetManager mCabinetManager;
    private int status = -1;//初始状态
    private ExchangeBean mExchangeBean;
    private static final String TAG = "OperateFragment";

    private OperateListener mListener;
    private StringBuilder frameBuilder = new StringBuilder();//查询帧
    private int residue;//最后一包有几位是需要的数据
    private int sum;//总包数
    private MyHandler mHandler = new MyHandler();
    private Timer timer = new Timer();
    private String lastPageNo = "";
    private Disposable disposable;
    private String deviceNo;

    @Override
    protected void initInject() {
        getFragmentComponent().inject(this);
    }

    public static OperateFragment newInstance() {
        OperateFragment OperateFragment = new OperateFragment();
        return OperateFragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_message;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OperateListener) context;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void init() {
        initView();
        initData();
        initCabinetManager();
    }

    private void initCabinetManager() {
        mCabinetManager = new CabinetManager(getActivity());
    }

    private void initData() {

    }

    public void schedule() {
        //定时上传数据
        //timer.schedule(task, 0, 60*60*5 * 1000);
        searchPackage("1");
    }

    private void initView() {
    }


    @Override
    public void showContent(String message) {
        ToastUtil.showShort(getActivity(), message);
    }


    @Subscribe
    public void onEvent(LoginEvent event) {
        UserBean userBean = event.userBean;
        if (userBean != null) {
            mDetail.setVisibility(View.VISIBLE);
            if (userBean.getMember() != null) {
                CommonGlideImageLoader.getInstance().displayNetImageWithCircle(getActivity(), userBean.getMember().getAvatar(), mAvatar, ContextCompat.getDrawable(getActivity(), R.drawable.avatar_def));
                if (!TextUtils.isEmpty(userBean.getMember().getName())) {
                    mName.setText(userBean.getMember().getName());
                }
                mWallet.setText("" + userBean.getMember().getWalletMoney());
            }

        }
    }

    @Subscribe
    public void onEvent(MemberEvent event) {
        MemberBean memberBean = event.memberBean;
        if (memberBean != null) {
            mDetail.setVisibility(View.VISIBLE);
            CommonGlideImageLoader.getInstance().displayNetImageWithCircle(getActivity(), memberBean.getAvatar(), mAvatar, ContextCompat.getDrawable(getActivity(), R.drawable.avatar_def));
            if (!TextUtils.isEmpty(memberBean.getName())) {
                mName.setText(memberBean.getName());
            }
            mWallet.setText(String.valueOf(memberBean.getWalletMoney()));
            App.getInstance().setTicket(memberBean.getTicket());
        }
    }


    @Override
    public void openDoor(ExchangeBean exchangeBean) {
        mCabinetManager.openDoor(0, exchangeBean.getEmptyBoxNumber());
        mListener.showDialog("正在打开箱门，请稍后...");
        this.mExchangeBean = exchangeBean;
        status = 1;//操作状态
    }

    @Override
    public void putOldSuccess() {
        //打开新的柜子；
        mCabinetManager.openDoor(0, 2);
    }

    @Override
    public void logoutSuccess() {
        mDetail.setVisibility(View.GONE);
        App.getInstance().setTicket("");

    }

    @Subscribe
    public void onEvent(DoorStatusEvent event) {
        boolean isOpen = event.isOpen;
        if (!isOpen && status == 4 && event.iLockId == mExchangeBean.getEmptyBoxNumber()) {
//            mPresenter.closeNew("5");
            if (disposable != null) {
                disposable.dispose();
            }
            mListener.showDialog("打开柜门中...");
            mCabinetManager.openDoor(0, mExchangeBean.getExchangeBoxNumber());
            status = 5;
        } else if (!isOpen && status == 7 && event.iLockId == mExchangeBean.getExchangeBoxNumber()) {
            status = -1;//完成操作
            mListener.closeDialog();
            ToastUtil.show(getActivity(), "取电池成功，完成交易", Toast.LENGTH_SHORT);
        }
    }

    @Subscribe
    public void onEvent(OpenDoorEvent event) {
        boolean isOpen = event.isOpen;
        if (isOpen) {
            if (status == 1&& event.iLockId == mExchangeBean.getEmptyBoxNumber()) {
                mPresenter.speak("箱门已经打开，请放入电池");
                mListener.showDialog("请放入电池");
                status = 2;
            } else if (status == 3) {
                mListener.showDialog("请重新插入电池");
                status = 2;
                mPresenter.speak("箱门已经打开，请取出新电池，并关闭箱门");
            } else if (status == 5 && event.iLockId == mExchangeBean.getExchangeBoxNumber()) {
                status = 6;
                mListener.showDialog("请取出新电池,并关闭箱门");
                mPresenter.speak("箱门已经打开，请取出新电池，并关闭箱门");
            }
        }

    }

    @Subscribe
    public void onEvent(BatteryFrame batteryFrame) {
        if (batteryFrame != null) {
            if (status == 2 && batteryFrame.bms.equals("00") && Integer.parseInt(batteryFrame.device) == mExchangeBean.getEmptyBoxNumber()) {
                //故障检测
                status = 3;//电池插入
                mListener.showDialog("电池已放入，正在检测...");
                searchPackage(String.valueOf(mExchangeBean.getEmptyBoxNumber()));
            } else if (status == 6 && Integer.parseInt(batteryFrame.device) == mExchangeBean.getExchangeBoxNumber() &&
                    batteryFrame.bms.equals("01")) {
                status = 7;
                mListener.showDialog("电池已取出，请关闭箱门");
                if (disposable != null) {
                    disposable.dispose();
                }
                disposable = Observable.interval(0, 1, TimeUnit.SECONDS)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                mCabinetManager.getDoorStatus(0, mExchangeBean.getExchangeBoxNumber());
                            }
                        });
            } else {
                //正常上报
            }
        }
    }

    //校验
    @Subscribe
    public void onEvent(ForwardFrame forwardFrame) {
        if (Integer.parseInt(forwardFrame.device)==(Integer.parseInt(deviceNo))) {
            if (forwardFrame.data.startsWith(Const.FRAME_78)) {
                String length = forwardFrame.data.substring(2, 4);
                int frameL = Integer.parseInt(length, 16);
                sum = (int) Math.ceil(frameL / 7.0);
                residue = frameL % 7;
            }
            String packNo = Integer.toHexString(sum - 1);
            if (packNo.length() == 1) {
                packNo = "0" + packNo;
            }
            if (!forwardFrame.packNo.equals(lastPageNo)) {
                if (forwardFrame.packNo.equals(packNo)) {
                    if (residue != 0) {
                        frameBuilder.append(forwardFrame.data.substring(0, residue * 2));
                    } else {
                        frameBuilder.append(forwardFrame.data);
                    }
                    Message message = Message.obtain();
                    message.what = 1;
                    message.arg1 = Integer.parseInt(forwardFrame.device, 16);
                    message.obj = frameBuilder.toString();
                    Log.d("11111","1111->"+frameBuilder.toString());
                    mHandler.sendMessage(message);
                    frameBuilder.delete(0, frameBuilder.length());
                } else {

                    frameBuilder.append(forwardFrame.data);
                }
                lastPageNo = forwardFrame.packNo;
            }
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (timer != null) {
            timer.cancel();
        }
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String newFrame = (String) msg.obj;
                    Log.d(TAG, "newFrame" + newFrame);
                    //校验
//                    newFrame = "783C1808F70001A181000000008000000010241025102810261028102710251026102410221025101F1010221025101F101F10251024102215141413000000C800C86D";
//                    byte[] bytes = DataUtils.HexString2Bytes(newFrame);
//                    byte verify = DataUtils.checkData(bytes, 0, bytes.length - 1);//校验码
//                    Log.d(TAG, "verify" + verify);
                    BMSFrame bmsFrame = DecodeFrame.decodeBmsFrame(newFrame);
                    if (bmsFrame != null) {
                        bmsFrame.pageNo = msg.arg1;
                        mListener.sendFrame(bmsFrame);//上传到服务器
                        EventBus.getDefault().post(bmsFrame);
                    }

                    //(bmsFrame.code.equals(mExchangeBean.getLeaseBatteryNumber()) &&
                    if (bmsFrame != null && status == 3) {
                        if (CheckUtil.checkBattery(bmsFrame)) {
                            status = 4;//电池放入完成
//                            mPresenter.putInOld();
                            mListener.showDialog("电池检测成功，请关闭箱门");
//                            mCabinetManager.openDoor(0,5);

                            mCabinetManager.getDoorStatus(0, mExchangeBean.getEmptyBoxNumber());
//                            searchPackage("0");
                            if (disposable != null) {
                                disposable.dispose();
                            }
                            disposable = Observable.interval(0, 1, TimeUnit.SECONDS)
                                    .subscribeOn(AndroidSchedulers.mainThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<Long>() {
                                        @Override
                                        public void accept(Long aLong) throws Exception {
                                            mCabinetManager.getDoorStatus(0, mExchangeBean.getEmptyBoxNumber());
                                        }
                                    });
                        }else{
                            mListener.showDialog("电池检测失败");
                            mCabinetManager.openDoor(0,mExchangeBean.getEmptyBoxNumber());
                        }
                    }
                    break;
            }
        }
    }


    public interface OperateListener {
        void showDialog(String message);

        void closeDialog();

        void sendFrame(byte[] frame);

        void sendFrame(BMSFrame bmsFrame);

    }


    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if (status == -1) {
                searchPackage("1");
            }
        }
    };

    private void searchPackage(String deviceNo) {
        try {
            this.deviceNo = deviceNo;
            byte[] frame;
            frame = EncodeFrame.selectFirst(deviceNo);
            mListener.sendFrame(frame);
            for (int i = 0; i < 9; i++) {
                Thread.sleep(1000);
                frame = EncodeFrame.selectByPageNo(deviceNo, String.valueOf(i));
                mListener.sendFrame(frame);
            }
            Thread.sleep(1000);
            frame = EncodeFrame.selectEnd(deviceNo);
            mListener.sendFrame(frame);
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
