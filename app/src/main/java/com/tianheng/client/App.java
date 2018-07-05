package com.tianheng.client;

import android.support.multidex.MultiDexApplication;

import com.facebook.stetho.Stetho;
import com.iflytek.cloud.SpeechUtility;
import com.tencent.bugly.crashreport.CrashReport;
import com.tianheng.client.model.di.componet.AppComponent;
import com.tianheng.client.model.di.componet.DaggerAppComponent;
import com.tianheng.client.model.di.module.AppModule;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by huyg on 2017/12/20.
 */

public class App extends MultiDexApplication{

    private static App instance;
    private String param = "appid=5a62ff77";

    public static synchronized App getInstance() {
        return instance;
    }

    private String ticket;



    private String imei;


    private boolean isRoot = false;
    /**
     * Module与Activity连接器
     */
    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initDaggerComponent();
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
        //jpush
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
         JPushInterface.init(this);     		// 初始化 JPush
        SpeechUtility.createUtility(this,param);
        initCrash();
    }

    private void initCrash() {
        CrashReport.initCrashReport(getApplicationContext(), "f1760f78fc", true);
    }

    /**
     * 初始化Dagger所使用的连接器
     */
    private void initDaggerComponent() {
        mAppComponent = DaggerAppComponent
                .builder()
                .appModule(new AppModule())
                .build();
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * 获取连接器
     *
     * @return
     */
    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }
}
