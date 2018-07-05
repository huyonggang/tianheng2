package com.tianheng.client.model.di.componet;

import android.app.Activity;


import com.tianheng.client.model.di.FragmentScope;
import com.tianheng.client.model.di.module.FragmentModule;
import com.tianheng.client.ui.fragment.BatteryFragment;
import com.tianheng.client.ui.fragment.OperateFragment;

import dagger.Component;

/**
 * Created by codeest on 2017/12/25.
 */

@FragmentScope
@Component(dependencies = AppComponent.class, modules = FragmentModule.class)
public interface FragmentComponent {

    Activity getActivity();
    void inject(BatteryFragment batteryFragment);
    void inject(OperateFragment OperateFragment);
}




