package com.tianheng.client.model.di.module;

import android.app.Activity;


import com.tianheng.client.model.di.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by codeest on 2017/12/25.
 */

@Module
public class ActivityModule {
    private Activity mActivity;

    public ActivityModule(Activity activity) {
        this.mActivity = activity;
    }

    @Provides
    @ActivityScope
    public Activity provideActivity() {
        return mActivity;
    }
}
