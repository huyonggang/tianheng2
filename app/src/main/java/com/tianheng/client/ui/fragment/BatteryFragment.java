package com.tianheng.client.ui.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tianheng.client.R;
import com.tianheng.client.base.BaseFragment;
import com.tianheng.client.broad.CabinetManager;
import com.tianheng.client.model.bean.BatteryBean;
import com.tianheng.client.model.event.DoorsStatusEvent;
import com.tianheng.client.model.frame.BMSFrame;
import com.tianheng.client.model.frame.BatteryFrame;
import com.tianheng.client.presenter.BatteryPresenter;
import com.tianheng.client.presenter.contract.BatteryContract;
import com.tianheng.client.ui.adapter.BatteryAdapter;
import com.tianheng.client.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
        mCabinetManager.getDoorsStatus(0, 7);
        for (int i = 0; i <8; i++) {
            BatteryBean batteryBean = new BatteryBean();
            batteryBean.setNum(i);
//            batteryBean.setStatus(opends.get(i));
            batteryBeans.add(batteryBean);
            mAdapter.setList(batteryBeans);
        }
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
//        batteryBeans.clear();
//        List<Integer> opends = event.opendArray;
    }

    @Subscribe
    public void onEvent(BatteryFrame batteryFrame) {
        if (batteryFrame != null) {
            BatteryBean batteryBean = new BatteryBean();
            batteryBean.setNum(5);
            batteryBean.setElectricity(Integer.parseInt(batteryFrame.electric,16));
            mAdapter.setBatteryBean(batteryBean);
        }
    }


    @Subscribe
    public void onEvent(BMSFrame bmsFrame){
        if (bmsFrame != null) {
            BatteryBean batteryBean = new BatteryBean();
            batteryBean.setNum(5);
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
        ToastUtil.showShort(getActivity(),message);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }
}
