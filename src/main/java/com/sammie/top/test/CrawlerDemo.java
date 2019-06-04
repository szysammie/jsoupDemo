package com.sammie.top.test;


import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class CrawlerDemo {
    public static void main(String[] args) throws IOException {
        //获取http对象
        CloseableHttpClient httpClients = HttpClients.createDefault();
        //获取网址
        HttpGet httpGet = new HttpGet("http://www.itcast.cn");
        //响应
        CloseableHttpResponse response = httpClients.execute(httpGet);
        //解析
        if (response.getStatusLine().getStatusCode()==200){
            HttpEntity httpEntity = response.getEntity();
            String content = EntityUtils.toString(httpEntity, "utf8");
            System.out.println(content);
        }
    }
}
