package com.gin.mergegfassets.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.gin.mergegfassets.utils.JsonUtils;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.TreeMap;

/**
 * 字典，保存原文件对Alpha文件的映射
 * @author : ginstone
 * @version : v1.0.0
 * @since : 2022/7/15 10:41
 **/
public class Dictionary {
    /**
     * 标记，表示该文件不需要合并，直接复制
     */
    public static final String METHOD_COPY = "COPY";

    TreeMap<String, String> data;

    File file;

    public boolean hasKey(String key) {
        return this.data.containsKey(key);
    }

    public String get(String key) {
        return this.data.get(key);
    }

    public void put(String key, String value) {
        this.data.put(key, value);
    }

    public Dictionary(File file) throws IOException {
        this.file = file;
        if (file.exists()) {
            System.out.println("Loading Dictionary " + file.getPath());
            final String res = JsonUtils.readFromFile(file);
            this.data = JSONObject.parseObject(res).to(new TypeReference<TreeMap<String, String>>() {
            });
        }else{
            System.out.println("Created Dictionary " + file.getPath());
        }
    }

    public void save() throws IOException {
        JsonUtils.writeToFile(this.file, this.data);
    }
}
