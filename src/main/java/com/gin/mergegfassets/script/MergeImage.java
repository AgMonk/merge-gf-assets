package com.gin.mergegfassets.script;

import com.gin.mergegfassets.utils.TimeUtils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author : ginstone
 * @version : v1.0.0
 * @since : 2022/7/13 16:30
 **/
public class MergeImage {
    public static void mergeOpenCv(File rawFile, File alphaFile, File destFile) throws IOException {
        final long start = System.currentTimeMillis();
        // 原图
        final BufferedImage rawImage = ImageIO.read(rawFile);
        final int width = rawImage.getWidth();
        final int height = rawImage.getHeight();
        // Alpha图
        final Mat alphaMat = Imgcodecs.imread(alphaFile.getPath(), Imgcodecs.IMREAD_UNCHANGED);

        //如果尺寸不一致，缩放 alpha文件
        if (width != alphaMat.width()) {
            //缩放图片
            Imgproc.resize(alphaMat, alphaMat, new Size(width, height), 0, 0, Imgproc.INTER_CUBIC);
        }
        final BufferedImage alphaImage = matToBuffer(".png", alphaMat);

        BufferedImage combined = combine(rawImage, alphaImage);
        ImageIO.write(combined, "PNG", destFile);
        System.out.print("Completed： " + destFile.getName() + " ");
        TimeUtils.printlnTimeCost(start);
    }

    /**
     * 合并两个 BufferedImage
     * @param rawImage   原图
     * @param alphaImage Alpha通道
     * @return BufferedImage
     */
    public static BufferedImage combine(BufferedImage rawImage, BufferedImage alphaImage) {
        final int width = rawImage.getWidth();
        final int height = rawImage.getHeight();
        BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                final int rawImageRgb = rawImage.getRGB(i, j);
                final int alphaImageRgb = alphaImage.getRGB(i, j);
                combined.setRGB(i, j, rawImageRgb & alphaImageRgb);
            }
        }
        return combined;
    }

    /**
     * 将Mat类转换为 BufferedImage类
     */
    public static BufferedImage matToBuffer(String fileExt, Mat mat) throws IOException {
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(fileExt, mat, mob);
        // convert the "matrix of bytes" into a byte array
        byte[] byteArray = mob.toArray();
        InputStream in = new ByteArrayInputStream(byteArray);
        return ImageIO.read(in);
    }


    public static void init() {
        // 加载动态库
        final String path = "/" + System.getProperty("user.dir") + "/opencv_java460.dll";
        System.out.println("Loading " + path);
        System.load(path);
//        System.load(url.getPath());
    }
}
