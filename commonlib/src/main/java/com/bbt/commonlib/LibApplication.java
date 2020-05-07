package com.bbt.commonlib;

import android.app.Application;

import com.bbt.commonlib.operationutil.ProcessUtils;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.tencent.mmkv.MMKV;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
  *  @author lixiaonan
  *  功能描述: lib工程的Appliction类的
  *  时 间： 2019-11-14 16:32
  */
public class LibApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //在主进程初始化一些东西
        if(ProcessUtils.isMainProcess()){
            //初始化微信sp架构的
            MMKV.initialize(this);
            initThird();
        }
    }

    /**
     * 初始化第三方组件
     */
    private void initThird() {
        initLog();
    }

    /**
     * 初始化日志控件
     */
    private void initLog() {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(true)
                // (Optional) How many method line to show. Default 2
                .methodCount(0)
                // (Optional) Hides internal method calls up to offset. Default 5
                .methodOffset(0)
                //.logStrategy(customLog) // (Optional) Changes the log strategy to d out. Default LogCat
                // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .tag("wwlog")
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }

            @Override
            public void log(int priority, @Nullable String tag, @NonNull String message) {
                super.log(priority, tag, message);
            }
        });
    }
}
