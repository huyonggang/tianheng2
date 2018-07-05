package com.tianheng.client.model.di.module;

import android.content.Context;

import com.tianheng.client.manage.TTSManage;
import com.tianheng.client.model.http.ApiFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by codeest on 2017/12/25.
 */

@Module
public class AppModule {

    @Provides
    @Singleton
    ApiFactory provideRetrofitHelper() {
        return new ApiFactory();
    }

    @Provides
    @Singleton
    TTSManage provideTTSHelper(){
        return new TTSManage();
    }


}
