package com.bbt.commonlib.toolutil;

/**
  *  @author lixiaonan
  *  功能描述: String处理的工具类的
  *  时 间： 2019-11-02 17:07
  */
public class StringUtil {
    /**
     * 判断一个字符串是否为空
     * @param s  入参
     * @return   为null 或 '' 为true
     */
    public static boolean isEmpty(final CharSequence s) {
        return s == null || s.length() == 0;
    }

    /**
     * 判断一个String字符串 trim之后 是否为空的
     * @param str
     * @return
     */
    public static boolean isNotEmptyString(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        str = str.trim();
        return str.length() > 0;
    }
    /**
     * 判断一个字符串不为空
     * @param s
     * @return 不为空为true
     */
    public static boolean isNotEmpty(final CharSequence s) {
        return !(isNotEmpty(s));
    }

    /**
     * 比较2个字符串是否相等的
     * @param s1  入参1
     * @param s2  入参2
     * @return   相等返回true
     */
    public static boolean equals(final CharSequence s1, final CharSequence s2) {
        if (s1 == s2){
            return true;
        }
        int length;
        if (s1 != null && s2 != null && (length = s1.length()) == s2.length()) {
            if (s1 instanceof String && s2 instanceof String) {
                return s1.equals(s2);
            } else {
                for (int i = 0; i < length; i++) {
                    if (s1.charAt(i) != s2.charAt(i)){
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * String中的字母变小写的
     * @param string 入参字母
     * @return  变换完成的
     */
    public static String toLowerString(String string){
        if(isEmpty(string)){
            return "";
        }
        return string.toLowerCase();
    }
}
