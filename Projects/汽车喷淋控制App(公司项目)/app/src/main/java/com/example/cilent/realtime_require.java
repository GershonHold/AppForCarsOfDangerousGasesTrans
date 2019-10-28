package com.example.cilent;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class realtime_require implements Runnable {


    public void run() {
        requireThree_in_one("192.168.2.1");// 函数种有循环
    }

    public boolean requireThree_in_one(String serverAddStr) {
        if (serverAddStr == null)
            return false;
        final String xml_data_realtime = "<?xml version=\"1.0\" encoding=\"utf-8\"?><root><cmd><cmdType>READ</cmdType><cmdObject>data_realtime</cmdObject><cmdPara>886</cmdPara></cmd></root>";
        final String xml_alarm = "<?xml version=\"1.0\" encoding=\"utf-8\"?><root><cmd><cmdType>READ</cmdType><cmdObject>alarm</cmdObject><cmdPara></cmdPara></cmd></root>";
        final String xml_picture_realtime = "<?xml version=\"1.0\" encoding=\"utf-8\"?><root><cmd><cmdType>READ</cmdType><cmdObject>picture_realtime</cmdObject><cmdPara></cmdPara></cmd></root>";

        byte[] sentMessage_data_realtime = xml_data_realtime.getBytes();
        byte[] sentMessage_alarm = xml_alarm.getBytes();
        byte[] sentMessage_picture_realtime = xml_picture_realtime.getBytes();

        InetAddress serverAdd = null;
        try {
            serverAdd = InetAddress.getByName(serverAddStr);
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
            return false;
        }
        Socket client = null;
        int i = 0;
        while (true) {
            try {
                System.out.println("TCP连接准备建立。。。");
                client = new Socket(serverAdd, 10000);
                System.out.println("TCP连接已经建立！");

                System.out.println("正在发送 报警状态 请求报文，长度为" + sentMessage_alarm.length);
                client.getOutputStream().write(sentMessage_alarm);

                System.out.println("正在发送 实时数据 请求报文，长度为" + sentMessage_data_realtime.length);
                client.getOutputStream().write(sentMessage_data_realtime);

                i = i % 5;
                if (i == 0) {
                    System.out.println("正在发送 图片 请求报文，长度为" + sentMessage_picture_realtime.length);
                    client.getOutputStream().write(sentMessage_picture_realtime);
                    System.out.println("requirePicture OK");
                }

                client.getOutputStream().flush();
                Thread.sleep(1000);
                System.out.println("clint_alarm.xmlrm.xml successfully");
                return true;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                i++;
                if (client != null) {
                    try {
                        client.close();
                        System.out.println("TCP连接已关闭！");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
