package com.lchad.customcalendar;

import android.app.Application;

/**
 * Created by liuchad on 16/3/21.
 * Github: https://github.com/lchad
 */
public class BaseApplication extends Application {

    private static BaseApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static BaseApplication getInstance() {
        return sInstance;
    }
}