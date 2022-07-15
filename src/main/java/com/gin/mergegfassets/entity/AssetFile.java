package com.gin.mergegfassets.entity;

import lombok.Data;

import java.io.File;

/**
 * 资源文件
 * @author : ginstone
 * @version : v1.0.0
 * @since : 2022/7/15 11:52
 **/
@Data
public class AssetFile {
    File file;

    public AssetFile(File file) {
        this.file = file;

        //todo 解析文件名
    }
}
