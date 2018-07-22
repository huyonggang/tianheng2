package com.tianheng.client.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.KeyboardView;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dd.processbutton.FlatButton;
import com.tianheng.client.App;
import com.tianheng.client.R;
import com.tianheng.client.base.BaseFragment;
import com.tianheng.client.broad.CabinetManager;
import com.tianheng.client.global.Const;
import com.tianheng.client.model.bean.BoxStatus;
import com.tianheng.client.model.bean.ExchangeBean;
import com.tianheng.client.model.bean.MemberBean;
import com.tianheng.client.model.bean.OrderBean;
import com.tianheng.client.model.bean.SubscribeBean;
import com.tianheng.client.model.bean.UserBean;
import com.tianheng.client.model.bean.UserMemberBean;
import com.tianheng.client.model.event.DoorStatusEvent;
import com.tianheng.client.model.event.MemberEvent;
import com.tianheng.client.model.event.OpenDoorEvent;
import com.tianheng.client.model.frame.BMSFrame;
import com.tianheng.client.model.frame.BatteryFrame;
import com.tianheng.client.model.frame.ForwardFrame;
import com.tianheng.client.presenter.OperatePresenter;
import com.tianheng.client.presenter.contract.OperateContract;
import com.tianheng.client.ui.activity.UserActivity;
import com.tianheng.client.util.CheckUtil;
import com.tianheng.client.util.DecodeFrame;
import com.tianheng.client.util.EncodeFrame;
import com.tianheng.client.util.ToastUtil;
import com.tianheng.client.wedget.NumberKeyboardView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static org.greenrobot.eventbus.ThreadMode.BACKGROUND;

/**
 * Created by huyg on 2017/12/26.
 * status
 * 0：初始状态
 * 1：打开空箱子
 */

public class OperateFragment extends BaseFragment<OperatePresenter> implements OperateContract.View {

    @BindView(R.id.user_qr_code)
    ImageView mQRCode;
    @BindView(R.id.exchange)
    Button mExchange;
    @BindView(R.id.input_code)
    EditText mInputCode;
    @BindView(R.id.keyboard)
    NumberKeyboardView mKeyboard;

    @OnClick({R.id.user_get_code, R.id.exchange})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_get_code:
                getCode();
                break;
            case R.id.exchange:
                String code = mInputCode.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    ToastUtil.showShort(mContext, "请输入取件码");
                    return;
                }
                mPresenter.subscribeCode(code);
                break;

        }
    }


    private CabinetManager mCabinetManager;
    private int status = -1;//初始状态 status :1:用户开始操作；2：柜门打开:3:插入电池。4：电池检测成功.5:结束旧订单
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
    private int deviceNo;
    private List<BoxStatus> boxStatuses = new ArrayList<>();


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
        return R.layout.fragment_operate;
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
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (timer != null) {
            timer.cancel();
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
        String imei = App.getInstance().getImei();
        mPresenter.getQRCode(imei, 200, 200, "png");
        boxStatuses.clear();
        for (int i = 0; i < 8; i++) {
            BoxStatus boxStatus = new BoxStatus();
            boxStatus.setEmpty(true);
            boxStatus.setBoxNum(i);
            boxStatuses.add(boxStatus);
        }
    }

    public void schedule() {
        //定时上传数据
        timer.schedule(task, 60 * 1000, 15 * 60 * 1000);
    }

    private void initView() {
        initEditText();
        initKeyboard();
    }


    @Override
    public void showContent(String message) {
//        ToastUtil.showShort(getActivity(), message);
    }


    @Subscribe
    public void onEvent(MemberEvent event) {
        UserMemberBean userMemberBean = event.memberBean;
        App.getInstance().setTicket(userMemberBean.getTicket());
        mPresenter.exchange();
    }

    /**
     * 门的状态
     *
     * @param event
     */
    @Subscribe
    public void onEvent(DoorStatusEvent event) {
        boolean isOpen = event.isOpen;
        Log.d(TAG, "isOpen" + isOpen);
        if (!isOpen) {
            if (status == 4 && event.iLockId == mExchangeBean.getEmptyBoxNumber()) {
                if (disposable != null) {
                    disposable.dispose();
                }
                mPresenter.closeOld(mExchangeBean.getLeaseBatteryNumber(), mExchangeBean.getEmptyBoxNumber());
            } else if (status == 2 && event.iLockId == mExchangeBean.getEmptyBoxNumber()) {
                sendCloseMessage();
                status = -1;
                if (disposable != null) {
                    disposable.dispose();
                }
            } else if (status == 6 && event.iLockId == mExchangeBean.getExchangeBoxNumber()) {
                sendShowMessage("请取出电池");
                mCabinetManager.openDoor(0, mExchangeBean.getExchangeBoxNumber());
            } else if (status == 7 && event.iLockId == mExchangeBean.getExchangeBoxNumber()) {
                mPresenter.closeNew(mExchangeBean.getExchangeBoxNumber(), mExchangeBean.getExchangeBatteryNumber());
                if (disposable != null) {
                    disposable.dispose();
                }
            }
        } else {

        }


    }

    /**
     * 开门回传状态
     *
     * @param event
     */
    @Subscribe
    public void onEvent(OpenDoorEvent event) {
        boolean isOpen = event.isOpen;
        if (isOpen) {
            Log.d(TAG, "status =" + status);
            if (status == 1 && event.iLockId == mExchangeBean.getEmptyBoxNumber()) {
                sendShowMessage("请放入电池");
                status = 2;
            } else if (status == 5 && event.iLockId == mExchangeBean.getExchangeBoxNumber()) {
                status = 6;
                sendShowMessage("请取出电池,并关闭箱门");
                if (disposable == null || disposable.isDisposed()) {
                    disposable = Observable.interval(0, 5, TimeUnit.SECONDS)
                            .subscribeOn(Schedulers.io())
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
    }

    /**
     * bms状态
     *
     * @param batteryFrame
     */
    @Subscribe
    public void onEvent(BatteryFrame batteryFrame) {
        if (batteryFrame != null) {
            switch (batteryFrame.bms) {
                //连接状态
                case "00":
                    if (Integer.parseInt(batteryFrame.device) == mExchangeBean.getEmptyBoxNumber() && status == 2) {
                        status = 3;//电池插入成功
                        searchPackage(mExchangeBean.getEmptyBoxNumber());
                    }
                    BoxStatus boxStatus = boxStatuses.get(Integer.parseInt(batteryFrame.device));
                    if (boxStatus.getEmpty()) {
                        boxStatus.setEmpty(false);
                        boxStatuses.set(Integer.parseInt(batteryFrame.device), boxStatus);
                    }

                    break;
                //断开状态
                case "01":
                    if ((status == 4 || status == 3) && Integer.parseInt(batteryFrame.device) == mExchangeBean.getEmptyBoxNumber()) {//  //检测成功或正在检测后拔出
                        status = 2;
                        sendShowMessage("请插入电池");
                    } else if (status == 6 && Integer.parseInt(batteryFrame.device) == mExchangeBean.getExchangeBoxNumber()) {
                        status = 7;
                    }
                    BoxStatus boxStatus2 = boxStatuses.get(Integer.parseInt(batteryFrame.device));
                    if (!boxStatus2.getEmpty()) {
                        boxStatus2.setEmpty(true);
                        boxStatuses.set(Integer.parseInt(batteryFrame.device), boxStatus2);
                    }
                    break;
            }
        }
    }


    /**
     * 电池状态
     *
     * @param forwardFrame
     */
    @Subscribe(threadMode = BACKGROUND)
    public void onEvent(ForwardFrame forwardFrame) {
        if (Integer.parseInt(forwardFrame.device) == deviceNo) {
            Log.d(TAG, "forwardFrame.data" + forwardFrame.data);
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
        this.mExchangeBean = exchangeBean;
        sendShowMessage("正在打开箱门，请稍后...");
        status = 1;
        mCabinetManager.openDoor(0, mExchangeBean.getEmptyBoxNumber());
    }


    @Override
    public void closeOldSuccess(OrderBean orderBean) {
        status = 5;
        sendShowMessage("打开柜门中...");
        mCabinetManager.openDoor(0, mExchangeBean.getExchangeBoxNumber());
    }


    @Override
    public void closeNewSuccess() {
        status = -1;//完成操作
        ToastUtil.show(getActivity(), "完成交易", Toast.LENGTH_SHORT);
        sendCloseMessage();
    }


    @Override
    public void closeDialog() {
        sendCloseMessage();
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
                    }

                    if (status == 3 && bmsFrame != null && mExchangeBean != null && bmsFrame.pageNo == mExchangeBean.getEmptyBoxNumber()) {
                        if (CheckUtil.checkBattery(bmsFrame)) {
                            Log.d(TAG, "电池放入完成");
                            sendShowMessage("电池检测成功");
                            status = 4;
                            if (disposable == null || disposable.isDisposed()) {
                                disposable = Observable.interval(0, 5, TimeUnit.SECONDS)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Consumer<Long>() {
                                            @Override
                                            public void accept(Long aLong) throws Exception {
                                                mCabinetManager.getDoorStatus(0, mExchangeBean.getEmptyBoxNumber());
                                            }
                                        });
                            }

                        } else {
                            status = 2;
                            sendShowMessage("电池异常,请联系当地网点");
                        }
                    } else if (bmsFrame == null && status == 3) {
                        searchPackage(OperateFragment.this.deviceNo);
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

        void sendEmptyBox(int boxNum);
    }


    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if (boxStatuses != null && boxStatuses.size() > 0) {
                for (int i = 0; i < boxStatuses.size(); i++) {
                    //检测电池
                    if (!boxStatuses.get(i).getEmpty() && status == -1) {
                        searchPackage(i);
                    }
                    //上传空柜子
                    if (boxStatuses.get(i).getEmpty()) {
                        mListener.sendEmptyBox(i);
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
            Thread.sleep(600);
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


    @Override
    public void showQRImg(String url) {
        Glide.with(this).load(url).into(mQRCode);
    }

    @Override
    public void loginSuccess(UserBean userBean) {
        String ticket = userBean.getTicket();
        if (!TextUtils.isEmpty(ticket)) {
            App.getInstance().setTicket(ticket);
            mPresenter.exchange();
        }
    }

    @Override
    public void subscribeSuccess(SubscribeBean subscribeBean) {
        App.getInstance().setTicket(subscribeBean.getTicket());
        this.mExchangeBean = subscribeBean.getExchangeModel();
        sendShowMessage("正在打开箱门，请稍后...");
        status = 1;
        mCabinetManager.openDoor(0, mExchangeBean.getEmptyBoxNumber());
    }

    @Override
    public void logoutSuccess() {

    }

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


    private void initEditText() {
        if (android.os.Build.VERSION.SDK_INT > 10) {//4.0以上 danielinbiti
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus",
                        boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(mInputCode, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mInputCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //隐藏系统软键盘
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mInputCode.getWindowToken(), 0);
                }
            }
        });

    }

    private void initKeyboard() {
        mKeyboard.setOnNumberClickListener(new NumberKeyboardView.OnNumberClickListener() {
            @Override
            public void onNumberReturn(String number) {
                setTextContent(mInputCode.getText().toString() + number);
            }

            @Override
            public void onNumberDelete() {
                int length = mInputCode.getText().length();
                if (length <= 1) {
                    setTextContent("");
                } else {
                    setTextContent(mInputCode.getText().toString().substring(0, length - 1));
                }
            }

            @Override
            public void onNumberDeleteAll() {
                setTextContent("");
            }
        });
    }

    private void setTextContent(String content) {
        mInputCode.setText(content);
        mInputCode.setSelection(content.length());
    }

}
