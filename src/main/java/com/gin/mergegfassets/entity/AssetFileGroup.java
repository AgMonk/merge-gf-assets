package com.gin.mergegfassets.entity;

import com.gin.mergegfassets.script.MergeImage;
import com.gin.mergegfassets.utils.FileUtils;
import com.gin.mergegfassets.utils.TimeUtils;
import lombok.Data;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 资源文件组
 * @author : ginstone
 * @version : v1.0.0
 * @since : 2022/7/15 11:52
 **/
@Data
public class AssetFileGroup {
    String path;

    List<AssetFile> rawFiles;
    List<AssetFile> alphaFiles;

    public AssetFileGroup(File assetDir, String path) {
        this.path = path;
        final List<AssetFile> files = FileUtils.listAllFilesWithTimeCost(new File(assetDir.getPath() + path))
                .stream()
                //过滤编队界面图
                .filter(f -> !f.getName().endsWith("_N.png"))
                .filter(f -> !f.getName().endsWith("_Pass.png"))
                //过滤spine图
                .filter(f -> !f.getParentFile().getName().contains("spine"))
                .map(AssetFile::new).collect(Collectors.toList());

        this.rawFiles = files.stream().filter(assetFile -> !assetFile.isAlpha()).collect(Collectors.toList());
        this.alphaFiles = files.stream().filter(AssetFile::isAlpha).collect(Collectors.toList());
    }

    /**
     * 根据精准匹配的结果
     * @param group     文件组
     * @param outputDir 输出目录
     * @param executor  线程池
     */
    public void mergeByMatchPair(File outputDir, ThreadPoolTaskExecutor executor) throws InterruptedException {
        System.out.println("Merge Start: " + this.path);
        final long start = System.currentTimeMillis();
        this.rawFiles
                .stream().limit(30)
                .forEach(rawFile -> {
                    final List<AssetFile> matchedFiles = this.alphaFiles.stream()
                            .filter(f -> rawFile.getParentPath().equals(f.getParentPath()))
                            .filter(rawFile::matchPair)
                            .sorted((a, b) -> {
                                if (a.isHd() && !b.isHd()) {
                                    return -1;
                                }
                                if (!a.isHd() && b.isHd()) {
                                    return 1;
                                }
                                return 0;
                            }).collect(Collectors.toList());
                    if (matchedFiles.size() > 0) {
                        final AssetFile alphaFile = matchedFiles.get(0);
                        final String path = rawFile.getParentPath().substring(rawFile.getParentPath().indexOf(this.path));
                        final File destFile = new File(outputDir.getPath() + path + '/' + rawFile.toFormatName() + rawFile.getExtensions());
                        if (destFile.exists()) {
                            System.out.printf("[warning] File Exists Skipped : %s \n", destFile.getPath());
                        } else {
                            //noinspection ResultOfMethodCallIgnored
                            destFile.getParentFile().mkdirs();
                            executor.execute(() -> {
                                try {
                                    MergeImage.mergeOpenCv(rawFile.getFile(), alphaFile.getFile(), destFile);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        }
                    } else {
                        System.out.printf("[error] can not match alpha : %s \n", rawFile.getFile().getPath());
                    }
                });

        while (executor.getActiveCount() > 0) {
            Thread.sleep(1000);
        }
        System.out.println("--------------------------");
        TimeUtils.printlnTimeCost(start, "Merge Completed ");
    }

}
