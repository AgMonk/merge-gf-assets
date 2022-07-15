package com.gin.mergegfassets.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author : ginstone
 * @version : v1.0.0
 * @since : 2022/7/15 11:15
 **/
public class IoUtils {

    public static File readAssetPath() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        File assetDir = null;
        while (assetDir == null || !assetDir.exists()) {
            System.out.print("AssetPath : ");
            assetDir = new File(reader.readLine());
        }
        return assetDir;
    }
}
