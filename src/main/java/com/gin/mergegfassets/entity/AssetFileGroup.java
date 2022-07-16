package com.gin.mergegfassets.entity;

import com.gin.mergegfassets.utils.FileUtils;
import com.gin.mergegfassets.utils.TimeUtils;
import lombok.Data;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.File;
import java.util.ArrayList;
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

    public AssetFileGroup(File assetDir, String path) {
        this.path = path;
        final List<AssetFile> files = FileUtils.listAllFilesWithTimeCost(new File(assetDir.getPath() + path))
                .stream()
                //过滤编队界面图
                .filter(f -> !f.getName().endsWith("_N.png"))
                .filter(f -> !f.getName().contains("_N_"))
                .filter(f -> !f.getName().endsWith("_Pass.png"))
                //过滤spine图
                .filter(f -> !f.getParentFile().getName().contains("spine"))
                .map(AssetFile::new).collect(Collectors.toList());

        this.rawFiles = files.stream().filter(assetFile -> !assetFile.isAlpha()).collect(Collectors.toList());
        this.alphaFiles = files.stream().filter(AssetFile::isAlpha).collect(Collectors.toList());


    }

    private void match(File outputDir, Dictionary dictionary){
        for (AssetFile rawFile : this.rawFiles) {
            //目标文件路径
            final String destPath = rawFile.getParentPath().substring(rawFile.getParentPath().indexOf(this.path));
            final File destFile = new File(outputDir.getPath() + destPath + '/' + rawFile.toFilename());
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
                //找到匹配，使用匹配结果
                final List<AssetFile> alphaFileFromDic = this.alphaFiles.stream().filter(f -> f.getRelativePath().equals(dictionary.get(relativePath))).collect(Collectors.toList());
                this.matchedPairs.add(new AssetFilePair(rawFile,alphaFileFromDic));
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
                    }).collect(Collectors.toList());
            if (matchedFiles.size() > 0) {
                this.matchedPairs.add(new AssetFilePair(rawFile,matchedFiles));
            } else{
                this.similarPairs.add(new AssetFilePair(rawFile,similarFiles));
            }
        }
        System.out.printf("匹配完成 跳过: %d ,匹配: %d ,相似: %d \n",skippedFiles.size(),matchedPairs.size(),similarPairs.size());
    }

    /**
     * 根据精准匹配的结果
     * @param outputDir  输出目录
     * @param executor   线程池
     * @param limit      合并的数量
     * @param dictionary 字典
     */
    public void mergeByMatchPair(File outputDir, ThreadPoolTaskExecutor executor, Integer limit, Dictionary dictionary) throws InterruptedException {
        System.out.println("--------------------------");
        System.out.println("合并开始: " + this.path);
        final long start = System.currentTimeMillis();

        match(outputDir, dictionary);

//        this.rawFiles
//                .stream().limit(limit == null ? this.rawFiles.size() : limit)
//                .forEach(rawFile -> {
//                    //指定目标文件 创建目录
//                    final String path = rawFile.getParentPath().substring(rawFile.getParentPath().indexOf(this.path));
//                    final File destFile = new File(outputDir.getPath() + path + '/' + rawFile.toFilename());
//                    if (destFile.exists()) {
//                        //目标文件已存在:跳过
//                        System.out.printf("[INFO] File Exists Skipped : %s \n", destFile.getPath());
//                        return;
//                    } else {
//                        //noinspection ResultOfMethodCallIgnored
//                        destFile.getParentFile().mkdirs();
//                    }
//                    //相似文件
//                    final List<AssetFile> similarFiles = this.alphaFiles.stream().filter(AssetFile::isAlpha).filter(rawFile::similar).collect(Collectors.toList());
//                    //筛选、排序匹配的alpha文件
//                    final List<AssetFile> matchedFiles = similarFiles.stream()
//                            .filter(f -> rawFile.getParentPath().equals(f.getParentPath()))
//                            .filter(rawFile::matchPair)
//                            .sorted((a, b) -> {
//                                if (a.isHd() && !b.isHd()) {
//                                    return -1;
//                                }
//                                if (!a.isHd() && b.isHd()) {
//                                    return 1;
//                                }
//                                return 0;
//                            }).collect(Collectors.toList());
//                    if (matchedFiles.size() > 0) {
//                        //精准匹配到 alpha文件
//                        //匹配信息
////                        final String m = matchedFiles.stream().map(f -> String.format("%s -> %s", f.toFormatName(), f.getFile().getName())).collect(Collectors.joining(" | "));
////                        System.out.printf("%s -> %s match [%s] \n" ,rawFile.toFormatName(), rawFile.getFile().getName(),m);
//
//
//                        //找到精准匹配的alpha文件
//                        final AssetFile alphaFile = matchedFiles.get(0);
//                        executor.execute(() -> {
//                            try {
//                                MergeImage.mergeOpenCv(rawFile.getFile(), alphaFile.getFile(), destFile);
//                            } catch (IOException e) {
//                                throw new RuntimeException(e);
//                            }
//                        });
//                    } else {
//                        final int similarFileSize = similarFiles.size();
//                        if (similarFileSize > 0) {
//                            System.out.printf("[WARN] Found %d similar Alpha Files: %s \n", similarFileSize, rawFile.getFile().getPath());
//                            //发现并列出相似的文件
//                            for (int i = 0; i < similarFileSize; i++) {
//                                final AssetFile assetFile = similarFiles.get(i);
//                                System.out.printf("\t %d : %s -> %s \n", i, assetFile.toFormatName(), assetFile.getFile().getPath());
//                            }
//                            // 打开对应文件夹 确认选择
//                            final Desktop desktop = Desktop.getDesktop();
//                            similarFiles.stream().map(AssetFile::getParentPath).distinct().forEach(f -> {
//                                try {
//                                    desktop.open(new File(f));
//                                } catch (IOException e) {
//                                    throw new RuntimeException(e);
//                                }
//                            });
//                            // 选择一个序号 或者 提供一个地址
//                            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//                            AssetFile alphaFile = null;
//                            String line = null;
//                            while (alphaFile == null) {
//                                System.out.printf("Chose An Index [0~%d] Or A Path >>", similarFileSize);
//                                try {
//                                    line = reader.readLine();
//                                } catch (IOException e) {
//                                    throw new RuntimeException(e);
//                                }
//                                final Matcher matcher = AssetFile.NUMBER.matcher(line);
//                                if (matcher.find()) {
//                                    final int index = Integer.parseInt(matcher.group());
//                                    if (index >= 0 && index < similarFileSize) {
//                                        alphaFile = similarFiles.get(index);
//                                    }
//                                } else {
//                                    String finalLine = line;
//                                    final List<AssetFile> list = this.alphaFiles.stream().filter(f -> f.getFile().getPath().equals(finalLine)).collect(Collectors.toList());
//                                    if (list.size() == 1) {
//                                        alphaFile = list.get(0);
//                                    }
//                                }
//                            }
//                            // 用户已指定使用的 alpha 文件
//
//
//                        } else {
//                            System.out.printf("[ERROR] Can Not Match Alpha File: %s \n", rawFile.getFile().getPath());
//                        }
//                    }
//                });

        while (executor.getActiveCount() > 0) {
            //noinspection BusyWait
            Thread.sleep(1000);
        }
        System.out.println("--------------------------");
        TimeUtils.printlnTimeCost(start, "合并完成 ");
    }

}
