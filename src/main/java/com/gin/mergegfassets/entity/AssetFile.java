package com.gin.mergegfassets.entity;

import lombok.Data;

import java.io.File;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 资源文件
 * @author : ginstone
 * @version : v1.0.0
 * @since : 2022/7/15 11:52
 **/
@Data
public class AssetFile {
    public static final Pattern PATTERN_1 = Pattern.compile("^(.+?)\\((\\d+)\\)$");
    public static final Pattern PATTERN_2 = Pattern.compile("^(.+?)_(\\d+)$");
    public static final String ALPHA = "_ALPHA";
    public static final String HD = "_HD";
    public static final String HE = "_HE";
    public static final String DIFF = "_DIFF";
    public static final String DAMAGED = "_DAMAGED";
    public static final String ASSET = "\\assets";


    public static final String HE_STRING_1 = "\u202A";
    public static final String HE_STRING_2 = "pic_he";
    public static final String HE_STRING_3 = " #";

    public static final String PIC = "PIC_";
    public static final String DAMAGED_1 = "_D_";
    public static final String DAMAGED_2 = "_D.PNG";
    public static final String NPC_1 = "NPC_";
    public static final String NPC_2 = "NPC-";
    public static final String S = "_";


    File file;

    String parentPath;
    /**
     * 名称
     */
    String character;
    /**
     * 版本
     */
    String version;
    /**
     * 后缀
     */
    String extensions;
    /**
     * 是否为alpha文件
     */
    boolean alpha;
    /**
     * 是否为重创立绘
     */
    boolean damaged;
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

    /**
     * 格式化名称 ：角色名 是否差分 版本名 是否重创 是否河蟹 是否高清  是否alpha
     * @return 格式化名称
     */
    public String toFormatName(){

        final StringBuilder sb = new StringBuilder(this.character);
        if (this.difference){
            sb.append(DIFF);
        }
        if (this.version!=null){
            sb.append(S).append(this.version);
        }
        if (this.damaged){
            sb.append(DAMAGED);
        }
        if (this.he){
            sb.append(HE);
        }
        if (this.hd){
            sb.append(HD);
        }
        if (this.alpha){
            sb.append(ALPHA);
        }
        return sb.toString();
    }

    /**
     * 精准匹配为配对的文件
     * @param assetFile 另一个资源文件
     * @return 精准匹配
     */
    @SuppressWarnings("RedundantIfStatement")
    public boolean matchPair(AssetFile assetFile) {
        //两个文件必须相似
        if (!similar(assetFile)) {
            return false;
        }
        //两个文件的version必须相同
        if (!Objects.equals(this.version, assetFile.getVersion())) {
            return false;
        }
        return true;
    }

    /**
     * 判断两个文件是否相似
     * @param assetFile 另一个资源文件
     * @return 是否相似
     */
    public boolean similar(AssetFile assetFile){
        //两个文件必须同为河蟹或非河蟹立绘
        if (this.he != assetFile.isHe()) {
            return false;
        }
        //两个文件必须同为差分或非差分
        if (this.difference != assetFile.isDifference()) {
            return false;
        }
        //两个文件必须同为重创或非重创
        if (this.damaged != assetFile.isDamaged()) {
            return false;
        }
        //两个文件的角色名必须相同
        if (!Objects.equals(this.character, assetFile.getCharacter())) {
            return false;
        }
        return true;
    }

    public AssetFile(File file) {
        this.file = file;
        this.parentPath = file.getParentFile().getPath();

        //文件名转换为全大写，移除多余字符
        final String name = file.getName().toUpperCase()
                .replace(PIC, "")
                .replace(NPC_1, "")
                .replace(NPC_2, "");
        final int dotIndex = name.lastIndexOf(".");

        this.hd = name.contains(HD);
        this.he = name.contains(HE_STRING_1) || name.contains(HE_STRING_3) || this.parentPath.contains(HE_STRING_2);
        this.alpha = name.contains(ALPHA);
        this.damaged = name.endsWith(DAMAGED_2) || name.contains(DAMAGED_1);
        this.extensions = name.substring(dotIndex);

        final String n = name.substring(0,dotIndex)
                .replaceAll(" #\\d+", "")
                .replace(ALPHA, "")
                .replace(HD, "")
                .replace(HE_STRING_1, "")
                .replace("_D", "");

        //文件名格式为 xxx_数字 的
        final Matcher matcher2 = PATTERN_2.matcher(n);
        if (matcher2.find()) {
            this.character = matcher2.group(1);
            this.version = matcher2.group(2);
            final int index = Integer.parseInt(this.version);
            //判断是否为差分
            this.difference = (index < 100) && !parentPath.toUpperCase().contains(matcher2.group(0));
        }
        //文件名格式为 xxx(数字)
        final Matcher matcher1 = PATTERN_1.matcher(n);
        if (matcher1.find()) {
            this.character = matcher1.group(1);
            this.version = matcher1.group(2);
            this.difference = true;
        }

        if (this.character==null){
            this.character = n;
            this.version = null;
            this.difference = false;
        }
    }

    public String toFilename(){
        return this.toFormatName()+this.extensions;
    }

    /**
     * 文件的相对路径
     * @return 文件的相对路径
     */
    public String getRelativePath(){
        final String path = this.file.getPath();
        return path.substring(path.indexOf(ASSET)+ASSET.length());
    }
}
