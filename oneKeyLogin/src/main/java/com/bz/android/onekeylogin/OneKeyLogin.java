package com.bz.android.onekeylogin;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.dahantc.dahantclibrary.DahantcPhone;
import com.dahantc.dahantclibrary.PhoneListener;
import com.dahantc.dahantclibrary.UserThemeConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * 第三方一键登录统一管理类
 * 目前接入的三方一键登录服务为大汉三通
 * http://www.dahantc.com/
 */
public class OneKeyLogin {

    private static final String TAG = OneKeyLogin.class.getSimpleName();

    private static String appId;
    private static String appKey;
    private static Context contextApplication;

    private static boolean isShowLog;


    private static int service;

    @Retention(SOURCE)
    @IntDef({ONE_KEY_SERVICE_DHST})
    public @interface OneKeyService {
    }
    //大汉三通的
    public static final int ONE_KEY_SERVICE_DHST = 1;

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
     */
    public interface OneKeyLoginCallback {

        /**
         * 获取鉴权Token条件符合，准备获取鉴权Token，回调用于UI处理
         */
        void onGetAuthTokenStart();

        /**
         * 获取鉴权Token成功
         */
        void onGetAuthTokenSuccess(String authToken, String type);

        /**
         * 获取鉴权Token失败
         */
        void onGetAuthTokenFail(String errorMsg);

        /**
         * 获取鉴权Token错误
         */
        void onGetAuthTokenError();
    }

    /**
     * 进行SDK初始化
     *
     * @param context       上下文
     * @param oneKeyService 三方一键登录服务商
     * @param oneKeyAppId   三方一键登录应用Id, 必须
     * @param oneKeyAppKey  三方一键登录应用AppKey或者AppSecret,  根据第三方服务所需为必选或可选
     */
    public static void init(Context context, @OneKeyService int oneKeyService, String oneKeyAppId, String oneKeyAppKey) {
        contextApplication = context.getApplicationContext();
        appId = oneKeyAppId;
        appKey = oneKeyAppKey;
        service = oneKeyService;
        switch (service) {
            case ONE_KEY_SERVICE_DHST:
                try {
                    if (context == null) {
                        throw new NullPointerException("未能正常初始化，context对象为null");
                    }

                    if (appId == null) {
                        throw new NullPointerException("未能正常初始化，appId对象为null");
                    }

                    if (appKey == null) {
                        throw new NullPointerException("未能正常初始化，appKey对象为null");
                    }
                    if (isShowLog) {
                        DahantcPhone.getInstance().setTestModel("");
                        DahantcPhone.getInstance().logPackageAndSign(contextApplication);
                    }
                    DahantcPhone.getInstance().initializeUser(context, appId, appKey);

                } catch (Exception e) {
                    if (e != null) {
                        Log.e(TAG, e.getMessage());
                    }
                }
                break;
            default:
                break;
        }
    }


    /**
     * 是否显示日志的
     *
     * @param isShowLog
     */
    public static void setIsShowLog(boolean isShowLog) {
        OneKeyLogin.isShowLog = isShowLog;
    }

    /**
     * 向SDK获取鉴权token
     * token用于向自己的服务端请求手机号
     *
     * @param callback 获取鉴权token的回调
     */
    public void getAuthToken(@NonNull Activity activity, final OneKeyLoginCallback callback) {
        getAuthToken(activity,null, callback);
    }

    /**
     * 向SDK获取鉴权token
     * token用于向自己的服务端请求手机号
     * userThemeConfigBuilder 用于自定义界面的
     *
     * @param callback 获取鉴权token的回调
     */
    public void getAuthToken(@NonNull Activity  activity, UserThemeConfig.UserThemeConfigBuilder userThemeConfigBuilder, final OneKeyLoginCallback callback) {
        switch (service) {
            case ONE_KEY_SERVICE_DHST:
                try {
                    if (contextApplication == null) {
                        throw new NullPointerException("getAccessCode失败，context对象为null");
                    }

                    if (appId == null) {
                        throw new NullPointerException("getAccessCode失败，appId对象为null");
                    }

                    if (appKey == null) {
                        throw new NullPointerException("getAccessCode失败，appKey对象为null");
                    }

                    if (callback == null) {
                        throw new NullPointerException("getAccessCode失败，OneKeyLoginCallback对象为null");
                    }

                    callback.onGetAuthTokenStart();
                    PhoneListener phoneListener = new PhoneListener() {
                        @Override
                        public void onGetTokenComplete(JSONObject jsonObject) {
                            if (isShowLog) {
                                Log.d(TAG, "获取token响应：" + jsonObject.toString());
                            }
                            try {
                                if ("success".equals(jsonObject.optString("result"))) {
                                    //成功，根据msgId向服务端请求手机号或进行号码校验，
                                    //请求方法参照《移动token取号接口文档》、《号码校验接口文档》
                                    String msgId = jsonObject.optString("msgId");
                                    String type = jsonObject.optString("type");
                                    if (msgId != null) {
                                        callback.onGetAuthTokenSuccess(msgId, type);
                                    }
                                } else {
                                    //失败，跳转自定义的登录页面
                                    if (isShowLog) {
                                        Log.e(TAG, "未获取到token");
                                    }
                                    String errorMsg = jsonObject.optString("msg");
                                    if (errorMsg != null) {
                                        callback.onGetAuthTokenFail(errorMsg);
                                    }
                                }
                            } catch (Exception e) {
                                //解析jsonObject异常，跳转自定义的登录页面
                                if (isShowLog) {
                                    Log.e(TAG, "解析result异常", e);
                                }
                                callback.onGetAuthTokenError();
                            }
                        }
                    };
                    if (null != userThemeConfigBuilder) {
                        DahantcPhone.getInstance().GetUserPhone(activity, appId, appKey, userThemeConfigBuilder, phoneListener);
                    } else {
                        DahantcPhone.getInstance().GetUserPhone(activity, appId, appKey, phoneListener);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    if (callback != null) {
                        callback.onGetAuthTokenError();
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 获取流量类型的
     * @return
     */
    public JSONObject getNetworkType(){
        JSONObject network = DahantcPhone.getInstance().getNetworkType(contextApplication);
        return network;
    }

    /**
     * 获取是否是联通流量
     * @return 未开启流量或者不是联通流量都是false
     */
    public boolean getIsUnicom(){
        try {
            JSONObject network = DahantcPhone.getInstance().getNetworkType(contextApplication);
            boolean isUnicom = false;
            if(null!=network){
                //networkType 0.未知；1.流量；2.wiﬁ； 3.数据流量+wiﬁ
                String networkType=network.getString("networkType");
                if("1".equals(networkType)||"3".equals(networkType)){
                    //operatorType 运营商类型：1.移动流量；2.联通流量；3.电信流量
                    if("2".equals(network.getString("operatorType"))){
                        isUnicom=true;
                    }
                }
            }
            return isUnicom;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 获取是否是电信流量
     * @return 未开启流量或者不是联通流量都是false
     */
    public boolean getIsCtcc(){
        try {
            JSONObject network = DahantcPhone.getInstance().getNetworkType(contextApplication);
            boolean isctcc = false;
            if(null!=network){
                //networkType 0.未知；1.流量；2.wiﬁ； 3.数据流量+wiﬁ
                String networkType=network.getString("networkType");
                if("1".equals(networkType)||"3".equals(networkType)){
                    //operatorType 运营商类型：1.移动流量；2.联通流量；3.电信流量
                    if("3".equals(network.getString("operatorType"))){
                        isctcc=true;
                    }
                }
            }
            return isctcc;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取运营商类型的
     *
     * @return
     */
    public String getOperatorType() {
        try {
            JSONObject network = DahantcPhone.getInstance().getNetworkType(contextApplication);
            String operatorType = "";
            if (null != network) {
                operatorType = network.getString("operatorType");

            }
            return operatorType;
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}
