package com.gin.mergegfassets.entity;

import com.gin.mergegfassets.script.MergeImage;
import com.gin.mergegfassets.utils.FileUtils;
import com.gin.mergegfassets.utils.IoUtils;
import com.gin.mergegfassets.utils.NumberUtils;
import com.gin.mergegfassets.utils.TimeUtils;
import lombok.Data;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
    public static final String SKIP = "SKIP";
    public static final String COPY = "COPY";
    String path;

    List<AssetFile> rawFiles;
    List<AssetFile> alphaFiles;
    /**
     * 严格匹配的文件
     */
    List<AssetFilePair> matchedPairs = new ArrayList<>();
    /**
     * 相似的文件
     */
    List<AssetFilePair> similarPairs = new ArrayList<>();
    /**
     * 跳过的文件
     */
    List<AssetFile> skippedFiles = new ArrayList<>();
    /**
     * 直接复制的文件
     */
    List<AssetFile> copyFiles = new ArrayList<>();

    public AssetFileGroup(File assetDir, String path) {
        this.path = path;
        final List<AssetFile> files = FileUtils.listAllFilesWithTimeCost(new File(assetDir.getPath() + path))
                .stream()
                //过滤编队界面图
                .filter(f -> !f.getName().endsWith("_N.png"))
                .filter(f -> !f.getName().contains("_N_"))
                .filter(f -> !f.getName().endsWith("_Pass.png"))
                //过滤spine图
                .filter(f -> !f.getPath().contains("spine"))
                .filter(f -> !f.getPath().contains("commander"))
                .map(AssetFile::new).collect(Collectors.toList());

        this.rawFiles = files.stream().filter(assetFile -> !assetFile.isAlpha()).collect(Collectors.toList());
        this.alphaFiles = files.stream().filter(AssetFile::isAlpha).collect(Collectors.toList());


    }

    private void match(File outputDir, Dictionary dictionary) {
        for (AssetFile rawFile : this.rawFiles) {
            //目标文件路径
            final File destFile = getDestFile(outputDir, rawFile);
            if (destFile.exists()) {
                //目标文件已存在:跳过
                skippedFiles.add(rawFile);
                continue;
            } else {
                //目标文件不存在：创建目录
                //noinspection ResultOfMethodCallIgnored
                destFile.getParentFile().mkdirs();
            }
            //查询字典看是否有匹配
            final String relativePath = rawFile.getRelativePath();
            if (dictionary.hasKey(relativePath)) {
                final String value = dictionary.get(relativePath);
                if (COPY.equalsIgnoreCase(value)) {
                    this.copyFiles.add(rawFile);
                    continue;
                }
                if (SKIP.equalsIgnoreCase(value)) {
                    this.skippedFiles.add(rawFile);
                    continue;
                }
                //找到匹配，使用匹配结果
                final List<AssetFile> alphaFileFromDic = this.alphaFiles.stream().filter(f -> f.getRelativePath().equals(value)).collect(Collectors.toList());
                this.matchedPairs.add(new AssetFilePair(rawFile, alphaFileFromDic));
                continue;
            }

            //相似文件
            final List<AssetFile> similarFiles = this.alphaFiles.stream().filter(rawFile::similar).collect(Collectors.toList());
            //匹配文件
            final List<AssetFile> matchedFiles = similarFiles.stream()
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
                    }).limit(1).collect(Collectors.toList());
            if (matchedFiles.size() > 0) {
                this.matchedPairs.add(new AssetFilePair(rawFile, matchedFiles));
            } else {
                this.similarPairs.add(new AssetFilePair(rawFile, similarFiles));
            }
        }
        System.out.printf("匹配完成 跳过: %d ,复制: %d ,匹配: %d ,相似: %d\n", skippedFiles.size(), copyFiles.size(), matchedPairs.size(),similarPairs.size());
    }

    /**
     * 获取目标文件
     * @param outputDir 输出目录
     * @param rawFile   原文件
     * @return 目标文件
     */
    private File getDestFile(File outputDir, AssetFile rawFile) {
        final String destPath = rawFile.getParentPath().substring(rawFile.getParentPath().indexOf(this.path));
        return new File(outputDir.getPath() + destPath + '/' + rawFile.toFilename());
    }

    /**
     * 根据精准匹配的结果
     * @param outputDir  输出目录
     * @param executor   线程池
     * @param limit      合并的数量
     * @param dictionary 字典
     */
    public void mergeByMatchPair(File outputDir, ThreadPoolTaskExecutor executor, Integer limit, Dictionary dictionary)
            throws InterruptedException, IOException {
        System.out.println("--------------------------");
        System.out.println("合并开始: " + this.path);
        final long start = System.currentTimeMillis();

        match(outputDir, dictionary);

        // 为每一个相似的文件指定匹配的 Alpha 文件
        if (this.similarPairs.size() > 0) {
            // todo
            for (AssetFilePair similarPair : this.similarPairs) {
                final AssetFile rawFile = similarPair.getRawFile();
                final List<AssetFile> similarAlphaFiles = similarPair.getAlphaFiles();
                //打印原文件情况 和 相似 alpha文件情况
                System.out.printf("原文件: %s 路径: %s \n", rawFile.toFormatName(), rawFile.getFile().getPath());
                final int similarSize = similarAlphaFiles.size();
                for (int i = 0; i < similarSize; i++) {
                    final AssetFile saf = similarAlphaFiles.get(i);
                    System.out.printf("\t[%d] Alpha文件: %s 路径: %s \n", i, saf.toFormatName(), saf.getFile().getPath());
                }
                //打开文件夹
                final List<String> dirs = similarAlphaFiles.stream().map(f -> f.getFile().getParentFile().getPath()).distinct().collect(Collectors.toList());
                final Desktop desktop = Desktop.getDesktop();
                if (dirs.size() > 0) {
                    dirs.forEach(f -> {
                        try {
                            desktop.open(new File(f));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else {
                    desktop.open(rawFile.getFile().getParentFile());
                }

                //提示文字
                StringBuilder sb = new StringBuilder();
                sb.append("请输入指令：\n");
                if (similarSize>0){
                    sb.append("\t");
                    sb.append(String.format("序号 [0~%d]：从上述列出的Alpha文件中选定一个与该文件匹配\n",similarSize-1));
                }
                sb.append("\t绝对路径 ：给定一个Alpha文件的绝对路径与该文件匹配，必须处在该文件夹内：").append(outputDir.getPath()).append(this.path).append("\n");
                sb.append("\tcopy ：表示该文件不需要合并，直接复制\n");
                sb.append("\tskip ：表示该文件不需要任何处理\n");

                //用户输入
                final String command = IoUtils.readCommand(sb.toString(), (line) -> {
                    if (NumberUtils.isInt(line)) {
                        final int i = Integer.parseInt(line);
                        return i >= 0 && i < similarSize;
                    } else if (SKIP.equalsIgnoreCase(line) || COPY.equalsIgnoreCase(line)) {
                        return true;
                    } else {
                        return this.alphaFiles.stream().anyMatch(f -> f.getFile().getPath().equals(line));
                    }
                });
                if (SKIP.equalsIgnoreCase(command)) {
                    // 如果输入的是skip 跳过该文件 ，记录到字典
                    dictionary.put(rawFile.getRelativePath(), SKIP);
                    dictionary.save();
                } else if (COPY.equalsIgnoreCase(command)) {
                    // 如果输入的是copy 跳过该文件 ，记录到字典
                    dictionary.put(rawFile.getRelativePath(), COPY);
                    dictionary.save();
                } else {
                    // 如果输入的是数字 ，直接取该文件
                    @SuppressWarnings("OptionalGetWithoutIsPresent") final AssetFile alphaFile = NumberUtils.isInt(command) ? similarAlphaFiles.get(Integer.parseInt(command))
                            // 如果输入的是路径 ，从总表里找到这个文件
                            : (this.alphaFiles.stream().filter(f -> f.getFile().getPath().equals(command)).findFirst().get());
                    //保存到字典
                    dictionary.put(rawFile.getRelativePath(), alphaFile.getRelativePath());
                    dictionary.save();
                    //添加到匹配列表
                    this.matchedPairs.add(new AssetFilePair(rawFile, Collections.singletonList(alphaFile)));
                }
            }
        }

        //todo 复制复制列表的文件


        // 开始合并匹配完成的文件
        this.matchedPairs.stream().limit(limit == null ? this.matchedPairs.size() : limit).forEach(pair -> {
            final AssetFile rawFile = pair.getRawFile();
            final AssetFile alphaFile = pair.getAlphaFiles().get(0);
            final File destFile = getDestFile(outputDir, rawFile);
            executor.execute(() -> {
                try {
                    MergeImage.mergeOpenCv(rawFile.getFile(), alphaFile.getFile(), destFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        });



        while (executor.getActiveCount() > 0) {
            //noinspection BusyWait
            Thread.sleep(1000);
        }
        System.out.println("--------------------------");
        TimeUtils.printlnTimeCost(start, "合并完成 ");
    }

}
