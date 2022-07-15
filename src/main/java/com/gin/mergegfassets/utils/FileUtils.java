package com.gin.mergegfassets.utils;

import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 文件操作工具类
 * @author : ginstone
 * @version : v1.0.0
 * @since : 2022/7/8 14:46
 **/
public class FileUtils {
    /**
     * 移动文件到一个指定目录
     * @param file 文件
     * @param dir  目录
     */
    public static void moveToDir(File file, File dir) {
        final String destPath = dir.getPath() + "/" + file.getName();
        final File dest = new File(destPath);
        move(file, dest);
    }

    public static void move(File source, File dest) {
        final File dir = dest.getParentFile();
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("目录创建失败: " + dir.getPath());
        }
        if (dest.exists()) {
            throw new RuntimeException("文件已存在: " + dest.getPath());
        } else {
            if (source.renameTo(dest)) {
                System.out.println(source.getName() + " 移动到 -> " + dest.getPath());
            }
        }
    }

    /**
     * 返回字符串是否包含任一一个关键字
     * @param s        字符串
     * @param keywords 关键字
     * @return 是否
     */
    public static boolean hasKeywords(String s, String... keywords) {
        for (String keyword : keywords) {
            if (s.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 删除文件列表中，文件名包含任一关键字的文件
     * @param files    文件列表
     * @param keywords 关键字
     */
    public static void deleteFileWithKeyword(List<File> files, String... keywords) {
        files.stream().filter(f -> hasKeywords(f.getName(), keywords)).forEach(file -> {
            if (file.delete()) {
                System.out.println("删除文件： " + file.getPath());
            }
        });
        files.removeIf(f -> hasKeywords(f.getName(), keywords));
    }

    /**
     * 列出一个目录下的文件
     * @param dir 目录
     * @return 文件
     */
    public static List<File> listFiles(@NonNull File dir) {
        return new ArrayList<>(List.of(Objects.requireNonNull(dir.listFiles())));
    }


    /**
     * 递归删除一个目录及其下所有文件
     * @param dir 目录
     */
    public static void removeDir(@NonNull File dir) {
        if (!dir.exists()) {
            return;
        }
        final List<File> files = listFiles(dir);
        for (File file : files) {
            if (file.isDirectory()) {
                removeDir(file);
            } else {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
        }
        //noinspection ResultOfMethodCallIgnored
        dir.delete();
    }

    public static void copyToDir(File source, File dir) throws IOException {
        final String destPath = dir.getPath() + "/" + source.getName();
        final File dest = new File(destPath);
        copyFile(source, dest);
    }

    public static void copyFile(File source, File dest) throws IOException {
        final File dir = dest.getParentFile();
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("目录创建失败: " + dir.getPath());
        }
        if (dest.exists()) {
            throw new RuntimeException("文件已存在: " + dest.getPath());
        } else {
            Files.copy(source.toPath(), dest.toPath());
            System.out.println(source.getName() + " 复制到 -> " + dest.getPath());
        }
    }

    public static List<File> listAllFiles(File dir){
        final ArrayList<File> list = new ArrayList<>();
        if (dir==null || !dir.exists()) {
            return list;
        }
        final File[] files = dir.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isDirectory()) {
                list.addAll(listAllFiles(file));
            }else{
                list.add(file);
            }
        }
        return list;
    }
}
