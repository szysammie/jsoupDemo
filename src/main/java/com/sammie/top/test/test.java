package com.sammie.top.test;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;

public class test {
    public static void main(String[] args) {
        downloadHttpUrl("https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=3024430150,1944791863&fm=173&app=49&f=JPEG?w=218&h=146&s=1F104385BAE82E07683534A503008082","E:/files/","1.jpg");
    }

    public static void downloadHttpUrl(String url, String dir, String fileName) {
        try {
            URL httpurl = new URL(url);
            File dirfile = new File(dir);
            if (!dirfile.exists()) {
                dirfile.mkdirs();
            }

            FileUtils.copyURLToFile(httpurl, new File(dir+fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
