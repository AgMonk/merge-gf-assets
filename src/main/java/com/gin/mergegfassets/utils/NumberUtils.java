package com.gin.mergegfassets.utils;

import java.util.regex.Pattern;

/**
 * 数字工具类
 * @author : ginstone
 * @version : v1.0.0
 * @since : 2022/7/16 17:26
 **/
public class NumberUtils {
    public static final Pattern NUMBER = Pattern.compile("^\\d+$");

    public static boolean isInt(String s){
        if (s==null ||"".equals(s)) {
            return false;
        }
        return NUMBER.matcher(s).find();
    }
}
