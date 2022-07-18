package com.gin.mergegfassets;

import com.gin.mergegfassets.entity.AssetFileGroup;
import com.gin.mergegfassets.entity.Config;
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
        final Dictionary dictionary = new Dictionary();
        //加载运行配置
        final Config config = Config.init();
        //指定 assets文件夹路径
        File assetDir = new File(config.getAssetPath());
        //指定 输出文件夹路径
        File outputDir = new File(config.getOutputPath());
        final ThreadPoolTaskExecutor executor = TasksUtil.getExecutor("线程", config.getThreads());

        //扫描assets文件夹

        //标清立绘 + 差分等其他文件
        final AssetFileGroup charFiles = new AssetFileGroup(assetDir, "\\characters", outputDir, dictionary, executor);
        //高清立绘
        final AssetFileGroup gunFiles = new AssetFileGroup(assetDir, "\\resources\\dabao\\pics\\guns", outputDir, dictionary, executor);
        //妖精立绘
        final AssetFileGroup fairyFiles = new AssetFileGroup(assetDir, "\\resources\\dabao\\pics\\fairy", outputDir, dictionary, executor);

//        gunFiles.mergeByMatchPair(outputDir,executor, 5,dictionary);
        charFiles.merge(null);


        executor.shutdown();
        executor.destroy();
    }

}
