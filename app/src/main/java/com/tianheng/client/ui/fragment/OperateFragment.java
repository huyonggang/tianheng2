package com.tianheng.client.ui.fragment;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tianheng.client.App;
import com.tianheng.client.R;
import com.tianheng.client.base.BaseFragment;
import com.tianheng.client.broad.CabinetManager;
import com.tianheng.client.global.Const;
import com.tianheng.client.model.bean.BoxStatus;
import com.tianheng.client.model.bean.ExchangeBean;
import com.tianheng.client.model.bean.SubscribeBean;
import com.tianheng.client.model.bean.UserBean;
import com.tianheng.client.model.event.DoorStatusEvent;
import com.tianheng.client.model.event.GoodStatusEvent;
import com.tianheng.client.model.event.JPushEvent;
import com.tianheng.client.model.event.OpenDoorEvent;
import com.tianheng.client.model.frame.BMSFrame;
import com.tianheng.client.model.frame.BatteryFrame;
import com.tianheng.client.model.frame.ForwardFrame;
import com.tianheng.client.presenter.OperatePresenter;
import com.tianheng.client.presenter.contract.OperateContract;
import com.tianheng.client.util.CheckUtil;
import com.tianheng.client.util.DecodeFrame;
import com.tianheng.client.util.EncodeFrame;
import com.tianheng.client.util.ToastUtil;
import com.tianheng.client.wedget.NumberKeyboardView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Method;
import java.text.MessageFormat;
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
    @BindView(R.id.keyboard_layout)
    FrameLayout mKeyboardLayout;
    @BindView(R.id.input_title)
    TextView mIputTitle;
    @BindView(R.id.subscribe_layout)
    LinearLayout mSubscribeLayout;
    @BindView(R.id.imei)
    TextView mImei;


    @OnClick({R.id.exchange, R.id.imei})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.exchange:
                String code = mInputCode.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    ToastUtil.showShort(mContext, "请输入取件码");
                    return;
                }
                mPresenter.subscribeCode(code);
                break;
            case R.id.imei:
                num--;
                if (num == 0) {
                    if (mLogFragment == null) {
                        mLogFragment = LogFragment.newInstance();
                    }
                    mLogFragment.show(getChildFragmentManager(), "show");
                    num = 5;
                }
                break;

        }
    }


    private CabinetManager mCabinetManager;
    private int status = -1;//初始状态 status :1:用户开始操作；2：柜门打开:3:插入电池。4：电池检测成功.5:结束旧订单
    private ExchangeBean mExchangeBean;
    private static final String TAG = "OperateFragment";

    private OperateListener mListener;
    private StringBuilder frameBuilder = new StringBuilder(); //查询帧
    private int residue;//最后一包有几位是需要的数据
    private int sum;//总包数
    private MyHandler mHandler = new MyHandler();
    private Timer timer = new Timer();
    private String lastPageNo = "";
    private Disposable disposable;
    private int deviceNo;
    private List<BoxStatus> boxStatuses = new ArrayList<>();
    private int num = 5;
    private LogFragment mLogFragment;

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
        mImei.setText(MessageFormat.format("电池柜编号:{0}", imei));
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
        timer.schedule(task, 10 * 1000, 5 * 60 * 1000);
    }

    private void initView() {
        mKeyboardLayout.setVisibility(View.GONE);
        initEditText();
        initKeyboard();
    }

    @Override
    public void showContent(String message) {
        ToastUtil.showShort(getActivity(), message);
    }


    @Subscribe
    public void onEvent(JPushEvent event) {
        App.getInstance().setTicket(event.getTicket());
        this.mExchangeBean = event.getExchangeBean();
        sendCloseMessage();
        sendShowMessage("正在打开箱门，请稍后...");
        Log.d(TAG, "正在打开箱门    " + status);
        Log.d(TAG, "正在打开箱门    " + mExchangeBean.toString());
        mQRCode.setVisibility(View.GONE);
        if (mExchangeBean.getEmptyBoxNumber() == -1) {
            status = 5;
            mCabinetManager.openDoor(0, mExchangeBean.getExchangeBoxNumber());
        } else {
            status = 1;
            mCabinetManager.openDoor(0, mExchangeBean.getEmptyBoxNumber());
        }

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
            if (status == 3 && event.iLockId == mExchangeBean.getEmptyBoxNumber()) {
                sendCloseMessage();
                sendShowMessage("箱门已关闭，正在检测电池");
                if (disposable != null) {
                    disposable.dispose();
                }
            } else if (status == 4 && event.iLockId == mExchangeBean.getEmptyBoxNumber()) {
                if (disposable != null) {
                    disposable.dispose();
                }
                closeDialog();
                mPresenter.closeOld(mExchangeBean.getLeaseBatteryNumber(), mExchangeBean.getEmptyBoxNumber());
            } else if (status == 2 && event.iLockId == mExchangeBean.getEmptyBoxNumber()) {
                if (disposable != null) {
                    disposable.dispose();
                }
                Log.d(TAG, "箱门关闭    " + status);
                Log.d(TAG, "getGoodsStatus    " + status);
                if (disposable == null || disposable.isDisposed()) {
                    disposable = Observable.timer(3, TimeUnit.SECONDS)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Long>() {
                                @Override
                                public void accept(Long aLong) throws Exception {
                                    Log.d(TAG, "getDoorStatus    " + status);
                                    mCabinetManager.getGoodStatus(0, mExchangeBean.getEmptyBoxNumber());
                                }
                            });
                }


            } else if (status == 6 && event.iLockId == mExchangeBean.getExchangeBoxNumber()) {
                if (disposable != null) {
                    disposable.dispose();
                }
                Log.d(TAG, "getGoodsStatus    " + status);
                if (disposable == null || disposable.isDisposed()) {
                    disposable = Observable.timer(2, TimeUnit.SECONDS)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Long>() {
                                @Override
                                public void accept(Long aLong) throws Exception {
                                    Log.d(TAG, "getDoorStatus    " + status);
                                    mCabinetManager.getGoodStatus(0, mExchangeBean.getExchangeBoxNumber());
                                }
                            });
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
                sendCloseMessage();
                sendShowMessage("请放入电池");
                Log.d(TAG, "请放入电池    " + status);
                status = 2;
                Log.d(TAG, "getDoorStatus    " + status);
                if (disposable == null || disposable.isDisposed()) {
                    disposable = Observable.interval(4, 4, TimeUnit.SECONDS)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Long>() {
                                @Override
                                public void accept(Long aLong) throws Exception {
                                    Log.d(TAG, "getDoorStatus    " + status);
                                    mCabinetManager.getDoorStatus(0, mExchangeBean.getEmptyBoxNumber());
                                }
                            });
                }
            } else if (status == 5 && event.iLockId == mExchangeBean.getExchangeBoxNumber()) {
                status = 6;
                Log.d(TAG, "打开新柜门  " + status);
                sendCloseMessage();
                sendShowMessage("请取出电池,并关闭箱门");
                if (disposable == null || disposable.isDisposed()) {
                    disposable = Observable.interval(4, 4, TimeUnit.SECONDS)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Long>() {
                                @Override
                                public void accept(Long aLong) throws Exception {
                                    Log.d(TAG, "getDoorStatus    " + status);
                                    mCabinetManager.getDoorStatus(0, mExchangeBean.getExchangeBoxNumber());
                                }
                            });
                }
            }
        }
    }

    /**
     * 物检状态返回
     *
     * @param event
     */
    @Subscribe
    public void onEvent(GoodStatusEvent event) {
        if (disposable != null) {
            disposable.dispose();
        }
        int lockId = event.iLockId;
        boolean isGoods = event.isGoods;
        Log.d(TAG, "lockId    " + lockId + "     isGoods" + isGoods);
        if (isGoods) {//有物体
            if (status == 2 && lockId == mExchangeBean.getEmptyBoxNumber()) {
                sendCloseMessage();
                sendShowMessage("请重新插入电池");
                status = 1;
                Log.d(TAG, "重新插入电池    " + status);
                mCabinetManager.openDoor(0, mExchangeBean.getEmptyBoxNumber());
            } else if (status == 6 && lockId == mExchangeBean.getExchangeBoxNumber()) {
                sendCloseMessage();
                sendShowMessage("请取出电池");
                status = 5;
                mCabinetManager.openDoor(0, mExchangeBean.getExchangeBoxNumber());
            }
        } else {
            if (status == 2 && lockId == mExchangeBean.getEmptyBoxNumber()) {
                sendCloseMessage();
                status = -1;
                mQRCode.setVisibility(View.VISIBLE);
            } else if (status == 6 && event.iLockId == mExchangeBean.getExchangeBoxNumber()) {
                sendCloseMessage();
                Log.d(TAG, "关闭箱门,无物体    " + status);
                mPresenter.closeNew(mExchangeBean.getExchangeBoxNumber(), mExchangeBean.getExchangeBatteryNumber());
            }
        }
    }

    /**
     * bms状态
     *
     * @param batteryFrame
     */
    @Subscribe(threadMode = BACKGROUND)
    public void onEvent(BatteryFrame batteryFrame) {
        if (batteryFrame != null) {
            switch (batteryFrame.bms) {
                //连接状态
                case "00":
                    if (status == 2 && Integer.parseInt(batteryFrame.device) == mExchangeBean.getEmptyBoxNumber()) {
                        status = 3;//电池插入成功
                        Log.d(TAG, "放入电池    " + status);
                        sendCloseMessage();
                        sendShowMessage("电池放入成功,请关闭箱门");
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
                        sendCloseMessage();
                        sendShowMessage("请插入电池");
                        Log.d(TAG, "电池拔出    " + status);
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
    @Subscribe()
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
            Log.d("data", "packNo" + forwardFrame.packNo);
            Log.d("data", "lastPageNo" + lastPageNo);
            if (!forwardFrame.packNo.equals(lastPageNo)) {
                if (forwardFrame.packNo.equals(packNo)) {
                    Log.d("data", "append" + forwardFrame.packNo);
                    Log.d("data", "residue" + residue);
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
        sendCloseMessage();
        sendShowMessage("正在打开箱门，请稍后");
        status = 1;
        mCabinetManager.openDoor(0, mExchangeBean.getEmptyBoxNumber());
    }


    @Override
    public void closeOldSuccess() {
        showContent("订单结算成功");
        closeDialog();
        status = 5;
        sendCloseMessage();
        sendShowMessage("打开柜门中...");
        mCabinetManager.openDoor(0, mExchangeBean.getExchangeBoxNumber());
    }


    @Override
    public void closeNewSuccess() {
        status = -1;//完成操作
        mQRCode.setVisibility(View.VISIBLE);
        ToastUtil.show(getActivity(), "完成交易", Toast.LENGTH_SHORT);
        sendCloseMessage();
        mPresenter.logout(App.getInstance().getTicket());
        hindKeyboard();
        mInputCode.setText("");
    }


    @Override
    public void closeDialog() {
        sendCloseMessage();
    }

    @Override
    public void showDialog(String message) {
        sendCloseMessage();
        sendShowMessage(message);
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
                            sendCloseMessage();
                            sendShowMessage("电池检测成功");
                            Log.d(TAG, "电池检测成功    " + status);
                            status = 4;
                            if (disposable == null || disposable.isDisposed()) {
                                disposable = Observable.interval(0, 4, TimeUnit.SECONDS)
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
                            sendCloseMessage();
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
        sendCloseMessage();
        sendShowMessage("正在打开箱门，请稍后...");
        status = 1;
        mCabinetManager.openDoor(0, mExchangeBean.getEmptyBoxNumber());
    }

    @Override
    public void logoutSuccess() {

    }


    private void initEditText() {

        if (android.os.Build.VERSION.SDK_INT > 10) {//4.0以上 danielinbiti
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus",
                        boolean.class);
                setShowSoftInputOnFocus.setAccessible(false);
                setShowSoftInputOnFocus.invoke(mInputCode, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        hindKeyboard();
        mInputCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //隐藏系统软键盘
                    mKeyboardLayout.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void hindKeyboard() {
        mInputCode.clearFocus();
        mInputCode.clearFocus();
        mIputTitle.requestFocus();
        mIputTitle.requestFocusFromTouch();
        mKeyboardLayout.setVisibility(View.GONE);
    }

    private void initKeyboard() {
        mKeyboard.setOnNumberClickListener(new NumberKeyboardView.OnNumberClickListener() {
            @Override
            public void onNumberReturn(String number) {
                setTextContent(mInputCode.getText().toString() + number);
            }

            @Override
            public void onNumberDelete() {

                int inputCode = mInputCode.getText().length();
                if (inputCode <= 1) {
                    setTextContent("");
                } else {
                    setTextContent(mInputCode.getText().toString().substring(0, inputCode - 1));
                }
            }

            @Override
            public void onHintKeyboard() {
                hindKeyboard();

            }
        });
    }

    private void setTextContent(String content) {
        mInputCode.setText(content);
        mInputCode.setSelection(content.length());

    }
}
