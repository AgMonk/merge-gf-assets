package com.gin.mergegfassets.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : ginstone
 * @version : v1.0.0
 * @since : 2022/7/15 11:15
 **/
public class IoUtils {
    public static final Pattern NUMBER = Pattern.compile("^\\d+$");

    public static String readAssetPath() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        File assetDir = null;
        while (assetDir == null || !assetDir.exists()) {
            System.out.print("请提供assets目录路径 >> ");
            assetDir = new File(reader.readLine());
        }
        return assetDir.getPath();
    }

    public static String readOutputPath() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        File output = null;
        while (output == null || !output.exists()) {
            System.out.print("请提供输出目录路径 >> ");
            output = new File(reader.readLine());
        }
        //noinspection ResultOfMethodCallIgnored
        output.mkdirs();
        return output.getPath();
    }

    public static int readNumber(String prefix) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = null;
        boolean isNumber = false;
        while (line == null || !isNumber) {
            System.out.print(prefix + " >> ");
            line = reader.readLine();
            final Matcher matcher = NUMBER.matcher(line);
            isNumber = matcher.find();
        }
        return Integer.parseInt(line);
    }

}
