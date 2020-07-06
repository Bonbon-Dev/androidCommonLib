package com.bz.android.push;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.common.inter.ITagManager;
import com.umeng.message.tag.TagManager;

import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.mezu.MeizuRegister;
import org.android.agoo.oppo.OppoRegister;
import org.android.agoo.vivo.VivoRegister;
import org.android.agoo.xiaomi.MiPushRegistar;

import java.lang.reflect.Method;

public class PushManager {

    private static class PushManagerHolder {
        static PushManager instance = new PushManager();
    }

    public PushManager getInstance() {
        return PushManagerHolder.instance;
    }

    public static final String TAG = PushManager.class.getSimpleName();

    PushEnv pushEnv;
    PushAgent pushAgent;

    boolean hwEnable;
    boolean vivoEnable;
    boolean miEnable;
    String miPushId;
    String miPushKey;
    boolean mzEnable;
    String mzPushId;
    String mzPushKey;
    boolean oppoEnable;
    String oppoPushSecret;
    String oppoPushKey;
    int soundMode = 0;




    public interface PushEnv {
        boolean getHWPushEnable();

        boolean getVIVOPushEnable();

        boolean getMiPushEnable();

        String getMiPushAppId();

        String getMiPushAppKey();

        boolean getMZPushEnable();

        String getMZPushAppId();

        String getMZPushAppKey();

        boolean getOPPOPushEnable();

        String getOPPOPushAppId();

        String getOPPOPushAppKey();

        int getNotificationSoundMode();

        void getDeviceTokenSuccess(String deviceToken);

        void getDeviceTokenFailure(String s1, String s2);

    }

    public void setPushContextEnv(PushEnv pushEnv){
        if(pushEnv == null){
            return;
        }

        this.pushEnv = pushEnv;

        hwEnable = pushEnv.getHWPushEnable();
        vivoEnable = pushEnv.getVIVOPushEnable();
        miEnable = pushEnv.getMiPushEnable();
        miPushId = pushEnv.getMiPushAppId();
        miPushKey = pushEnv.getMiPushAppKey();
        mzEnable = pushEnv.getMZPushEnable();
        mzPushId = pushEnv.getMZPushAppId();
        mzPushKey = pushEnv.getMZPushAppKey();
        oppoEnable = pushEnv.getOPPOPushEnable();
        oppoPushSecret = pushEnv.getOPPOPushAppId();
        oppoPushKey = pushEnv.getOPPOPushAppKey();
        soundMode = pushEnv.getNotificationSoundMode();

    }

    public void initPush(Context context) {

        //获取消息推送代理示例
        pushAgent = PushAgent.getInstance(context.getApplicationContext());
        pushAgent.setNotificationPlaySound(soundMode); //服务端控制声音

        pushAgent.setResourcePackageName("com.bz.android.push");

        //注册推送服务，每次调用register方法都会回调该接口
        pushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                Log.i(TAG,"注册成功：deviceToken：-------->  " + deviceToken);
                if(pushEnv != null){
                    pushEnv.getDeviceTokenSuccess(deviceToken);
                }
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.i(TAG,"注册失败：-------->  " + "s:" + s + ",s1:" + s1);
                if(pushEnv != null){
                    pushEnv.getDeviceTokenFailure(s, s1);
                }
            }
        });


        initSystemPush(context);
    }

    private void initSystemPush(Context context) {
        /**
         * 初始化厂商通道
         */
        if(miEnable){
            //小米通道
            MiPushRegistar.register(context.getApplicationContext(), miPushId, miPushKey);
        }

        if(hwEnable){
            //华为通道，注意华为通道的初始化参数在minifest中配置
            HuaWeiRegister.register(getApplicationInner());
        }

        if(mzEnable){
            //魅族通道
            MeizuRegister.register(context.getApplicationContext(), mzPushId, mzPushKey);
        }

        if(oppoEnable){
            //OPPO通道
            OppoRegister.register(context.getApplicationContext(), oppoPushKey, oppoPushSecret);
        }

        if(vivoEnable){
            //VIVO 通道，注意VIVO通道的初始化参数在minifest中配置
            VivoRegister.register(context.getApplicationContext());
        }
    }

    public void addTag(String... tags){
        //添加标签 示例：将“标签1”、“标签2”绑定至该设备
        pushAgent.getTagManager().addTags(new TagManager.TCallBack() {
            @Override
            public void onMessage(final boolean isSuccess, final ITagManager.Result result) {

            }
        }, tags);
    }

    public static Application getApplicationInner() {
        try {
            Class<?> activityThread = Class.forName("android.app.ActivityThread");

            Method currentApplication = activityThread.getDeclaredMethod("currentApplication");
            Method currentActivityThread = activityThread.getDeclaredMethod("currentActivityThread");

            Object current = currentActivityThread.invoke((Object)null);
            Object app = currentApplication.invoke(current);

            return (Application)app;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
