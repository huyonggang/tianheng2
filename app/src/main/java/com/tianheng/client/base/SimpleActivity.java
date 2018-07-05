package com.tianheng.client.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by huyg on 2017/12/25.
 * 无MVP activity基类
 */

public abstract class SimpleActivity extends AppCompatActivity{

    private Unbinder mUnBinder;
    protected Activity mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        mUnBinder= ButterKnife.bind(this);
        mContext=this;
        init();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        mUnBinder.unbind();
    }

    protected abstract int getLayout();
    protected abstract void init();
}
