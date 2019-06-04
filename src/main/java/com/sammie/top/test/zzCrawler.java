package com.sammie.top.test;

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

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class zzCrawler {
    static String s = "";
    public static void main(String[] args) throws Exception {
        String baseUrl = "http://www.ypppt.com/moban/list-";
        ppt p = new ppt();
        for (int i = 2; i < 100; i++) {
            doCrawler(baseUrl+i+".html",p);
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
    public static  void  doCrawler(String CrawlerUrl,ppt p) throws URISyntaxException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        URIBuilder url = new URIBuilder(CrawlerUrl);
        //如需添加参数 可以使用url.setParameter(key,val)方法
        HttpGet httpGet  = new HttpGet(url.build());
        //发起请求
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode()==200){
                String content = EntityUtils.toString(response.getEntity(), "utf8");
                Document doc = Jsoup.parse(content);
                Elements elements = doc.select(".posts li");
                for (Element e : elements) {
                    String attr = e.select(".p-title").attr("href").substring(14,18);
                    if(attr.contains(".")){
                        attr = attr.replace(".", "");
                        if (attr.contains("h")){
                            attr = attr.replace("h","");
                        }
                        p.setLink(attr);
                    }
                    else{
                        p.setLink(attr);
                    }
                    String title = e.select(".p-title").text();
                    p.setTitle(title);
                    CloseableHttpClient htp = HttpClients.createDefault();
                    HttpGet get = new HttpGet("http://www.ypppt.com/p/d.php?aid="+attr);
                    CloseableHttpResponse execute = htp.execute(get);
                    if (execute.getStatusLine().getStatusCode()==200){
                        String con = EntityUtils.toString(execute.getEntity(), "utf8");
                        Document parse = Jsoup.parse(con);
                        Element li = parse.select("li").last();
                        String attr1 = li.select("a").attr("href");
                        if (attr1.contains("uploads")){
                            attr1 = "http://www.youpinppt.com"+attr1.replace("uploads/","");
                        }
                        p.setDownloadUrl(attr1);
                        insert(p);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static int insert(ppt p) {
        Connection conn = getConn();
        int i = 0;
        String sql = "insert into ppt (title,link,downloadUrl) values(?,?,?)";
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, p.getTitle());
            pstmt.setString(2, p.getLink());
            pstmt.setString(3, p.getDownloadUrl());
            i = pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }
}
