package com.example.dangerousproj_20191102;


import android.os.Bundle;
import android.os.Message;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class Picture_sendThread extends Thread {
    private boolean keepRunning = true;
    private String serverAddStr = null;
    private int serverPort = -1;
    private Data_update data_update;
    private final String xml_picture_realtime = "<?xml version=\"1.0\" encoding=\"utf-8\"?><root><cmd><cmdType>READ</cmdType><cmdObject>picture_realtime</cmdObject><cmdPara></cmdPara></cmd></root>";

    public Picture_sendThread(Data_update data_update) {
        this.serverAddStr = "127.0.0.1";
        this.serverPort = 10001;
        this.data_update=data_update;
    }

    public Picture_sendThread(String serverAddStr, int serverPort,Data_update data_update) {
        this.serverAddStr = serverAddStr;
        this.serverPort = serverPort;
        this.data_update=data_update;
    }

    @Override
    public void run() {
        byte[] sentMessage_picture_realtime = xml_picture_realtime.getBytes();
        Socket client = null;
        InetAddress serverAdd = null;
        SocketAddress remoteAddr = null;
        OutputStream os = null;
        InputStream is = null;
        try {
            serverAdd = InetAddress.getByName(serverAddStr);
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
            return;
        }

        int i = 0;

        System.out.println("Client向服务器发送连接请求。。。");
        do {
            try {
//				client = new Socket(serverAdd, 10001);
                client = new Socket();
                client.setSoTimeout(500);
                remoteAddr = new InetSocketAddress(serverAdd, serverPort);
                client.connect(remoteAddr, 3000);
                is = client.getInputStream();
                os = client.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (!client.isConnected());
        System.out.println("Client已经成功与服务器建立连接！");

        byte[] b;
        while (keepRunning) {
            if (client.isConnected()) {
                StringBuilder sb = new StringBuilder("");
                try {
                    b = new byte[4096];

                    System.out.println("端口"+client.getLocalPort()+"正在发送 图片 请求报文，长度为" + sentMessage_picture_realtime.length);
                    os.write(sentMessage_picture_realtime);
                    Thread.sleep(500);
                    while(is.read(b)>0) {
                        sb.append(new String(b));
                        System.out.println("图片是："+new String(sb));
                        b = null;
                        b = new byte[4096];
                    }

                    Thread.sleep(4500);
                } catch (IOException e) {
                    new pictureHandler(new String(sb).getBytes(),data_update).start();
                    try {
                        Thread.sleep(4500);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    b = null;
                }
            } else {
                System.out.println("Client与服务器连接意外断开，重新连接中。。。");
                do {
                    try {
                        client.connect(remoteAddr, 3000);
                        is = client.getInputStream();
                        os = client.getOutputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } while (!client.isConnected());
                System.out.println("Client与服务器重连成功！\"");
            }
        }
    }
}

class pictureHandler extends Thread {
    private byte[] b = null;
    private String pic1_base64 = null;
    private String pic2_base64 = null;
    private Data_update data_update;
    public pictureHandler(byte[] b,Data_update data_update) {
        this.b = b;
        this.data_update=data_update;
    }

    @Override
    public void run() {
        System.out.println("Client准备处理服务器返回的报文：");

		System.out.println("Client收到服务器返回的图片：" + new String(b));
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        ByteArrayInputStream serverBais = new ByteArrayInputStream(new String(b).replaceAll("\u0000", "").getBytes());
        Document doc = null;
        try {
            doc = db.parse(serverBais);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(doc == null)
            return;
        NodeList allElementsList = doc.getElementsByTagName("*");
        for (int i = 0; i < allElementsList.getLength(); i++) {
            Node parentNode = allElementsList.item(i);
            if (parentNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) parentNode;
                if (parentNode.getNodeName().equals("cmd")) {
//					for (Node node = parentNode.getFirstChild(); node != null; node = node.getNextSibling()) {
//						if (node.getNodeType() == Node.ELEMENT_NODE) {
//							String name = node.getNodeName();
//							String value = null;
//							if (node.getFirstChild() != null)
//								value = node.getFirstChild().getNodeValue();
//							System.out.println(name + ":" + value + "\t");
//						}
//					}
//					 System.out.println();
                } else if (parentNode.getNodeName().equals("content")) {
//                    int count = 0;
//                    for (Node node = parentNode.getFirstChild(); node != null; node = node.getNextSibling()) {
//                        if (node.getNodeType() == Node.ELEMENT_NODE) {
//                            String name = node.getNodeName();
//                            String value = null;
//                            if (name.equals("time")) {
//                                if (node.getFirstChild() != null)
//                                    value = node.getFirstChild().getNodeValue();
//                                System.out.println(name + ":" + value + "\t");
//                            } else if (name.equals("gatewayID")) {
//                                if (node.getFirstChild() != null)
//                                    value = node.getFirstChild().getNodeValue();
//                                System.out.println(name + ":" + value + "\t");
//                            }
//                        }
//                    }
//                    System.out.println();
                } else if (parentNode.getNodeName().equals("item")) {
                    Node node = parentNode.getFirstChild();
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        String name = node.getNodeName();
                        String value = null;
                        if (node.getFirstChild() != null)
                            value = node.getFirstChild().getNodeValue();
                        if(name.equals("DataID") && value.equals("35")) {
                            node = node.getNextSibling();
                            if (node.getFirstChild() != null)
                                pic1_base64 = node.getFirstChild().getNodeValue();
                        } else if (name.equals("DataID") && value.equals("36")){
                            node = node.getNextSibling();
                            if (node.getFirstChild() != null)
                                pic2_base64 = node.getFirstChild().getNodeValue();
                        }
                    }
                }
            }
        }
        System.out.println("35号相机的base64码如下："+pic1_base64);
        System.out.println("36号相机的base64码如下："+pic2_base64);


        Message message = Message.obtain();
        Bundle data = new Bundle();

        data.putString("base64_1",pic1_base64);
        data.putString("base64_2",pic2_base64);
        message.what=02;
        message.setData(data);
        data_update.sendMessage(message);








//        base64ToImage(pic1_base64, "C:\\Users\\Kang boy\\Desktop\\test01.png");
//        base64ToImage(pic2_base64, "C:\\Users\\Kang boy\\Desktop\\test02.png");
    }
//    public static boolean base64ToImage(String imgStr, String imgFilePath) {
//        if(StringUtils.isEmpty(imgStr)) // 图片数据为空
//            return false;
//        BASE64Decoder decoder = new BASE64Decoder();
//        try {
//            // Base64解码
//            byte[] b = decoder.decodeBuffer(imgStr);
//            for(int i = 0; i < b.length; ++i) {
//                if(b[i] < 0) { // 调整异常数据
//                    b[i] += 256;
//                }
//            }
//            OutputStream out = new FileOutputStream(imgFilePath);
//            out.write(b);
//            out.flush();
//            out.close();
//            return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
}