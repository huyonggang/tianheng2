package com.tianheng.client.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.tianheng.client.R;
import com.tianheng.client.base.BaseActivity;
import com.tianheng.client.model.bean.BatteryInfo;
import com.tianheng.client.presenter.LogPresenter;
import com.tianheng.client.presenter.contract.LogContract;
import com.tianheng.client.ui.adapter.LogAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by huyg on 2018/10/16.
 */
public class LogActivity extends BaseActivity<LogPresenter> implements LogContract.View {


    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.log)
    TextView mLog;

    private LogAdapter mAdapter;
    private List<BatteryInfo> batteryInfos;

    @Override
    protected void initInject() {
        getActivityComponent().inject(this);
    }


    @Override
    protected int getLayout() {
        return R.layout.activity_log;
    }

    @Override
    protected void init() {
        initView();
    }

    private void initView() {
        mAdapter = new LogAdapter(R.layout.item_log, batteryInfos);
    }

    @Override
    public void showContent(String message) {

    }

}
