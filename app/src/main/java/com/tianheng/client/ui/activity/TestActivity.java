package com.tianheng.client.ui.activity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.rey.material.widget.Button;
import com.tianheng.client.R;
import com.tianheng.client.base.BaseActivity;
import com.tianheng.client.broad.CabinetManager;
import com.tianheng.client.model.bean.BatteryBean;
import com.tianheng.client.model.event.DoorsStatusEvent;
import com.tianheng.client.model.event.OpenDoorNumEvent;
import com.tianheng.client.model.frame.BatteryFrame;
import com.tianheng.client.presenter.TestPresenter;
import com.tianheng.client.presenter.contract.TestContract;
import com.tianheng.client.ui.adapter.BatteryAdapter;
import com.tianheng.client.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by huyg on 2018/1/29.
 */

public class TestActivity extends BaseActivity<TestPresenter> implements TestContract.View {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.battery)
    RecyclerView mRecycler;
    @BindView(R.id.box_status)
    Button mBoxStatus;
    @BindView(R.id.battery_status)
    Button mBatteryStatus;


    @OnClick({R.id.box_status, R.id.battery_status})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.box_status:
                mCabinetManager.getDoorsStatus(0, 8);
                break;
            case R.id.battery_status:

                break;
        }
    }


    private BatteryAdapter mAdapter;
    private GridLayoutManager mLayoutManager;
    private CabinetManager mCabinetManager;

    @Override
    protected void initInject() {
        getActivityComponent().inject(this);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_test;
    }

    @Override
    protected void init() {
        initCabinetManager();
        initView();
    }

    private void initView() {
        initToolbar();
        initRecycler();
    }

    private void initCabinetManager() {
        mCabinetManager = new CabinetManager(this);
    }


    private void initRecycler() {
        mAdapter = new BatteryAdapter();
        mAdapter.setTesting(true);
        mLayoutManager = new GridLayoutManager(this, 2);
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setAdapter(mAdapter);
    }

    private void initToolbar() {
        mToolbar.setTitle("维护");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Subscribe
    public void onEvent(DoorsStatusEvent event) {
        List<BatteryBean> batteryBeans = new ArrayList<>();
        List<Integer> opends = event.opendArray;
        for (int i = 0; i < opends.size(); i++) {
            BatteryBean batteryBean = new BatteryBean();
            batteryBean.setNum(i);
            batteryBean.setStatus(opends.get(i));
            batteryBeans.add(batteryBean);
        }
        mAdapter.setList(batteryBeans);
    }

    @Subscribe
    public void onEvent(OpenDoorNumEvent event) {
        int position = event.position;
        mCabinetManager.openDoor(0, position);
    }

    @Subscribe
    public void onEvent(BatteryFrame batteryFrame) {
        if (batteryFrame != null) {
        }
    }


    @Override
    public void showContent(String message) {
        ToastUtil.showShort(this, message);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }
}
