package com.gin.mergegfassets;

import com.gin.mergegfassets.entity.AssetFileGroup;
import com.gin.mergegfassets.entity.Dictionary;
import com.gin.mergegfassets.script.MergeImage;
import com.gin.mergegfassets.utils.FileUtils;
import com.gin.mergegfassets.utils.IoUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author bx002
 */
@SpringBootApplication
public class MergeGfAssetsApplication {

    public static void main(String[] args) throws IOException {
//        SpringApplication.run(MergeGfAssetsApplication.class, args);

        //加载 dll
        MergeImage.init();

        //加载字典
        final Dictionary dictionary = new Dictionary(new File(System.getProperty("user.dir") + "/Dic.json"));

        //指定 assets文件夹路径
        File assetDir = IoUtils.readAssetPath();
        //指定 输出文件夹路径
        File outputDir = IoUtils.readOutputPath();

        //扫描assets文件夹

        //标清立绘 + 差分等其他文件
        final AssetFileGroup charFiles = new AssetFileGroup(assetDir, "/characters");
        //高清立绘
        final AssetFileGroup gunFiles = new AssetFileGroup(assetDir, "/resources/dabao/pics/guns");
        final AssetFileGroup fairyFiles = new AssetFileGroup(assetDir, "/resources/dabao/pics/fairy");

        // todo 解析图片文件
        // todo 尝试将原文件 与 alpha文件配对
        // todo 配对失败时从总文件列表中查找可能的备选项，复制到临时文件夹中供选择；选定后添加到字典中保存
        //
    }

}
