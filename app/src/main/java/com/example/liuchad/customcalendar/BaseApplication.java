package com.example.liuchad.customcalendar;

import android.app.Application;

/**
 * Created by liuchad on 16/3/21.
 */
public class BaseApplication extends Application {

    private static BaseApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static BaseApplication getInstance() {
        return sInstance;
    }

    @Override
    public String getPackageName() {
        return super.getPackageName();
    }
}