package com.gin.mergegfassets.utils;

import java.io.IOException;

/**
 * @author : ginstone
 * @version : v1.0.0
 * @since : 2022/7/18 14:03
 **/
public class CmdUtils {
    public static Runtime getRuntime() {
        return Runtime.getRuntime();
    }

    public static void explorerSelect(String path) throws IOException {
        getRuntime().exec("Explorer.exe /e,/select," + path);
    }
}
