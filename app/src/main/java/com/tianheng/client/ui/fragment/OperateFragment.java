package com.tianheng.client.ui.fragment;

import android.content.Context;
import android.content.Intent;
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

import com.rey.material.widget.Button;
import com.tianheng.client.App;
import com.tianheng.client.R;
import com.tianheng.client.base.BaseFragment;
import com.tianheng.client.broad.CabinetManager;
import com.tianheng.client.global.Const;
import com.tianheng.client.model.bean.BoxStatus;
import com.tianheng.client.model.bean.ExchangeBean;
import com.tianheng.client.model.bean.MemberBean;
import com.tianheng.client.model.bean.OrderBean;
import com.tianheng.client.model.bean.UserBean;
import com.tianheng.client.model.event.DoorStatusEvent;
import com.tianheng.client.model.event.GoodStatusEvent;
import com.tianheng.client.model.event.GoodsStatusEvent;
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
import com.tianheng.client.util.DecodeFrame;
import com.tianheng.client.util.EncodeFrame;
import com.tianheng.client.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

    private final int STATE_INIT = -1;
    private final int STATE_DETECT_DOOR = 1;


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
    private Disposable disposable2;
    private int deviceNo;
    private OrderBean orderBean;
    private List<BoxStatus> boxStatuses = new ArrayList<>();
    private boolean oldFlag;
    private boolean newFlag;

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
        for (int i = 0; i < 8; i++) {
            BoxStatus boxStatus = new BoxStatus();
            boxStatus.setEmpty(true);
            boxStatuses.add(boxStatus);
        }
    }

    public void schedule() {
        //定时上传数据
        timer.schedule(task, 10*1000, 5 * 60 * 60 * 1000);
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
                BigDecimal bigDecimal = new BigDecimal(userBean.getMember().getWalletMoney()).subtract(new BigDecimal(userBean.getMember().getTradeMoney()));
                mWallet.setText(String.valueOf(bigDecimal.setScale(2)));
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

//    @Subscribe
//    public void onEvent(GoodStatusEvent event) {
//        boolean isGoods = event.isGoods;
//        if (status == STATE_DETECT_DOOR && isGoods && event.iLockId == mExchangeBean.getEmptyBoxNumber()) {
//            ToastUtil.showShort(mContext, "箱内已有电池,请重新操作");
//        } else {
//            mCabinetManager.openDoor(0, mExchangeBean.getEmptyBoxNumber());
//        }
//    }

    @Subscribe
    public void onEvent(DoorStatusEvent event) {

        boolean isOpen = event.isOpen;
        Log.d(TAG,"isOpen"+isOpen);
        if (!isOpen) {
            if (status == 4 && event.iLockId == mExchangeBean.getEmptyBoxNumber()) {
                if (disposable != null) {
                    disposable.dispose();
                }
                mPresenter.closeOld(1, mExchangeBean.getLeaseBatteryNumber(), mExchangeBean.getEmptyBoxNumber(), mExchangeBean.getExchangeBoxNumber(), mExchangeBean.getExchangeBatteryNumber());
                status = 5;
            } else if (status == 7 && event.iLockId == mExchangeBean.getExchangeBoxNumber()) {
                mPresenter.closeNew(orderBean.getOldOrderId());
            } else if (status == 2&& event.iLockId == mExchangeBean.getEmptyBoxNumber()) {
//                mCabinetManager.openDoor(0, mExchangeBean.getEmptyBoxNumber());
//                sendShowMessage("请重新放入电池");
                sendCloseMessage();
                status = -1;
            } else if (status == 6 && event.iLockId == mExchangeBean.getExchangeBoxNumber()) {
                mCabinetManager.openDoor(0, mExchangeBean.getExchangeBoxNumber());
                sendShowMessage("请取出电池");
            }
        }


    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(OpenDoorEvent event) {
        boolean isOpen = event.isOpen;
        if (isOpen) {
            Log.d(TAG,"status ="+status);
            if (status == 1 && event.iLockId == mExchangeBean.getEmptyBoxNumber()) {
//                mPresenter.speak("箱门已经打开，请放入电池");
                sendShowMessage("请放入电池");
                status = 2;
                if (disposable != null) {
                    disposable.dispose();
                }
                disposable = Observable.interval(10, 3, TimeUnit.SECONDS)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                if (oldFlag){
                                    mCabinetManager.getDoorStatus(0, mExchangeBean.getEmptyBoxNumber());
                                }

                            }
                        });

            }
//            else if (status == 3) {
//                Message message = Message.obtain();
//                message.obj = "请重新插入电池";
//                message.what = 3;
//                mHandler.sendMessage(message);
//                status = 2;
//                mPresenter.speak("箱门已经打开，请取出新电池，并关闭箱门");
//            }
            else if (status == 5 && event.iLockId == mExchangeBean.getExchangeBoxNumber()) {
                status = 6;
                sendShowMessage("请取出电池,并关闭箱门");
                mPresenter.speak("箱门已经打开，请取出新电池，并关闭箱门");
                if (disposable != null) {
                    disposable.dispose();
                }
                disposable = Observable.interval(2, 3, TimeUnit.SECONDS)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                mCabinetManager.getDoorStatus(0, mExchangeBean.getExchangeBoxNumber());
                            }
                        });
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(BatteryFrame batteryFrame) {
        if (batteryFrame != null) {
            if (status==-1){
                if (batteryFrame.bms.equals("01")){
                    boxStatuses.get(Integer.parseInt(batteryFrame.device)).setEmpty(true);
                }else {
                    boxStatuses.get(Integer.parseInt(batteryFrame.device)).setEmpty(true);
                }

            } else if (status == 2 && batteryFrame.bms.equals("00") && Integer.parseInt(batteryFrame.device) == mExchangeBean.getEmptyBoxNumber()) {
                status = 3;//电池插入
                if (disposable != null) {
                    disposable.dispose();
                }
                //故障检测
                sendShowMessage("电池已放入，正在检测...");
                searchPackage(mExchangeBean.getEmptyBoxNumber());
            } else if (status == 6 && Integer.parseInt(batteryFrame.device) == mExchangeBean.getExchangeBoxNumber() &&
                    batteryFrame.bms.equals("01")) {
                if (disposable != null) {
                    disposable.dispose();
                }
                status = 7;
                mPresenter.takeoutNew(orderBean.getOldOrderId());

            } else if (status == 6 && Integer.parseInt(batteryFrame.device) == mExchangeBean.getExchangeBoxNumber() &&
                    batteryFrame.bms.equals("00")) {
                if (disposable != null) {
                    disposable.dispose();
                }
                //mPresenter.putInNew(orderBean.getOldOrderId());
            } else if ((status == 4 || status == 3) && Integer.parseInt(batteryFrame.device) == mExchangeBean.getEmptyBoxNumber() &&
                    batteryFrame.bms.equals("01")) {
                //检测成功或正在检测后拔出
                if (disposable != null) {
                    disposable.dispose();
                }
                status = 2;
                mPresenter.takeoutOld();
                sendShowMessage("请插入电池");
            }
        }
    }

    @Subscribe
    public void onEvent(ForwardFrame forwardFrame) {
        if (Integer.parseInt(forwardFrame.device) == deviceNo) {
            Log.d(TAG, "forwardFrame.data"+forwardFrame.data);
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
            Log.d(TAG, "packNo" + forwardFrame.packNo);
            Log.d(TAG, "lastPageNo" + lastPageNo);
            if (!forwardFrame.packNo.equals(lastPageNo)) {
                if (forwardFrame.packNo.equals(packNo)) {
                    Log.d(TAG, "append" + forwardFrame.packNo);
                    if (residue != 0) {
                        frameBuilder.append(forwardFrame.data.substring(0, residue * 2));
                    } else {
                        frameBuilder.append(forwardFrame.data);
                    }
                    Message message = Message.obtain();
                    message.what = 1;
                    message.arg1 = Integer.parseInt(forwardFrame.device, 16);
                    message.obj = frameBuilder.toString();
                    Log.d("11111", "1111->" + frameBuilder.toString());
                    mHandler.sendMessage(message);
                    frameBuilder.delete(0, frameBuilder.length());
                } else {
                    Log.d(TAG, "append" + forwardFrame.packNo);
                    frameBuilder.append(forwardFrame.data);
                }
                lastPageNo = forwardFrame.packNo;
            }
        }
    }


    @Override
    public void openDoor(ExchangeBean exchangeBean) {
//        mCabinetManager.getGoodStatus(0, exchangeBean.getEmptyBoxNumber());
        this.mExchangeBean = exchangeBean;
        sendShowMessage("正在打开箱门，请稍后...");
        status = 1;//操作状态
        mCabinetManager.openDoor(0, mExchangeBean.getEmptyBoxNumber());
    }

    @Override
    public void putOldSuccess() {
        mCabinetManager.getDoorStatus(0, mExchangeBean.getEmptyBoxNumber());
        if (disposable != null) {
            disposable.dispose();
        }
        disposable = Observable.interval(2, 3, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        mCabinetManager.getDoorStatus(0, mExchangeBean.getEmptyBoxNumber());
                    }
                });
    }

    @Override
    public void closeOldSuccess(OrderBean orderBean) {
        this.orderBean = orderBean;
        sendShowMessage("打开柜门中...");
        mCabinetManager.openDoor(0, mExchangeBean.getExchangeBoxNumber());
    }

    @Override
    public void logoutSuccess() {
        mDetail.setVisibility(View.GONE);
        App.getInstance().setTicket("");

    }

    @Override
    public void takeoutNewSuccess() {
        sendShowMessage("电池已取出，请关闭箱门");
        if (disposable != null) {
            disposable.dispose();
        }
        disposable = Observable.interval(2, 3, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        mCabinetManager.getDoorStatus(0, mExchangeBean.getExchangeBoxNumber());
                    }
                });
    }

    @Override
    public void closeNewSuccess() {
        status = -1;//完成操作
        mPresenter.getMemberDetail();
        ToastUtil.show(getActivity(), "取电池成功，完成交易", Toast.LENGTH_SHORT);
        sendCloseMessage();
    }

    @Override
    public void showMemberDetail(MemberBean memberBean) {
        if (!TextUtils.isEmpty(memberBean.getName())) {
            mName.setText(memberBean.getName());
        }
        BigDecimal bigDecimal = new BigDecimal(memberBean.getWalletMoney()).subtract(new BigDecimal(memberBean.getTradeMoney()));
        mWallet.setText(String.valueOf(bigDecimal.setScale(2)));
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
                    BMSFrame bmsFrame = DecodeFrame.decodeBmsFrame(newFrame);
                    if (bmsFrame != null) {
                        bmsFrame.pageNo = msg.arg1;
                        mListener.sendFrame(bmsFrame);//上传到服务器
                        EventBus.getDefault().post(bmsFrame);
                    }
                    Log.d(TAG, "isCheck" + (bmsFrame != null && mExchangeBean != null && bmsFrame.pageNo == mExchangeBean.getEmptyBoxNumber()));
                    if (status == 3 && bmsFrame != null && mExchangeBean != null && bmsFrame.pageNo == mExchangeBean.getEmptyBoxNumber()) {
                        if (CheckUtil.checkBattery(bmsFrame)) {
                            status = 4;//电池放入完成
                            Log.d(TAG, "电池放入完成");

                            sendCloseMessage();
                            mPresenter.putInOld();

                            sendShowMessage("电池检测成功");
                        } else {
                            status = 2;
                            sendShowMessage("电池异常");
                            mCabinetManager.openDoor(0, mExchangeBean.getEmptyBoxNumber());
                        }
                    }
                    break;
                case 2:
                    int deviceNo = msg.arg1;
                    searchPackage(deviceNo);
                    break;
                case 3:
                    mListener.showDialog((String) msg.obj);
                    break;
                case 4:
                    mListener.closeDialog();
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
                if (boxStatuses!=null&&boxStatuses.size()>0){
                    for (int i=0;i<boxStatuses.size();i++){
                        if (!boxStatuses.get(i).getEmpty()){
                            searchPackage(i);
                        }
                    }
                }
            }
        }
    };

    private void searchPackage(int deviceNo) {
        try {
            this.deviceNo = deviceNo;
            byte[] frame;
            frame = EncodeFrame.selectFirst(deviceNo);
            mListener.sendFrame(frame);
            Thread.sleep(1000);
            mListener.sendFrame(frame);
            for (int i = 0; i < 9; i++) {
                Thread.sleep(600);
                frame = EncodeFrame.selectByPageNo(deviceNo, String.valueOf(i));
                mListener.sendFrame(frame);
            }
            Thread.sleep(600);
            frame = EncodeFrame.selectEnd(deviceNo);
            mListener.sendFrame(frame);
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendShowMessage(String content) {
        Message message = Message.obtain();
        message.obj = content;
        message.what = 3;
        mHandler.sendMessage(message);
    }

    private void sendCloseMessage() {
        Message message = Message.obtain();
        message.what = 4;
        mHandler.sendMessage(message);
    }
}
