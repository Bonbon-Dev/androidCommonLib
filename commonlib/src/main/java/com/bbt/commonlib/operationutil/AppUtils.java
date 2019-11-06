package com.bbt.commonlib.operationutil;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.util.List;

/**
 * @author lixiaonan
 * 功能描述: app相关的工具类
 * 时 间： 2019-11-06 14:31
 */
public final class AppUtils {

    private AppUtils() {
    }


    /**
     * 返回当前应用是否在前台
     *
     * @return 在前台为true
     */
    public static boolean isAppForeground() {
        return Utils.isAppForeground();
    }


    /**
     * 退出应用的 待实验效果看对界面的onDestroy有没影响
     */
    public static void exitApp() {
        List<Activity> activityList = Utils.getActivityList();
        // remove from top
        for (int i = activityList.size() - 1; i >= 0; --i) {
            Activity activity = activityList.get(i);
            // sActivityList remove the index activity at onActivityDestroyed
            activity.finish();
        }
        System.exit(0);
    }

    /**
     * 获取当前应用的版本name
     *
     * @return
     */
    public static String getAppVersionName() {
        return getAppVersionName(Utils.getApp().getPackageName());
    }

    /**
     * 获取传入应用的版本name
     *
     * @param packageName
     * @return
     */
    public static String getAppVersionName(final String packageName) {
        try {
            PackageManager pm = Utils.getApp().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? null : pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取当期应用的版本号
     *
     * @return
     */
    public static long getAppVersionCode() {
        return getAppVersionCode(Utils.getApp());
    }

    /**
     * 获取出入的app的版本号
     *
     * @param context
     * @return long
     */
    public static long getAppVersionCode(Context context) {
        long appVersionCode = 0;
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                appVersionCode = packageInfo.getLongVersionCode();
            } else {
                appVersionCode = packageInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("", e.getMessage());
        }
        return appVersionCode;
    }


    /**
     * Return the application's user-ID.
     *
     * @return the application's signature for MD5 value
     */
    public static int getAppUid() {
        return getAppUid(Utils.getApp().getPackageName());
    }

    /**
     * Return the application's user-ID.
     *
     * @param pkgName The name of the package.
     * @return the application's signature for MD5 value
     */
    public static int getAppUid(String pkgName) {
        try {
            ApplicationInfo ai = Utils.getApp().getPackageManager().getApplicationInfo(pkgName, 0);
            if (ai != null) {
                return ai.uid;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


}
