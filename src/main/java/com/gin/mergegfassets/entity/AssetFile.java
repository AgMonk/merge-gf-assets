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
    public static final String ALPHA = "_ALPHA";
    public static final String HD = "_HD";


    public static final String HE_STRING_1 = "\u202A";
    public static final String HE_STRING_2 = "pic_he";
    public static final String HE_STRING_3 = " #";

    public static final String PIC ="PIC_";
    public static final String NPC_1 ="NPC_";
    public static final String NPC_2 ="NPC-";


    File file;

    String parentPath;
    /**
     * 名称
     */
    String name;

    /**
     * 是否为alpha文件
     */
    boolean alpha;
    /**
     * 是否为hd文件
     */
    boolean hd;
    /**
     * 是否为河蟹版本
     */
    boolean he;
    /**
     * 是否为差分立绘
     */
    boolean difference;



    public AssetFile(File file) {
        this.file = file;
        this.parentPath = file.getParentFile().getPath();

        //文件名转换为全大写，移除多余字符
        final String name = file.getName().toUpperCase()
                .replace(PIC,"")
                .replace(NPC_1,"")
                .replace(NPC_2,"")
                ;

        this.hd = name.contains(HD);
        this.he = name.contains(HE_STRING_1) || name.contains(HE_STRING_3) || this.parentPath.contains(HE_STRING_2);
        this.alpha = name.contains(ALPHA);


        //todo 解析文件名
    }
}
