package com.gin.mergegfassets.utils;

/**
 * @author : ginstone
 * @version : v1.0.0
 * @since : 2022/7/11 14:38
 **/
public class TimeUtils {

    public static void printlnTimeCost(long start) {
        final long end = System.currentTimeMillis();
        final long range = (end - start) / 100;
        final double time = 1.0 * range;

        System.out.println("time cost： " + time / 10 + "s");
    }
}
