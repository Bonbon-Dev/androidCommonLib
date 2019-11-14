package com.bbt.commonlib.toolutil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.Reader;
import java.lang.reflect.Type;


/**
 * @author lixiaonan
 * 功能描述: gson相关的工具类的
 * 时 间： 2019-11-08 17:38
 */
public final class GsonUtils {

    private static final Gson GSON = createGson(true);

    private static final Gson GSON_NO_NULLS = createGson(false);

    private GsonUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 获取 Gson 对象 默认空值序列化的
     *
     * @return
     */
    public static Gson getGson() {
        return getGson(true);
    }

    /**
     * Gets pre-configured {@link Gson} instance.
     *
     * @param serializeNulls Determines if nulls will be serialized.
     * @return {@link Gson} instance.
     */
    public static Gson getGson(final boolean serializeNulls) {
        return serializeNulls ? GSON_NO_NULLS : GSON;
    }

    /**
     * 对象转 Json 串
     *
     * @param object The object to serialize.
     * @return object serialized into json.
     */
    public static String toJson(final Object object) {
        return toJson(object, true);
    }

    /**
     * Serializes an object into json.
     *
     * @param object       The object to serialize.
     * @param includeNulls Determines if nulls will be included.
     * @return object serialized into json.
     */
    public static String toJson(final Object object, final boolean includeNulls) {
        return includeNulls ? GSON.toJson(object) : GSON_NO_NULLS.toJson(object);
    }

    /**
     * Serializes an object into json.
     *
     * @param src       The object to serialize.
     * @param typeOfSrc The specific genericized type of src.
     * @return object serialized into json.
     */
    public static String toJson(final Object src, final Type typeOfSrc) {
        return toJson(src, typeOfSrc, true);
    }

    /**
     * Serializes an object into json.
     *
     * @param src          The object to serialize.
     * @param typeOfSrc    The specific genericized type of src.
     * @param includeNulls Determines if nulls will be included.
     * @return object serialized into json.
     */
    public static String toJson(final Object src, final Type typeOfSrc, final boolean includeNulls) {
        return includeNulls ? GSON.toJson(src, typeOfSrc) : GSON_NO_NULLS.toJson(src, typeOfSrc);
    }


    /**
     * Json 串转对象
     *
     * @param json The json to convert.
     * @param type Type json will be converted to.
     * @return instance of type
     */
    public static <T> T fromJson(final String json, final Class<T> type) {
        try {
            return GSON.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Converts {@link String} to given type.
     *
     * @param json the json to convert.
     * @param type type type json will be converted to.
     * @return instance of type
     */
    public static <T> T fromJson(final String json, final Type type) {
        try {
            return GSON.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Converts {@link Reader} to given type.
     *
     * @param reader the reader to convert.
     * @param type   type type json will be converted to.
     * @return instance of type
     */
    public static <T> T fromJson(final Reader reader, final Class<T> type) {
        try {
            return GSON.fromJson(reader, type);
        } catch (JsonSyntaxException | JsonIOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Converts {@link Reader} to given type.
     *
     * @param reader the reader to convert.
     * @param type   type type json will be converted to.
     * @return instance of type
     */
    public static <T> T fromJson(final Reader reader, final Type type) {
        try {
            return GSON.fromJson(reader, type);
        } catch (JsonIOException | JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Create a pre-configured {@link Gson} instance.
     *
     * @param serializeNulls determines if nulls will be serialized.
     * @return {@link Gson} instance.
     */
    private static Gson createGson(final boolean serializeNulls) {
        final GsonBuilder builder = new GsonBuilder();
        if (serializeNulls) {
            builder.serializeNulls();
        }
        return builder.create();
    }
}
