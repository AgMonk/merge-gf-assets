package com.gin.mergegfassets.entity;

import lombok.Data;

import java.util.List;

/**
 * 匹配结果
 * @author : ginstone
 * @version : v1.0.0
 * @since : 2022/7/16 16:13
 **/
@Data
public class AssetFilePair {
    AssetFile rawFile;
    List<AssetFile> alphaFiles;

    public AssetFilePair(AssetFile rawFile, List<AssetFile> alphaFiles) {
        this.rawFile = rawFile;
        this.alphaFiles = alphaFiles;
    }
}
