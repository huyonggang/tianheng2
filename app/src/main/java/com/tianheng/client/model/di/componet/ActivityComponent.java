package com.tianheng.client.model.di.componet;

import android.app.Activity;

import com.tianheng.client.model.di.ActivityScope;
import com.tianheng.client.model.di.module.ActivityModule;
import com.tianheng.client.ui.activity.HomeActivity;
import com.tianheng.client.ui.activity.LogActivity;
import com.tianheng.client.ui.activity.SettingActivity;
import com.tianheng.client.ui.activity.UserActivity;

import dagger.Component;

/**
 * Created by codeest on 2017/12/25.
 */

@ActivityScope
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    Activity getActivity();

    void inject(HomeActivity homeActivity);

    void inject(UserActivity userActivity);

    void inject(SettingActivity settingActivity);

    void inject(LogActivity logActivity);


}
