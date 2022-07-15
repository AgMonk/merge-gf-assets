package com.gin.mergegfassets.entity;

import com.gin.mergegfassets.utils.FileUtils;
import lombok.Data;

import java.io.File;
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

    List<AssetFile> files;

    public AssetFileGroup(File assetDir , String path) {
        this.path = path;
        this.files = FileUtils.listAllFilesWithTimeCost(new File(assetDir.getPath() + path))
                .stream().map(AssetFile::new).collect(Collectors.toList());
    }
}
