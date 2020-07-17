package com.bz.android.push;

import android.content.Intent;
import android.os.Bundle;

import com.umeng.message.UmengNotifyClickActivity;

import org.android.agoo.common.AgooConstants;

/**
  *  @author ZhangYi
  *  功能描述: 配合友盟推送的：国产厂商系统推送的点击处理:
 *           小米，魅族，OPPO, VIVO, 华为
  *  时 间： 2020/7/14 3:50 PM
 *  参见：https://developer.umeng.com/docs/67966/detail/98589#h1--push-1
 *  使用友盟后台推送时以及后端使用Api进行推送时，需要知道本类全路径名称，以便实现调用系统推送通知
 *  com.bz.android.push.SystemPushClickActivity
  */
public class SystemPushClickActivity extends UmengNotifyClickActivity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    /**
     * 根据文档：本方法异步调用，不阻塞主线程
     * */
    @Override
    public void onMessage(Intent intent) {
        super.onMessage(intent);
        final String pushMsgBody = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //为避免（多次通过系统任务栈）打开页面的行为可能会出现问题而做出的处理
                //经验来自于小程序，可参考项目工程中 WXEntryActivity中的注释说明
                //如果经证实没有问题，可使用普通 finish()方法关闭页面
                finishAndRemoveTask();
                UMPushManager.getInstance().distributeNotificationMsg(true, null, pushMsgBody);
            }
        });
    }
}
