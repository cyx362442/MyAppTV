package com.duowei.mytvtest;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by Administrator on 2017-03-07.
 */

public class MyApplicaction extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        //it is public static, you can set this everywhere
        //JCVideoPlayer.TOOL_BAR_EXIST = false;
        //JCVideoPlayer.ACTION_BAR_EXIST = false;
    }
}
