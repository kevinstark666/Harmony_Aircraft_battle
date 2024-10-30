package com.oop;

import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Statement;

public class http {

    public static String getPosttype(String requestLine) {
        //加14位因为还有request_type":
        int start =requestLine.indexOf("request_type")+14;
        System.out.println("开始位置为："+start);
        //只读一位即可
        int end =start+1;
        System.out.println("结束位置为："+end);
        String posttype = requestLine.substring(start, end);
        System.out.println("获取到的post类型为:" + posttype);
        return posttype;
    }
    public static String getUsername(String requestLine) {

        //加11位因为还有“和：
        int start =requestLine.indexOf("username")+11;
        System.out.println("开始位置为："+start);
        //减一位因为还有，
        int end =requestLine.indexOf("password")-3;
        System.out.println("结束位置为："+end);
        String username = requestLine.substring(start, end);

        System.out.println("获取到的用户名为:" + username);
        return username;
    }
    public static String getPassword(String requestLine) {
        //加3位因为":"
        int start =requestLine.indexOf("password")+11;
        //减2位因为还有“}
        int end =requestLine.length()-2;

        String password = requestLine.substring(start, end);
        System.out.println("获取到的密码为:" + password);
        return password;
    }

//    public static String getRecord(String requestLine) {
//        //加3位因为":"
//        int start =requestLine.indexOf("record")+8;
//        //减2位因为还有“}
//        int end =requestLine.length()-2;
//
//        String password = requestLine.substring(start, end);
//        System.out.println("获取到的记录为:" + password);
//        return password;
//    }
    public static boolean Response(OutputStream resp,String body){
        StringBuilder response = new StringBuilder();
        // 构造响应消息头
        response.append("HTTP/1.1 ").append(200).append("\r\n")
                .append("Content-Type: text/plain\r\n")
                .append("Content-Length: ").append(body.length()).append("\r\n")
                .append("\r\n");
        try{// 开始响应
            resp.write(response.toString().getBytes());
            resp.write(body.getBytes());
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
    // 主函数
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(1111);
        System.out.println("Server started on port 1111");

        //获取数据库连接对象
        MainTest test = new MainTest();
        Statement t=test.build_connection();

        while (true) {
            //循环监听等待请求
            Socket socket = serverSocket.accept();
            System.out.println("New client connected");

            byte[] buffer =new byte[1024];
            int a = socket.getInputStream().read(buffer);
            //响应对象
            OutputStream resp = socket.getOutputStream();
            //报文转化成字符串
            String requestLine = new String(buffer, 0, a);
            System.out.println("报文内容为："+requestLine);

            //获取请求类型
            String posttype = getPosttype(requestLine);
            //获取用户名和密码
            String username = getUsername(requestLine);
            String password = getPassword(requestLine);
            String record = test.get_userRecord(t,username);

            if(posttype.equals("1")){//登陆逻辑
                System.out.println("请求类型为：登录");

                //判断用户名和密码是否正确
                //正确返回用户记录，错误返回reject
                if( test.check_user(t,username,password)){
                    Response(resp,record);
                }
                else{
                    Response(resp,"reject");
                }
            }
            else if(posttype.equals("2")) {//注册逻辑
                System.out.println("请求类型为：注册");
                if( test.add_user(t,username,password)){
                    Response(resp,"pass");
                    System.out.println("注册成功");
                }else{
                    Response(resp,"reject");
                }
            }
            else if(posttype.equals("3")){
                System.out.println("请求类型为：更新");
                if(test.update_userRecord(t,username,password)){
                        Response(resp,"pass");
                        System.out.println("更新记录成功");
                }
            }
            else{
                System.out.println("请求类型为：其他");
            }
            System.out.println("请求处理完毕，继续监听");
            }

        }
    }
