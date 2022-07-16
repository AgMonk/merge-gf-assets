package com.gin.mergegfassets.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.gin.mergegfassets.utils.JsonUtils;

import java.io.File;
import java.io.IOException;
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

    TreeMap<String, String> data = new TreeMap<>();

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

    public Dictionary() throws IOException {
        this.file = new File(System.getProperty("user.dir") + "/dic.json");
        if (this.file.exists()) {
            System.out.println("Loading Dictionary " + this.file.getPath());
            final String res = JsonUtils.readFromFile(this.file);
            this.data = JSONObject.parseObject(res).to(new TypeReference<TreeMap<String, String>>() {
            });
        }else{
            System.out.println("创建字典 " + this.file.getPath());
        }
    }

    public void save() throws IOException {
        JsonUtils.writeToFile(this.file, this.data);
    }
}
