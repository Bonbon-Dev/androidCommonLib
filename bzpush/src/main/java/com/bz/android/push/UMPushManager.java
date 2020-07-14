package com.bz.android.push;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.common.inter.ITagManager;
import com.umeng.message.entity.UMessage;
import com.umeng.message.tag.TagManager;

import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.mezu.MeizuRegister;
import org.android.agoo.oppo.OppoRegister;
import org.android.agoo.vivo.VivoRegister;
import org.android.agoo.xiaomi.MiPushRegistar;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
  *  @author ZhangYi
  *  功能描述: 友盟推送管理类
  *  时 间： 2020/7/14 3:35 PM
  */
public class UMPushManager {

    private static class PushManagerHolder {
        static UMPushManager instance = new UMPushManager();
    }

    public static UMPushManager getInstance() {
        return PushManagerHolder.instance;
    }

    private UMPushManager(){

    }

    public static final String TAG = UMPushManager.class.getSimpleName();

    public static final String KEY_PUSH_ENABLE_HW = "KEY_PUSH_ENABLE_HW";

    public static final String KEY_PUSH_ENABLE_VIVO = "KEY_PUSH_ENABLE_VIVO";

    public static final String KEY_PUSH_ENABLE_MI = "KEY_PUSH_ENABLE_MI";

    public static final String KEY_PUSH_ENABLE_OPPO = "KEY_PUSH_ENABLE_OPPO";

    public static final String KEY_PUSH_ENABLE_MZ = "KEY_PUSH_ENABLE_MZ";

    public static final String KEY_PUSH_SOUND_TYPE = "KEY_PUSH_SOUND_TYPE";

    public static final String KEY_MI_PUSH_APP_ID = "KEY_MI_PUSH_APP_ID";

    public static final String KEY_MI_PUSH_APP_KEY = "KEY_MI_PUSH_APP_KEY";

    public static final String KEY_MZ_PUSH_APP_ID = "KEY_MZ_PUSH_APP_ID";

    public static final String KEY_MZ_PUSH_APP_KEY = "KEY_MZ_PUSH_APP_KEY";

    public static final String KEY_OPPO_PUSH_APP_SECRET = "KEY_OPPO_PUSH_APP_SECRET";

    public static final String KEY_OPPO_PUSH_APP_KEY = "KEY_OPPO_PUSH_APP_KEY";


    PushInitResult pushInitResult;
    PushMsgClickListener clickListener;
    PushTagResult tagResult;
    PushAliasResult aliasResult;
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
    int soundMode = -1;
    /**
     * 启用推送
     * */
    boolean enablePush;
    /**
     * 启用Log
     * */
    boolean printLog;




    public interface PushInitResult {
        /**
         * 获取deviceToken成功的回调
         * */
        void getDeviceTokenSuccess(@NonNull String deviceToken);

        /**
         * 获取deviceToken失败的回调
         * */
        void getDeviceTokenFailure(@NonNull String errorCode, @NonNull String msg);
    }


    public interface PushTagResult {
        /**
         * 对设备设置标签的回调(设置标签不依赖deviceToken的获取)
         * */
        void onAddTagResult(boolean isSuccess, @Nullable ITagManager.Result result);

        /**
         * 对设备设置标签的回调(设置标签不依赖deviceToken的获取)
         * */
        void onDelTagResult(boolean isSuccess, @Nullable ITagManager.Result result);

        /**
         * 获取设备标签的回调
         * */
        void onGetTagResult(boolean isSuccess, @Nullable List<String> result);
    }



    public interface PushAliasResult {
        /**
         * 对deviceToken添加别名的回调(添加别名依赖deviceToken的获取)
         * */
        void onAddAliasResult(boolean isSuccess, @Nullable String message);

        /**
         * 对deviceToken绑定别名的回调(绑定别名依赖deviceToken的获取)
         * */
        void onBindAliasResult(boolean isSuccess, @Nullable String message);

        /**
         * 移除和deviceToken绑定别名的回调
         * */
        void onDelAliasResult(boolean isSuccess, @Nullable String message);
    }


    public interface PushMsgClickListener {
        /**
         * 获取deviceToken成功的回调
         * */
        void onClickPushMsg(@NonNull BZPushMsg bzPushMsg);
    }

    /**
     * 初始化推送之前调用
     * */
    public void setPushContextEnv(boolean enablePush, boolean printLog, @NonNull Map<String, Object> envMap){
        this.enablePush = enablePush;
        this.printLog = printLog;

        if(!enablePush){
            return;
        }

        if(envMap.size() <= 0){
            print("没有正确设置初始化环境");
            return ;
        }

        if(envMap.get(KEY_PUSH_ENABLE_HW) != null && envMap.get(KEY_PUSH_ENABLE_HW) instanceof Boolean){
            hwEnable = (Boolean) envMap.get(KEY_PUSH_ENABLE_HW);
        }

        if(envMap.get(KEY_PUSH_ENABLE_VIVO) != null && envMap.get(KEY_PUSH_ENABLE_VIVO) instanceof Boolean){
            vivoEnable = (Boolean) envMap.get(KEY_PUSH_ENABLE_VIVO);
        }

        if(envMap.get(KEY_PUSH_ENABLE_MI) != null && envMap.get(KEY_PUSH_ENABLE_MI) instanceof Boolean){
            miEnable = (Boolean) envMap.get(KEY_PUSH_ENABLE_MI);
        }

        if(envMap.get(KEY_PUSH_ENABLE_OPPO) != null && envMap.get(KEY_PUSH_ENABLE_OPPO) instanceof Boolean){
            oppoEnable = (Boolean) envMap.get(KEY_PUSH_ENABLE_OPPO);
        }

        if(envMap.get(KEY_PUSH_ENABLE_MZ) != null && envMap.get(KEY_PUSH_ENABLE_MZ) instanceof Boolean){
            mzEnable = (Boolean) envMap.get(KEY_PUSH_ENABLE_MZ);
        }

        if(envMap.get(KEY_PUSH_SOUND_TYPE) != null && envMap.get(KEY_PUSH_SOUND_TYPE) instanceof Integer){
            soundMode = (Integer) envMap.get(KEY_PUSH_SOUND_TYPE);
        }

        if(envMap.get(KEY_MI_PUSH_APP_ID) != null && envMap.get(KEY_MI_PUSH_APP_ID) instanceof String){
            miPushId = (String) envMap.get(KEY_MI_PUSH_APP_ID);
        }

        if(envMap.get(KEY_MI_PUSH_APP_KEY) != null && envMap.get(KEY_MI_PUSH_APP_KEY) instanceof String){
            miPushKey = (String) envMap.get(KEY_MI_PUSH_APP_KEY);
        }

        if(envMap.get(KEY_MZ_PUSH_APP_ID) != null && envMap.get(KEY_MZ_PUSH_APP_ID) instanceof String){
            mzPushId = (String) envMap.get(KEY_MZ_PUSH_APP_ID);
        }

        if(envMap.get(KEY_MZ_PUSH_APP_KEY) != null && envMap.get(KEY_MZ_PUSH_APP_KEY) instanceof String){
            mzPushKey = (String) envMap.get(KEY_MZ_PUSH_APP_KEY);
        }

        if(envMap.get(KEY_OPPO_PUSH_APP_SECRET) != null && envMap.get(KEY_OPPO_PUSH_APP_SECRET) instanceof String){
            oppoPushSecret = (String) envMap.get(KEY_OPPO_PUSH_APP_SECRET);
        }

        if(envMap.get(KEY_OPPO_PUSH_APP_KEY) != null && envMap.get(KEY_OPPO_PUSH_APP_KEY) instanceof String){
            oppoPushKey = (String) envMap.get(KEY_OPPO_PUSH_APP_KEY);
        }
    }

    public void setPushInitResult(PushInitResult pushInitResult){
        this.pushInitResult = pushInitResult;
    }

    public void setPushMsgClickListener(PushMsgClickListener pushMsgClickListener){
        this.clickListener = pushMsgClickListener;
    }

    public void setPushAliasResult(PushAliasResult aliasResult){
        this.aliasResult = aliasResult;
    }

    public void setPushTagResult(PushTagResult tagResult){
        this.tagResult = tagResult;
    }

    private void print(String msg) {
        if(enablePush){
            Log.d(TAG, msg);
        }
    }

    public void initPush(Context context) {

        if(!enablePush){
            return;
        }

        //获取消息推送代理示例
        pushAgent = PushAgent.getInstance(context.getApplicationContext());

        if(soundMode != -1){
            pushAgent.setNotificationPlaySound(soundMode); //服务端控制声音
        }

        pushAgent.setResourcePackageName("com.bz.android.push");

        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                //友盟通道的推送
                distributePushMsg("");
            }
        };

        pushAgent.setNotificationClickHandler(notificationClickHandler);

        //注册推送服务，每次调用register方法都会回调该接口
        pushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                print("注册成功：deviceToken：-------->  " + deviceToken);
                if(pushInitResult != null){
                    pushInitResult.getDeviceTokenSuccess(deviceToken);
                }
            }

            @Override
            public void onFailure(String s, String s1) {
                print("注册失败：-------->  " + "s:" + s + ",s1:" + s1);
                if(pushInitResult != null){
                    pushInitResult.getDeviceTokenFailure(s, s1);
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
            if(miPushId != null && miPushId.length() > 0 && miPushKey != null && miPushKey.length() > 0){
                MiPushRegistar.register(context.getApplicationContext(), miPushId, miPushKey);
            } else {
                print("小米推送参数为null, 不能注册");
            }
        }

        if(hwEnable){
            //华为通道，注意华为通道的初始化参数在minifest中配置
            HuaWeiRegister.register(getApplicationInner());
        }

        if(mzEnable){
            //魅族通道
            if(mzPushId != null && mzPushId.length() > 0 && mzPushKey != null && mzPushKey.length() > 0){
                MeizuRegister.register(context.getApplicationContext(), mzPushId, mzPushKey);
            } else {
                print("魅族推送参数为null, 不能注册");
            }
        }

        if(oppoEnable){
            //OPPO通道
            if(oppoPushKey != null && oppoPushKey.length() > 0 && oppoPushSecret != null && oppoPushSecret.length() > 0){
                OppoRegister.register(context.getApplicationContext(), oppoPushKey, oppoPushSecret);
            }else {
                print("OPPO推送参数为null, 不能注册");
            }
        }

        if(vivoEnable){
            //VIVO 通道，注意VIVO通道的初始化参数在minifest中配置
            VivoRegister.register(context.getApplicationContext());
        }
    }

    void distributePushMsg(String msg){
        if(clickListener!= null){
            clickListener.onClickPushMsg(new BZPushMsg());
        }
    }

    public void addTag(String... tags){
        //添加标签 示例：将“标签1”、“标签2”绑定至该设备
        pushAgent.getTagManager().addTags(new TagManager.TCallBack() {
            @Override
            public void onMessage(final boolean isSuccess, final ITagManager.Result result) {
                if(tagResult != null){
                    tagResult.onAddTagResult(isSuccess, result);
                }
            }
        }, tags);
    }

    public void delTag(String... tags){
        //删除标签,将之前添加的标签中的一个或多个删除
        pushAgent.getTagManager().deleteTags(new TagManager.TCallBack() {
            @Override
            public void onMessage(final boolean isSuccess, final ITagManager.Result result) {
                if(tagResult != null){
                    tagResult.onDelTagResult(isSuccess, result);
                }
            }
        }, tags);
    }

    public void getTag(){
        //获取服务器端的所有标签
        pushAgent.getTagManager().getTags(new TagManager.TagListCallBack() {
            @Override
            public void onMessage(boolean isSuccess, List<String> result) {
                if(tagResult != null){
                    tagResult.onGetTagResult(isSuccess, result);
                }
            }
        });
    }

    /**
     * 别名增加，
     * 将某一类型的别名ID绑定至某设备，老的绑定设备信息还在，
     * 别名ID和device_token是一对多的映射关系，适合多端登陆
     * 注：别名的添加依赖device_token的获取
     * @param aliasId 别名id
     * @param aliasId 别名类型
     * 参考：https://developer.umeng.com/docs/66632/detail/89996
     * **/
    public void addAlias(String aliasId, String aliasType){
        if(aliasId != null && aliasId.length() > 0 && aliasType != null && aliasType.length() > 0){
            pushAgent.addAlias(aliasId, aliasType, new UTrack.ICallBack() {
                @Override
                public void onMessage(boolean isSuccess, String message) {
                    if(aliasResult != null){
                        aliasResult.onAddAliasResult(isSuccess, message);
                    }
                }
            });
        }
    }

    /**
     * 别名绑定，
     * 将某一类型的别名ID绑定至某设备，老的绑定设备信息被覆盖，
     * 别名ID和deviceToken是一对一的映射关系,适合单端登陆
     * 注：别名的添加依赖device_token的获取
     * @param aliasId 别名id
     * @param aliasId 别名类型
     * 参考：https://developer.umeng.com/docs/66632/detail/89996
     * **/
    public void bindAlias(String aliasId, String aliasType){
        if(aliasId != null && aliasId.length() > 0 && aliasType != null && aliasType.length() > 0){
            pushAgent.setAlias(aliasId, aliasType, new UTrack.ICallBack() {
                @Override
                public void onMessage(boolean isSuccess, String message) {
                    if(aliasResult != null){
                        aliasResult.onBindAliasResult(isSuccess, message);
                    }
                }
            });
        }
    }


    /**
     * //移除别名ID
     * 注：别名的添加依赖device_token的获取
     * @param aliasId 别名id
     * @param aliasId 别名类型
     * 参考：https://developer.umeng.com/docs/66632/detail/89996
     * **/
    public void delAlias(String aliasId, String aliasType){
        if(aliasId != null && aliasId.length() > 0 && aliasType != null && aliasType.length() > 0){
            pushAgent.deleteAlias(aliasId, aliasType, new UTrack.ICallBack() {
                @Override
                public void onMessage(boolean isSuccess, String message) {
                    if(aliasResult != null){
                        aliasResult.onDelAliasResult(isSuccess, message);
                    }
                }
            });
        }
    }

    private static Application getApplicationInner() {
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
