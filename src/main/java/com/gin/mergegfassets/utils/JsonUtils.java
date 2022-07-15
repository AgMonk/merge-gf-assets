package com.gin.mergegfassets.utils;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.TypeReference;
import com.gin.mergegfassets.entity.Dictionary;

import java.io.*;
import java.util.LinkedHashMap;

/**
 * json工具类
 * @author : ginstone
 * @version : v1.0.0
 * @since : 2022/7/15 09:58
 **/
public class JsonUtils {

    public static void writeToFile(File file, Object obj) throws IOException {
        FileWriter writer = new FileWriter(file);
        writer.write(JSONObject.toJSONString(obj, JSONWriter.Feature.PrettyFormat));
        writer.close();
    }

    public static String readFromFile(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();
        while (line!=null){
            sb.append(line);
            line = br.readLine();
        }
        br.close();
        return sb.toString();
    }
}
