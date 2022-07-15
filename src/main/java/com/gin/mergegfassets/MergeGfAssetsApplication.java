package com.gin.mergegfassets;

import com.gin.mergegfassets.entity.AssetFileGroup;
import com.gin.mergegfassets.entity.Dictionary;
import com.gin.mergegfassets.script.MergeImage;
import com.gin.mergegfassets.utils.TasksUtil;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.File;
import java.io.IOException;

/**
 * @author bx002
 */
@SpringBootApplication
public class MergeGfAssetsApplication {

    public static void main(String[] args) throws IOException, InterruptedException {
//        SpringApplication.run(MergeGfAssetsApplication.class, args);

        //加载 dll
        MergeImage.init();

        //加载字典
        final Dictionary dictionary = new Dictionary(new File(System.getProperty("user.dir") + "/Dic.json"));

        //指定 assets文件夹路径
        File assetDir = new File("F:\\Texture2D\\assets");
//        File assetDir = IoUtils.readAssetPath();
        //指定 输出文件夹路径
        File outputDir = new File("F:\\Texture2D\\assets\\output");
//        File outputDir = IoUtils.readOutputPath();

        final int coreSize = 10;
//        final int coreSize = IoUtils.readNumber("Threads:");
        final ThreadPoolTaskExecutor executor = TasksUtil.getExecutor("Merge", coreSize);
        //扫描assets文件夹

        //标清立绘 + 差分等其他文件
        final AssetFileGroup charFiles = new AssetFileGroup(assetDir, "\\characters");
        //高清立绘
        final AssetFileGroup gunFiles = new AssetFileGroup(assetDir, "\\resources\\dabao\\pics\\guns");
        //妖精立绘
        final AssetFileGroup fairyFiles = new AssetFileGroup(assetDir, "\\resources\\dabao\\pics\\fairy");

        gunFiles.mergeByMatchPair(outputDir,executor);


        executor.shutdown();
        executor.destroy();
        // todo 配对失败时从总文件列表中查找可能的备选项，复制到临时文件夹中供选择；选定后添加到字典中保存
        //
    }

}
