package com.gin.mergegfassets;

import com.gin.mergegfassets.script.MergeImage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@SpringBootApplication
public class MergeGfAssetsApplication {

    public static void main(String[] args) throws IOException {
//        SpringApplication.run(MergeGfAssetsApplication.class, args);

        MergeImage.init();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        File assetDir = null;
        while (assetDir == null || !assetDir.exists()) {
            System.out.print("AssetPath : ");
            assetDir = new File(reader.readLine());
        }
        System.out.println("path = " + assetDir.getPath());

        File rawFile = new File("F:/resource_downloader/py/aa/assets/resources/dabao/pics/guns/ro635_4504/pic_RO635_4504_HD.png");
        File alphaFile = new File("F:/resource_downloader/py/aa/assets/resources/dabao/pics/guns/ro635_4504/pic_RO635_4504_HD_Alpha.png");
        File destFile = new File("F:/resource_downloader/py/aa/pic_RO635_4504.png");
        MergeImage.mergeOpenCV(rawFile, alphaFile, destFile);

    }

}
