package com.bz.android.push;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.common.inter.ITagManager;
import com.umeng.message.entity.UMessage;
import com.umeng.message.tag.TagManager;

import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.mezu.MeizuRegister;
import org.android.agoo.oppo.OppoRegister;
import org.android.agoo.vivo.VivoRegister;
import org.android.agoo.xiaomi.MiPushRegistar;

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
    PushMsgListener msgListener;
    PushTagResult tagResult;
    PushAliasResult aliasResult;
    PushAgent pushAgent;  //推送SDK管理类

    /**
     * 启用华为推送
     * */
    boolean hwEnable;

    /**
     * 启用VIVO推送
     * */
    boolean vivoEnable;

    /**
     * 启用小米推送
     * */
    boolean miEnable;
    String miPushId;
    String miPushKey;

    /**
     * 启用魅族推送
     * */
    boolean mzEnable;
    String mzPushId;
    String mzPushKey;

    /**
     * 启用OPPO推送
     * */
    boolean oppoEnable;
    String oppoPushSecret;
    String oppoPushKey;

    /**
     * 通知声音模式
     * -1默认不设置
     * */
    int soundMode = -1;
    /**
     * 启用推送
     * */
    boolean enablePush;
    /**
     * 启用Log
     * */
    boolean printLog;
    /**
     * 可以设置别名
     * */
    boolean canAlias;




    /**
     * 推送SDK初始化回调
     * */
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

    /**
     * 推送SDK中标签管理回调
     * */
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


    /**
     * 推送SDK中别名管理回调
     * */
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

    /**
     * 推送消息点击回调
     * */
    public interface PushMsgListener {
        /**
         * 点击系统推送通知栏消息时的回调
         * */
        void onClickSystemNotificationMsg(@NonNull String pushMsgBody);

        /**
         * 点击友盟推送通知栏消息时的回调
         * */
        void onClickUMNotificationMsg(@Nullable Map<String, String> pushMsgExtraList);

        /**
         * 收到友盟透传消息时的回调
         * */
        void onGetUMPenetrateMsg(@Nullable String penetrateMsgJson);
    }

    /**
     * 初始化推送之前调用
     * @param enablePush 是否启用推送,为false时 {@link #setPushContextEnv(boolean, boolean, Map)}
     *                   和{@link #initPush(Application)}方法直接返回，不会做任何处理，推送SDK和服务不会启用
     * @param printLog 是否打印log，默认TAG为UMPushManager
     * @param envMap 推送SDK配置参数Map表，map不能为null也不能为空Map，本类中根据map中的值设置推送初始化参数
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

    /**
     * 设置推送SDK初始化的结果回调
     * */
    public void setPushInitResult(PushInitResult pushInitResult){
        this.pushInitResult = pushInitResult;
    }

    /**
     * 设置推送消息点击的回调
     * */
    public void setPushMsgListener(PushMsgListener pushMsgListener){
        this.msgListener = pushMsgListener;
    }

    /**
     * 给Device_Token设置别名的回调
     * */
    public void setPushAliasResult(PushAliasResult aliasResult){
        this.aliasResult = aliasResult;
    }

    /**
     * 给设备设置标签的回调
     * */
    public void setPushTagResult(PushTagResult tagResult){
        this.tagResult = tagResult;
    }

    /**
     * 打印log
     * */
    private void print(String msg) {
        if(enablePush){
            Log.d(TAG, msg);
        }
    }

    /**
     * 初始化推送
     * @param context 按华为推送的要求需要传入Application对象
     * */
    public void initPush(Application context) {

        if(!enablePush){
            return;
        }

        //获取消息推送代理示例
        pushAgent = PushAgent.getInstance(context.getApplicationContext());

        if(soundMode != -1){
            pushAgent.setNotificationPlaySound(soundMode); //服务端控制声音
        }

        pushAgent.setResourcePackageName("com.bz.android.push");

        //后续动作:点击通知栏消息处理
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {

            /**
             * 方式一：打开应用时回调
             * */
            @Override
            public void launchApp(Context context, UMessage msg) {
                super.launchApp(context, msg);
                distributeNotificationMsg(false, msg.extra, null);
            }

            /**
             * 方式二：打开链接(url)时回调
             * */
            @Override
            public void openUrl(Context context, UMessage uMessage) {
                super.openUrl(context, uMessage);
            }

            /**
             * 方式三：打开指定页面时回调
             * */
            @Override
            public void openActivity(Context context, UMessage uMessage) {
                super.openActivity(context, uMessage);
            }

            /**
             * 方式四：自定义行为时回调
             * */
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
            }

        };

        //后续动作:透传消息处理
        UmengMessageHandler messageHandler = new UmengMessageHandler(){
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                distributePenetrateMsg(msg.custom);
            }
        };

        pushAgent.setMessageHandler(messageHandler);

        pushAgent.setNotificationClickHandler(notificationClickHandler);

        //注册推送服务，每次调用register方法都会回调该接口
        pushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                print("注册成功：deviceToken：-------->  " + deviceToken);
                canAlias = true;
                if(pushInitResult != null){
                    pushInitResult.getDeviceTokenSuccess(deviceToken);
                }
            }

            @Override
            public void onFailure(String s, String s1) {
                print("注册失败：-------->  " + "s:" + s + ",s1:" + s1);
                canAlias = false;
                if(pushInitResult != null){
                    pushInitResult.getDeviceTokenFailure(s, s1);
                }
            }
        });

        initSystemPush(context);

    }

    /**
     * 初始化厂商通道
     * @param context 按华为推送的要求需要传入Application对象
     */
    private void initSystemPush(Application context) {
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
            HuaWeiRegister.register(context);
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

    /**
     * 向万物或棒棒糖分发消息
     * 由于通过系统推送推过来的额外消息的消息体是Object
     * 由于通过友盟推送推过来的额外消息的消息体是Map
     * 所以这里做了区分处理，不再进行统一的对外输出
     * (统一的对外输出会侵入业务，这不是这个库应该做的事情）
     * @param isFromSystem 消息体来自系统推送通知
     * @param pushMsgExtraList 友盟推送推过来的额外消息
     * @param pushMsgBody 系统推送推过来的消息
     * */
    void distributeNotificationMsg(boolean isFromSystem, Map<String, String> pushMsgExtraList, String pushMsgBody){
        if(msgListener != null){
            if(isFromSystem){
                msgListener.onClickSystemNotificationMsg(pushMsgBody);
            } else {
                msgListener.onClickUMNotificationMsg(pushMsgExtraList);
            }
        }
    }

    void distributePenetrateMsg(String penetrateMsgJson){
        if(msgListener != null){
            msgListener.onGetUMPenetrateMsg(penetrateMsgJson);
        }
    }

    /**
     * 给设备设置推送标签
     * */
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

    /**
     * 删除设备的推送标签
     * */
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

    /**
     * 获取设备的推送标签
     * */
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

    public void setAppStart(Context context){
        PushAgent.getInstance(context).onAppStart();
    }
}
