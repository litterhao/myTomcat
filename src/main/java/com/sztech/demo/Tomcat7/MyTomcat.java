package com.sztech.demo.Tomcat7;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: Tomcat
 * @description:tomcat 启动类
 * @author: jiefu
 * @create: 2019-01-02 15:51
 **/
public class MyTomcat {

    private int port=8080;

    private Map<String,String> urlServletMap=new HashMap<String, String>();

    public MyTomcat(int port) {
        this.port = port;
    }

    public void start(){

        //初始化url与对应关系servlet的关系
        initServletMapping();
        ServerSocket serverSocket=null;
        try {
            serverSocket=new ServerSocket(port);
            System.out.println("myTomcat is start");

            while (true){
                Socket socket=serverSocket.accept();
                InputStream inputStream=socket.getInputStream();
                OutputStream outputStream=socket.getOutputStream();

                MyRequest myRequest=new MyRequest(inputStream);
                MyResponse myResponse=new MyResponse(outputStream);

                dispatch(myRequest,myResponse);
                socket.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(serverSocket!=null){
                try {
                    serverSocket.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void initServletMapping(){
        for(ServletMapping servletMapping:ServletMappingConfig.servletMappingList){
            urlServletMap.put(servletMapping.getUrl(),servletMapping.getClazz());
        }
    }

    private void dispatch(MyRequest myRequest,MyResponse myResponse){
        String clazz=urlServletMap.get(myRequest.getUrl());

        //反射
        try {
            Class<MyServlet> myServletClass=(Class<MyServlet>) Class.forName(clazz);
            MyServlet myServlet=myServletClass.newInstance();
            myServlet.service(myRequest,myResponse);

        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }catch (InstantiationException e){
            e.printStackTrace();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

        new MyTomcat(8080).start();
    }
}
