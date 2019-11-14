package com.bbt.commonlib.operationutil;

import android.content.Context;
import android.content.SharedPreferences;

import com.bbt.commonlib.toolutil.StringUtil;
import com.tencent.mmkv.MMKV;

import androidx.annotation.NonNull;

/**
 * @author lixiaonan
 * 功能描述: sp存储相关的服务的
 * 时 间： 2019-11-14 16:37
 */
public class SpSetUtils {

    private static SpSetUtils instance;

    private String oldSpName;

    public synchronized static SpSetUtils getInstance() {
        if (instance == null) {
            instance = new SpSetUtils();
        }
        return instance;
    }

    /**
     * 迁移数据相关记录是否迁移完成的
     */
    private static final String KEY_MIGRATE_DONE = "KEY_MIGRATE_DONE";
    private static boolean migrateDone;

    /**
     * 注意：因为万物和棒棒糖目前都只有一个sp文件 所以先默认一次就可以转移完成的
     * 导入旧的sp的数据如果导入旧的就用旧的文件名作为新的文件名
     *
     * @param oldSpName 旧的sp文件名
     * @return  迁移的key的个数
     */
    public int importFromOldSharedPreferences(@NonNull String oldSpName) {
        this.oldSpName = oldSpName;
        int importSize = 0;
        MMKV mPreferences = MMKV.mmkvWithID(oldSpName, MMKV.MULTI_PROCESS_MODE);
        if (!migrateDone) {
            if (mPreferences.contains(KEY_MIGRATE_DONE) && mPreferences.decodeBool(KEY_MIGRATE_DONE)) {
                //防止有不同步的情况
                migrateDone = true;
            } else {
                //迁移老数据
                SharedPreferences oldPreferences = Utils.getApp().getSharedPreferences(oldSpName, Context.MODE_PRIVATE);
                importSize = mPreferences.importFromSharedPreferences(oldPreferences);
                //设置迁移完成
                mPreferences.encode(KEY_MIGRATE_DONE, true);
                migrateDone = true;
                oldPreferences.edit().clear().commit();
            }
        }
        return importSize;
    }

    /**
     * 根据文件名字获取MMKV
     * @return mmkv文件
     */
    private MMKV getMMkVByFileName(String fileName) {
        if (StringUtil.isNotEmpty(fileName)) {
            return MMKV.mmkvWithID(fileName, MMKV.MULTI_PROCESS_MODE);
        } else {
            if (StringUtil.isNotEmpty(oldSpName)) {
                return MMKV.mmkvWithID(oldSpName, MMKV.MULTI_PROCESS_MODE);
            } else {
                return MMKV.defaultMMKV(MMKV.MULTI_PROCESS_MODE, null);
            }
        }
    }

    /**
     * 判断某个key是否存在的
     * @param key key值
     * @return 存在true
     */
    public boolean containsKey(@NonNull String key) {
        return this.containsKey("", key);
    }

    public boolean containsKey(String fileName, @NonNull String key) {
        MMKV mmkv = getMMkVByFileName(fileName);
        return mmkv.containsKey(key);
    }

    /**
     * 在默认文件中保存布尔值
     * @param key   key
     * @param value  value
     */
    public void putBoolean(@NonNull String key,boolean value) {
        this.putBoolean("", key, value);
    }

    /**
     * 在指定文件中保存布尔值
     * @param fileName 文件名
     * @param key    key
     * @param value  value
     */
    public void putBoolean(String fileName, @NonNull String key,boolean value) {
        MMKV editor = getMMkVByFileName(fileName);
        editor.encode(key, value);
    }

    /**
     * 获取布尔值
     * @param key  key
     * @return
     */
    public boolean getBoolean(@NonNull String key) {
        return this.getBoolean("", key, false);
    }

    public boolean getBoolean(@NonNull String key, boolean defaultValue) {
        return this.getBoolean("", key, defaultValue);
    }

    public boolean getBooleanByOtherFile(String fileName, @NonNull String key) {
        return this.getBoolean(fileName, key, false);
    }

    /**
     * 获取布尔值
     *
     * @param fileName     文件名
     * @param key          key值
     * @param defaultValue 默认值
     * @return 获取到的值
     */
    public boolean getBoolean(String fileName, @NonNull String key, boolean defaultValue) {
        MMKV editor = getMMkVByFileName(fileName);
        return editor.decodeBool(key, defaultValue);
    }


    /**
     * 保存String值的
     *
     * @param key   key
     * @param value value
     */
    public void putString(String key, String value) {
        this.putString("", key, value);
    }

    public void putString(String fileName, String key, String value) {
        MMKV editor = getMMkVByFileName(fileName);
        editor.encode(key, value);
    }

    /**
     * 获取String值的
     *
     * @param key  key
     * @return  获取到的string值
     */
    public String getString(@NonNull String key) {
        return this.getString("", key, "");
    }

    public String getString(@NonNull String key, @NonNull String defaultValue) {
        return this.getString("", key, defaultValue);
    }

    public String getStringByOtherFile(String fileName, @NonNull String key) {
        return this.getString(fileName, key, "");
    }

    public String getString(String fileName, String key, @NonNull String defaultValue) {
        MMKV editor = getMMkVByFileName(fileName);
        return editor.decodeString(key, defaultValue);
    }


    /**
     * 保存int值的
     *
     * @param key   key
     * @param value value
     */
    public void putInt(String key, int value) {
        this.putInt("", key, value);
    }

    public void putInt(String fileName, String key, int value) {
        MMKV editor = getMMkVByFileName(fileName);
        editor.encode(key, value);
    }

    /**
     * 获取int值的
     *
     * @param key  key
     * @return  获取到的int值
     */
    public int getInt(@NonNull String key) {
        return this.getInt("", key, 0);
    }

    public int getInt(@NonNull String key, int defaultValue) {
        return this.getInt("", key, defaultValue);
    }

    public int getIntByOtherFile(String fileName, @NonNull String key) {
        return this.getInt(fileName, key, 0);
    }

    public int getInt(String fileName, String key, int defaultValue) {
        MMKV editor = getMMkVByFileName(fileName);
        return editor.decodeInt(key, defaultValue);
    }

    /**
     * 保存long值的
     *
     * @param key   key
     * @param value value
     */
    public void putLong(String key, long value) {
        this.putLong("", key, value);
    }

    public void putLong(String fileName, String key, long value) {
        MMKV editor = getMMkVByFileName(fileName);
        editor.encode(key, value);
    }

    /**
     * 获取long值的
     *
     * @param key  key
     * @return 获取的long值
     */
    public long getLong(@NonNull String key) {
        return this.getLong("", key, 0);
    }

    public long getLong(@NonNull String key, long defaultValue) {
        return this.getLong("", key, defaultValue);
    }

    public long getLongByOtherFile(String fileName, @NonNull String key) {
        return this.getLong(fileName, key, 0);
    }

    public long getLong(String fileName, String key, long defaultValue) {
        MMKV editor = getMMkVByFileName(fileName);
        return editor.decodeLong(key, defaultValue);
    }

    /**
     * 清除文件中相关的配置的
     */
    public void clearFile() {
        this.clearFile("");
    }

    public void clearFile(@NonNull String fileName) {
        MMKV mmkv = getMMkVByFileName(fileName);
        mmkv.clearAll();
    }
}
