package com.bbt.commonlib;

import android.app.Application;

import com.bbt.commonlib.operationutil.ProcessUtils;
import com.tencent.mmkv.MMKV;

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
        }

    }
}
