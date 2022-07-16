package com.gin.mergegfassets.entity;

import com.alibaba.fastjson2.JSONObject;
import com.gin.mergegfassets.utils.IoUtils;
import com.gin.mergegfassets.utils.JsonUtils;
import lombok.Data;

import java.io.File;
import java.io.IOException;

/**
 * 运行配置
 * @author : ginstone
 * @version : v1.0.0
 * @since : 2022/7/15 17:32
 **/
@Data
public class Config {
    String assetPath;
    String outputPath;
    Integer threads;

    public static Config init() throws IOException {
        final File file = new File(System.getProperty("user.dir") + "/config.json");
        if (file.exists()) {
            final String res = JsonUtils.readFromFile(file);
            return JSONObject.parseObject(res, Config.class);
        } else {
            final Config config = new Config();
            config.setAssetPath(IoUtils.readAssetPath());
            config.setOutputPath(IoUtils.readOutputPath());
            config.setThreads(IoUtils.readNumber("请提供使用的线程数量 "));
            JsonUtils.writeToFile(file,config);
            return config;
        }
    }
}
