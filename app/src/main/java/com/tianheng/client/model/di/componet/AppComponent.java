package com.tianheng.client.model.di.componet;


import android.content.Context;

import com.tianheng.client.manage.TTSManage;
import com.tianheng.client.model.di.module.AppModule;
import com.tianheng.client.model.http.ApiFactory;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by codeest on 2017/12/25.
 */

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    ApiFactory ApiFactory();  //提供http的帮助类
    TTSManage TTSManage();
}
