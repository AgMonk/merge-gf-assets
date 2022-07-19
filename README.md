少女前线立绘文件合并工具，提供多线程，剧情差分立绘合并功能

## 支持的文件路径

1. 高清立绘（2048x2048）：assets/resources/dabao/pics/guns
2. 标清立绘及部分剧情立绘：assets/characters
3. 妖精立绘： assets/resources/dabao/pics/fairy

## 导出立绘之前 AssetStudio 需要进行的操作

1. 依次选择 File - Export options ，在弹窗的左侧， Group exported assets by 项目，点击下拉框选择 container path， OK退出
2. 点击资源列表的表头 Container ，让它以升序排列

## 准备工作

### 下载opencv

xxxx.png 文件的尺寸和xxxx_alpha.png 的尺寸可能是不同的，需要这个工具来进行放大。

打开 [opencv官网](https://opencv.org/releases/)  , 选择 windows版本下载、解压

进入路径：\opencv\build\java\x64 （64位系统），或 \opencv\build\java\x86 （32位系统），里面有一个 opencv_javaXXX.dll文件，把它复制出来，本文中我们把它放到 F:\Texture2D\run ， 重命名，把最后 XXX的版本号删除。

### 安装JDK

运行Java程序需要 ，可以直接参考 [这个链接](https://www.runoob.com/java/java-environment-setup.html) ， Win10 系统在配置环境变量的环节略有不同，可自行百度。

### 本工具下载

```
https://github.com/AgMonk/merge-gf-assets/releases
```

或

```
https://gitee.com/AgMonk/merge-gf-assets/releases
```

解压到 F:\Texture2D\run ，本工具需要与 opencv 的 dll 文件处于同一目录

## 关于字典

本工具使用字典来处理差分立绘共用Alpha文件的问题。另外该目录中还有一部分文件不需要合并可以直接复制，或不需要任何处理，也通过该功能实现。

字典保存在 dic.json 文件中，可以使用记事本打开修改。格式为 key = 原文件相对路径 ，value = alpha文件相对路径。value = copy时表示该文件直接复制，value = skip 时表示该文件不做处理。

工具自带编写字典功能，如果不想使用自带字典可以删除 dic.json 文件自己制作。

## 使用说明

### 配置流程

运行 run.bat 文件 可以看到如下提示

```
F:\Texture2D\run>java -Dfile.encoding=utf-8 -jar merge-gf-assets-0.0.1-SNAPSHOT.jar
Loading /F:\Texture2D\run/opencv_java.dll
Loading Dictionary F:\Texture2D\run\dic.json
请提供assets目录路径 >>
```

assets目录指的是 “导出立绘”步骤中生成的 assets文件夹的路径 ，本文中为 F:\Texture2D\assets

粘贴该路径，回车可以看到进一步提示

```
请提供输出目录路径 >>
```

这里指的是合并完成的立绘文件的存放目录，工具会按照**原目录结构**输出合并完成的立绘，本文中选择 F:\Texture2D\assets\output
粘贴该路径，回车可以看到进一步提示

```
请提供使用的线程数量  >>
```

理论上线程数越多速度越快，但也要综合考虑你的机器性能。作为参考，笔者的测试中 5线程工作中会占用 1G左右的内存。 这里选择 5 。

上述配置将保存在同目录下的 config.json 文件中，下一次启动时将直接使用不需要再次配置，可以用记事本打开修改，或者删除该文件即可再次配置。

然后工具会开始扫描 assets 文件夹中的文件 ， 略微等待会得到如下提示

```
扫描目录 F:\Texture2D\assets\characters | 发现 13146 个文件 | 用时： 30.2s
扫描目录 F:\Texture2D\assets\resources\dabao\pics\guns | 发现 7922 个文件 | 用时： 3.5s
扫描目录 F:\Texture2D\assets\resources\dabao\pics\fairy | 发现 495 个文件 | 用时： 0.0s
```

然后是

```
--------------------------
合并开始: \characters
匹配完成 跳过: 2 ,复制: 0 ,匹配: 3244 ,相似: 324
```

其中：

1. 跳过：输出目录中已经存在合并完成的立绘，或者根据字典跳过的文件数量
2. 复制：根据字典直接复制的文件数量
3. 匹配：符合标准命名格式的待合并文件数量
4. 相似：不符合标准命名格式的待合并文件数量，需要编写字典进行匹配

如果“相似”的数量大于0将会进入字典编写流程

### 字典编写

字典编写流程中会看到如下格式的提示，并同时用资源管理器打开一个文件夹

```
--------------------------
[INFO]原文件: 80TYPEMOD_DIFF_3 路径: F:\Texture2D\assets\characters\80typemod\pic\pic_80typeMod_3.png
        [0] Alpha文件: 80TYPEMOD_HD_ALPHA 路径: F:\Texture2D\assets\characters\80typemod\pic\pic_80typeMod_HD_Alpha.png
        [1] Alpha文件: 80TYPEMOD_ALPHA 路径: F:\Texture2D\assets\characters\80typemod\pic\pic_80typeMod_Alpha.png
        [2] Alpha文件: 80TYPEMOD_DIFF_2_ALPHA 路径: F:\Texture2D\assets\characters\80typemod\pic\pic_80typeMod_2_Alpha.png
        [3] Alpha文件: 80TYPEMOD_DIFF_4_ALPHA 路径: F:\Texture2D\assets\characters\80typemod\pic\pic_80typeMod_4_Alpha.png
请输入指令：
        序号 [0~3]：从上述列出的Alpha文件中选定一个与该文件匹配
        绝对路径 ：给定一个Alpha文件的绝对路径与该文件匹配，必须处在该文件夹内：F:\Texture2D\assets\output1\characters
        copy ：表示该文件不需要合并，直接复制
        skip ：表示该文件不需要任何处理
 >>>
```

它表示 F:\Texture2D\assets\characters\80typemod\pic\pic_80typeMod_3.png 这个文件没能找到精准匹配的 alpha
文件，打开的文件夹为该文件所在的文件夹，同时程序找到了文件名相似的可能匹配的Alpha文件，如果上述相似的文件有符合的，则可以输入它们前面的序号来选定。
或者，个别文件可能找不到文件名相似的Alpha文件，则需要自行搜索（有可能不在同一文件夹），并粘贴它的绝对路径。
（获取绝对路径的TIPS：找到文件后选中 Ctrl+C ， 然后点击地址栏 Ctrl+V ，即可填写它的绝对路径到地址栏，再全选复制即可。）
或者也可以输入最后两个指令来选择复制或者跳过。

字典全部编写完毕后会进入合并流程

### 合并流程

工具会持续输出如下格式的提示

```
[INFO][线程-1][1/11] 合并完成: RO635_DAMAGED_HD.PNG 用时： 3.9s
[INFO][线程-4][2/11] 合并完成: RO635MOD_HD.PNG 用时： 3.9s
[INFO][线程-2][3/11] 合并完成: RO635_HD.PNG 用时： 3.9s
[INFO][线程-5][4/11] 合并完成: RO635_4504_DAMAGED_HD.PNG 用时： 4.0s
[INFO][线程-3][5/11] 合并完成: RO635MOD_DAMAGED_HD.PNG 用时： 4.1s
[INFO][线程-2][6/11] 合并完成: RO635_534_DAMAGED_HD.PNG 用时： 2.6s
[INFO][线程-1][7/11] 合并完成: RO635_4504_HE_HD.PNG 用时： 2.6s
[INFO][线程-3][8/11] 合并完成: RO635_554_DAMAGED_HD.PNG 用时： 2.4s
[INFO][线程-5][9/11] 合并完成: RO635_534_HD.PNG 用时： 2.4s
[INFO][线程-4][10/11] 合并完成: RO635_4504_HD.PNG 用时： 2.7s
[INFO][线程-2][11/11] 合并完成: RO635_554_HD.PNG 用时： 2.2s
```

等待完成即可