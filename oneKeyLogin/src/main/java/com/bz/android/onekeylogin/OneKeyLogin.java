package com.bz.android.onekeylogin;

import android.content.Context;
import android.util.Log;

import androidx.annotation.IntDef;

import com.dahantc.dahantclibrary.DahantcPhone;
import com.dahantc.dahantclibrary.PhoneListener;

import org.json.JSONObject;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * 第三方一键登录统一管理类
 * 目前接入的三方一键登录服务为大汉三通
 * http://www.dahantc.com/
 */
public class OneKeyLogin{

    private static final String TAG = OneKeyLogin.class.getSimpleName();

    private static String appId;
    private static String appKey;
    private static Context contextApplication;


    private static int service;

    @Retention(SOURCE)
    @IntDef({ONE_KEY_SERVICE_DHST})
    public @interface OneKeyService {}
    public static final int ONE_KEY_SERVICE_DHST = 1;  //大汉三通

    public static OneKeyLogin getInstance() {
        return OneKeyLoginHolder.instance;
    }

    private OneKeyLogin() {
    }

    private static class OneKeyLoginHolder {
        private static OneKeyLogin instance = new OneKeyLogin();
    }

    /**
     * 工程与SDK交互结果的回调
     * */
    public interface OneKeyLoginCallback {

        /**
         * 获取鉴权Token条件符合，准备获取鉴权Token，回调用于UI处理
         * */
        void onGetAuthTokenStart();

        /**
         * 获取鉴权Token成功
         * */
        void onGetAuthTokenSuccess(String authToken);

        /**
         * 获取鉴权Token失败
         * */
        void onGetAuthTokenFail(String authToken);

        /**
         * 获取鉴权Token错误
         * */
        void onGetAuthTokenError();
    }

    /**
     * 进行SDK初始化
     * @param context 上下文
     * @param oneKeyService 三方一键登录服务商
     * @param oneKeyAppId 三方一键登录应用Id, 必须
     * @param oneKeyAppKey 三方一键登录应用AppKey或者AppSecret,  根据第三方服务所需为必选或可选
     */
    public static void init(Context context, @OneKeyService int oneKeyService, String oneKeyAppId, String oneKeyAppKey){
        contextApplication = context.getApplicationContext();
        appId = oneKeyAppId;
        appKey = oneKeyAppKey;
        service = oneKeyService;
       switch (service){
           case ONE_KEY_SERVICE_DHST:
               try{
                   if(context == null){
                       throw new NullPointerException("未能正常初始化，context对象为null");
                   }

                   if(appId == null){
                       throw new NullPointerException("未能正常初始化，appId对象为null");
                   }

                   if(appKey == null){
                       throw new NullPointerException("未能正常初始化，appKey对象为null");
                   }

                   DahantcPhone.getInstance().initializeUser(context, appId, appKey);

               }catch (Exception e){
                   if(e != null){
                       Log.e(TAG, e.getMessage());
                   }
               }
               break;
       }
    }

    /**
     * 向SDK获取鉴权token
     * token用于向自己的服务端请求手机号
     * @param callback 获取鉴权token的回调
     */
    public void getAuthToken(final OneKeyLoginCallback callback){
        switch (service){
            case ONE_KEY_SERVICE_DHST:
                try{
                    if(contextApplication == null){
                        throw new NullPointerException("getAccessCode失败，context对象为null");
                    }

                    if(appId == null){
                        throw new NullPointerException("getAccessCode失败，appId对象为null");
                    }

                    if(appKey == null){
                        throw new NullPointerException("getAccessCode失败，appKey对象为null");
                    }

                    if(callback == null){
                        throw new NullPointerException("getAccessCode失败，OneKeyLoginCallback对象为null");
                    }

                    callback.onGetAuthTokenStart();

                    DahantcPhone.getInstance().GetUserPhone(contextApplication, appId, appKey, new PhoneListener(){
                        @Override
                        public void onGetTokenComplete(JSONObject jsonObject) {
                            if(BuildConfig.DEBUG){
                                Log.d(TAG, "获取token响应：" + jsonObject.toString());
                            }
                            try {
                                if ("success".equals(jsonObject.optString("result"))) {
                                    //成功，根据msgId向服务端请求手机号或进行号码校验，
                                    //请求方法参照《移动token取号接口文档》、《号码校验接口文档》
                                    String msgId = jsonObject.optString("msgId");
                                    if(callback != null && msgId != null){
                                        callback.onGetAuthTokenSuccess(msgId);
                                    }
                                } else {
                                    //失败，跳转自定义的登录页面
                                    if(BuildConfig.DEBUG){
                                        Log.e(TAG, "未获取到token");
                                    }
                                    String errorMsg = jsonObject.optString("msg");
                                    if(callback != null && errorMsg != null){
                                        callback.onGetAuthTokenFail(errorMsg);
                                    }
                                }
                            } catch (Exception e) {
                                //解析jsonObject异常，跳转自定义的登录页面
                                if(BuildConfig.DEBUG){
                                    Log.e(TAG, "解析result异常", e);
                                }

                                if(callback != null){
                                    callback.onGetAuthTokenError();
                                }
                            }
                        }
                    });

                }catch (Exception e){
                    if(e != null){
                        Log.e(TAG, e.getMessage());
                    }
                }
                break;
        }
    }
}
