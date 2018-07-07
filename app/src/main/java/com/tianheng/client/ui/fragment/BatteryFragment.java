package com.tianheng.client.ui.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tianheng.client.R;
import com.tianheng.client.base.BaseFragment;
import com.tianheng.client.broad.CabinetManager;
import com.tianheng.client.model.bean.BatteryBean;
import com.tianheng.client.model.event.DoorsStatusEvent;
import com.tianheng.client.model.event.GoodsStatusEvent;
import com.tianheng.client.model.frame.BMSFrame;
import com.tianheng.client.model.frame.BatteryFrame;
import com.tianheng.client.presenter.BatteryPresenter;
import com.tianheng.client.presenter.contract.BatteryContract;
import com.tianheng.client.ui.adapter.BatteryAdapter;
import com.tianheng.client.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by huyg on 2017/12/26.
 */

public class BatteryFragment extends BaseFragment<BatteryPresenter> implements BatteryContract.View {


    @BindView(R.id.battery)
    RecyclerView mRecycler;

    private BatteryAdapter mAdapter;
    private GridLayoutManager mLayoutManager;
    private CabinetManager mCabinetManager;
    private List<BatteryBean> batteryBeans = new ArrayList<>();

    @Override
    protected void initInject() {
        getFragmentComponent().inject(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_battery;
    }

    @Override
    protected void init() {
        initView();
        initData();
    }

    private void initData() {
        mCabinetManager = new CabinetManager(getActivity());
        mCabinetManager.getDoorsStatus(0, 8);
        for (int i = 0; i < 8; i++) {
            BatteryBean batteryBean = new BatteryBean();
            batteryBean.setNum(i);
            batteryBeans.add(batteryBean);
            mAdapter.setList(batteryBeans);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mAdapter.setList(batteryBeans);
    }

    private void initView() {
        initRecycler();
    }

    private void initRecycler() {
        mAdapter = new BatteryAdapter();
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setAdapter(mAdapter);
    }

    public static BatteryFragment newInstance() {
        BatteryFragment batteryFragment = new BatteryFragment();
        return batteryFragment;
    }


    @Subscribe
    public void onEvent(DoorsStatusEvent event) {
        List<Integer> opendArray = event.opendArray;
        if (opendArray != null && opendArray.size() > 0) {
            for (int i = 0; i < opendArray.size(); i++) {
                if (opendArray.get(i) == 1) {
                    ToastUtil.showShort(mContext, "请关闭" + (i + 1) + "号箱门");
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BatteryFrame batteryFrame){
        if (Integer.parseInt(batteryFrame.bms)==1){
            batteryBeans.get(Integer.parseInt(batteryFrame.device)).setStatus(2);
        }
        mAdapter.setList(batteryBeans);
    }

    @Subscribe
    public void onEvent(GoodsStatusEvent goodsStatusEvent) {
        List<Integer> goodsList = goodsStatusEvent.goodsList;
        if (goodsList != null && goodsList.size() > 0) {
            for (int i = 0; i < goodsList.size(); i++) {
                if (goodsList.get(i) == 0) {
                    batteryBeans.get(i).setStatus(2);
                }
            }
        }
        mAdapter.setList(batteryBeans);
    }


    @Subscribe
    public void onEvent(BMSFrame bmsFrame) {
        if (bmsFrame != null) {
            BatteryBean batteryBean = new BatteryBean();
            String status = bmsFrame.status;
            if ("0000".equals(status)){
                batteryBean.setStatus(0);
            }else if ("0001".equals(status)){
                batteryBean.setStatus(1);
            }
            batteryBean.setNum(bmsFrame.pageNo);
            batteryBean.setElectricity(getPower(bmsFrame));
            mAdapter.setBatteryBean(batteryBean);
        }
    }

    public int getPower(BMSFrame bmsFrame) {
        float capacity = Integer.parseInt(bmsFrame.capacity, 16);
        float capacitySum = Integer.parseInt(bmsFrame.capacitySum, 16);
        int power = (int) (capacity / capacitySum * 100);
        return power;
    }

    @Override
    public void showContent(String message) {
        ToastUtil.showShort(getActivity(), message);
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
    }
}
