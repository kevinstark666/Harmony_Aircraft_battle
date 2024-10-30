package com.oop;

import java.sql.*;
public class MainTest {
    //返回数据库操作对象
    public Statement build_connection() {
        try {
            // 注册驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try{
            // 获取数据库连接对象
            Connection connection= DriverManager.getConnection("jdbc:mysql://localhost:3306/InfoForUser?characterEncoding=utf8&serverTimezone=GMT%2B8"
                    ,"root",null);
            connection.setAutoCommit(true);
            // 获取数据库操作对象
            Statement statement=connection.createStatement();
            System.out.println("获取对象成功");
            return statement;
        }
        catch (SQLException e) {

            throw new RuntimeException(e);
        }
    }

    //数据库检查用户名密码是否正确
    public boolean check_user(Statement statement,String user, String password) {
        try {
            ResultSet resultSet = statement.executeQuery("select * from UserInfo where username='" + user + "' and password='" + password + "';");
            if (resultSet.next()) {
                System.out.println("用户名密码正确");
                return true;
            } else {
                System.out.println("用户名密码错误");
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 新增用户
    public boolean add_user(Statement statement,String user, String password) {
        try {
            // sql添加新用户
            statement.executeUpdate("insert into userinfo (username, password,record) values('" + user + "','" + password + "',0);");
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 更新用户记录
    public boolean update_userPass(Statement statement,String user, String record) {
        try {
            statement.executeUpdate("update userinfo set record='" + record + "' where username='" + user + "';");
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 获取用户记录
    public String get_userRecord(Statement statement,String user) {
        try {
            ResultSet resultSet = statement.executeQuery("select record from userinfo where username='" + user + "';");
            if (resultSet.next()) {
                return resultSet.getString("record");
            } else {
                System.out.println("用户不存在");
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    // 更新用户成绩
    public boolean update_userRecord(Statement statement,String user, String record) {
        try {
            statement.executeUpdate("update userinfo set record='" + record + "' where username='" + user + "';");
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args)  {

        try {
            // 获取数据库连接对象
            Connection connection= DriverManager.getConnection("jdbc:mysql://localhost:3306/InfoForUser?characterEncoding=utf8&serverTimezone=GMT%2B8"
            ,"root",null);
            connection.setAutoCommit(false);
            // 获取数据库操作对象
            Statement statement=connection.createStatement();
            System.out.println("获取对象成功");
            ResultSet resultSet=statement.executeQuery("select * from UserInfo;");

            //提交数据
            connection.commit();
            System.out.println("数据查询完成");
            while(resultSet.next()) {
                System.out.println(resultSet.getString("record"));
            }
            //connection.rollback();
        } catch (SQLException e) {

            throw new RuntimeException(e);
        }

    }
}
