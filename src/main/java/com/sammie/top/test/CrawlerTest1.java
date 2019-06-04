package com.sammie.top.test;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CrawlerTest1 {
    public static void main(String[] args) throws Exception {
        String baseUrl = "http://zgao.top/page/";
        for (int i = 1; i < 7; i++) {
            doCrawler(baseUrl+i);
        }
    }

    public static void  doCrawler(String CrawlerUrl) throws URISyntaxException {
        //获取httpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //设置访问地址

        URIBuilder url = new URIBuilder(CrawlerUrl);
        //如需添加参数 可以使用url.setParameter(key,val)方法
        HttpGet httpGet  = new HttpGet(url.build());
        //发起请求
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            //        解析
            if (response.getStatusLine().getStatusCode()==200){
                List <String> title = new ArrayList<String>();
                List <String> link = new ArrayList<String>();
                List <String> moreContent = new ArrayList<String>();
                String content = EntityUtils.toString(response.getEntity(), "utf8");
                Document doc = Jsoup.parse(content);
                Elements titleElements = doc.select(".entry-title");
                Elements moreElements = doc.select(".more-link");
                for (Element element : titleElements) {
                    title.add(element.text());
                }
//                for (String s : title) {
//                    System.out.println(s);
//                }

                for (Element moreElement : moreElements) {
                    link.add(moreElement.attr("href"));
                }
                for (String s : link) {
                    //利用url再爬取详情内容，这里我写次了，最好用递归来做.另外可以直接用jsoup的方法 但是最好还是用httpclient
                    //因为一般我们可以多线程或者要均衡来爬
                    CloseableHttpClient client = HttpClients.createDefault();
                    HttpGet httpGet1 = new HttpGet(s);
                    CloseableHttpResponse res = null;
                    res = client.execute(httpGet1);
                    if (res.getStatusLine().getStatusCode()==200){
                        String result = EntityUtils.toString(res.getEntity(), "utf8");
                        Document parse = Jsoup.parse(result);
                        //获取详情数据
                        String text = parse.select(".entry-content").first().text();
                        moreContent.add(text);

                        client.close();
                        res.close();
                    }else{
                        System.out.println("出问题了，应该是服务器问题");
                    }
                }
//                for (String s : moreContent) {
//                    insert(s);
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static Connection getConn() {
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/test?characterEncoding=utf8&useSSL=false&serverTimezone=UTC";
        String username = "root";
        String password = "123456";
        Connection conn = null;
        try {
            Class.forName(driver); //classLoader,加载对应驱动
            try {
                conn = (Connection) DriverManager.getConnection(url, username, password);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return conn;
    }

    private static int insert(String content) {
        Connection conn = getConn();
        int i = 0;
        String sql = "insert into contentList (content) values(?)";
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, content);
            i = pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }
}
