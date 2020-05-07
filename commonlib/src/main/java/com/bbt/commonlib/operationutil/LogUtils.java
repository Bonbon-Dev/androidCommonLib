package com.bbt.commonlib.operationutil;

import com.bbt.commonlib.toolutil.StringUtil;
import com.orhanobut.logger.Logger;

import androidx.annotation.Nullable;

/**
  *  @author zhangyi
  *  功能描述: 第三方日志使用的
  *  时 间： 2020/5/7 6:28 PM
  */
public class LogUtils {

    private static final String DEFAULT_LOG_TAG = "wwLog";

    /////////////////     VERBOSE            //////////////////////////////
    public static void v(String log) {
        v(null, log);
    }

    public static void v(String tag, String log) {
        printLog(Logger.VERBOSE, tag, log, null);
    }
    /////////////////     VERBOSE            //////////////////////////////
    /////////////////     info            //////////////////////////////

    public static void i(String log) {
        i(null, log);
    }

    public static void i(String tag, String log) {
        printLog(Logger.INFO, tag, log, null);
    }

    /////////////////     info            //////////////////////////////
    /////////////////     debug            //////////////////////////////

    public static void d(String log) {
        d(null, log);
    }

    public static void d(int log) {
        d(String.valueOf(log));
    }

    public static void d(String tag, String log) {
        printLog(Logger.DEBUG, tag, log, null);
    }

    /////////////////     debug            //////////////////////////////
    /////////////////     warning            //////////////////////////////


    public static void w(String log) {
        w(null, log);
    }

    public static void w(String tag, String log) {
        printLog(Logger.WARN, tag, log, null);

    }

    /////////////////     warning            //////////////////////////////
    /////////////////     error            //////////////////////////////

    public static void e(String log) {
        e(null, log);
    }

    public static void e(String tag, String log) {
        e(tag, log, null);
    }

    public static void e(String tag, String log, @Nullable Throwable throwable) {
        printLog(Logger.ERROR, tag, log, throwable);
    }

    /////////////////     error            //////////////////////////////

    /////////////////     json            //////////////////////////////

    public static void json(String json) {
        json(false, null, json);
    }

    public static void json(String tag, String json) {
        json(true, tag, json);
    }

    public static void json(boolean needTag, String tag, String json) {
        printJson(needTag, tag, json);
    }

    /////////////////     json            //////////////////////////////
    /////////////////     xml            //////////////////////////////

    public static void xml(String xml) {
        xml(false, null, xml);
    }

    public static void xml(String tag, String xml) {
        xml(true, tag, xml);
    }

    public static void xml(boolean needTag, String tag, String xml) {
        printXml(needTag, tag, xml);
    }

    /////////////////     xml            //////////////////////////////


    private static void printJson(boolean needTag, String tag, String json) {
        if (StringUtil.isNotEmptyString(tag)) {
            Logger.t(tag).json(json);
        } else {
            if (needTag) {
                Logger.t(DEFAULT_LOG_TAG).json(json);
            } else {
                Logger.json(json);
            }
        }
    }

    private static void printXml(boolean needTag, String tag, String xml) {
        if (StringUtil.isNotEmptyString(tag)) {
            Logger.t(tag).xml(xml);
        } else {
            if (needTag) {
                Logger.t(DEFAULT_LOG_TAG).xml(xml);
            } else {
                Logger.xml(xml);
            }
        }
    }

    private static void printLog(int level, String tag, String log, @Nullable Throwable throwable) {
        switch (level) {
            case Logger.VERBOSE:
                if (StringUtil.isNotEmptyString(tag)) {
                    Logger.t(tag).v(log);
                } else {
                    Logger.t(DEFAULT_LOG_TAG).v(log);
                }
                break;
            case Logger.DEBUG:
                try {
                    if (StringUtil.isNotEmptyString(tag)) {
                        Logger.t(tag).d(log);
                    } else {
                        Logger.t(DEFAULT_LOG_TAG).d(log);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case Logger.INFO:
                if (StringUtil.isNotEmptyString(tag)) {
                    Logger.t(tag).i(log);
                } else {
                    Logger.t(DEFAULT_LOG_TAG).i(log);
                }
                break;
            case Logger.WARN:
                if (StringUtil.isNotEmptyString(tag)) {
                    Logger.t(tag).w(log);
                } else {
                    Logger.t(DEFAULT_LOG_TAG).w(log);
                }
                break;
            case Logger.ERROR:
                if (StringUtil.isNotEmptyString(tag)) {
                    Logger.t(tag).e(log);
                } else {
                    Logger.t(DEFAULT_LOG_TAG).e(throwable, log);
                }
                break;
            default:
                break;
        }
    }

}
