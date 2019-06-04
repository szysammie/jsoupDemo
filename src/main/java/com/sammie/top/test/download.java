package com.sammie.top.test;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;
import java.sql.*;

public class download {
    public static void main(String[] args) {
        getAll();
    }

    private static Integer getAll() {
        Connection conn = getConn();
        String sql = "select * from ppt limit 500 ";
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement)conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                downloadHttpUrl(rs.getString("downloadUrl"),"E:/files/",rs.getString("title")+".rar");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void downloadHttpUrl(String url, String dir, String fileName) {
        try {
            URL httpurl = new URL(url);
            File dirfile = new File(dir);
            if (!dirfile.exists()) {
                dirfile.mkdirs();
            }
            FileUtils.copyURLToFile(httpurl, new File(dir+fileName));
            System.out.println(fileName+"下载完成！");
        } catch (Exception e) {
            e.printStackTrace();
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
}
