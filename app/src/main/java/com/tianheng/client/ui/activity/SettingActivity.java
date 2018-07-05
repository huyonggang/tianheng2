package com.tianheng.client.ui.activity;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rey.material.widget.Button;
import com.tianheng.client.R;
import com.tianheng.client.base.BaseActivity;
import com.tianheng.client.model.frame.BatteryFrame;
import com.tianheng.client.presenter.SettingPresenter;
import com.tianheng.client.presenter.contract.SettingContract;
import com.tianheng.client.util.DataUtils;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by huyg on 2018/1/19.
 */

public class SettingActivity extends BaseActivity<SettingPresenter> implements SettingContract.View {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.login_out)
    Button mLoginOut;
    @BindView(R.id.maintain)
    LinearLayout mMaintain;
    @BindView(R.id.version)
    LinearLayout mVersion;


    @OnClick({R.id.maintain, R.id.version, R.id.login_out})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_out:
                mIntent.setClass(SettingActivity.this, HomeActivity.class);
                startActivity(mIntent);
                finish();
                break;
            case R.id.maintain:
                mIntent.setClass(SettingActivity.this, TestActivity.class);
                startActivity(mIntent);
                break;
            case R.id.version:
                break;
        }
    }

    private Intent mIntent = new Intent();

    @Override
    protected void initInject() {
        getActivityComponent().inject(this);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_setting;
    }


    @Override
    protected void init() {
        initView();
    }

    private void initView() {
        initToolbar();
    }

    private void initToolbar() {
        mToolbar.setTitle("设置");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void showContent(String message) {

    }




}
