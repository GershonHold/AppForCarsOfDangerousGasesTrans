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
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Spray extends Thread {
    private String serverAddStr = null;
    private int serverPort = -1;
    private final String xml_spray = "<?xml version=\"1.0\" encoding=\"utf-8\"?><root><cmd><cmdType>CONTROL</cmdType><cmdObject>spray</cmdObject><cmdPara>0</cmdPara></cmd></root>";
    private int sprayPara = 0;
    private Data_update data_update;
    private ArrayList arr;

    public Spray(Data_update data_update,int sprayPara) {
        this.serverAddStr = "127.0.0.1";
        this.serverPort = 10002;
        this.data_update=data_update;
        this.sprayPara = sprayPara;
    }

    public Spray(String serverAddStr, int serverPort, int sprayPara, Data_update data_update, ArrayList arr) {
        this.serverAddStr = serverAddStr;
        this.serverPort = serverPort;
        this.sprayPara = sprayPara;
        this.data_update=data_update;
        this.arr=arr;
    }

    @Override
    public void run() {
        byte[] sentMessage_spray = xml_spray.replaceAll(">0<", ">"+sprayPara + "<").getBytes();
        Socket client = null;
        SocketAddress remoteAddr = null;
        InetAddress serverAdd = null;
        OutputStream os = null;
        InputStream is = null;

        try {
            serverAdd = InetAddress.getByName(serverAddStr);
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
            return;
        }

        System.out.println("Client向服务器发送连接请求。。。");
        do {
            try {
//				client = new Socket(serverAdd, 10000);
                client = new Socket();
                client.setSoTimeout(1000);
                remoteAddr = new InetSocketAddress(serverAdd, serverPort);
                client.connect(remoteAddr, 3000);
                is = client.getInputStream();
                os = client.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (!client.isConnected());
        System.out.println("Client已经成功与服务器建立连接！");

        byte[] b = new byte[1024];
        if (client.isConnected()) {
            try {
                System.out.println("端口" + client.getLocalPort() + "正在发送 喷淋 请求报文，参数为：" + sprayPara);
                System.out.println(xml_spray.replaceAll(">0<", ">"+sprayPara + "<"));
                os.write(sentMessage_spray);
                is.read(b);
                new sprayHandler(b,data_update).start();
                Thread.sleep(500);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                b = null;
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

class sprayHandler extends Thread {
    private byte[] b = null;
    private Data_update data_update;

    public sprayHandler(byte[] b,Data_update data_update) {
        this.b = b;
        this.data_update=data_update;
    }

    @Override
    public void run() {
        System.out.println("Client收到服务器返回的报文：" + new String(b));

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
        NodeList allElementsList = doc.getElementsByTagName("*");
        boolean isForSpray = false;
        int sprayPara = 0;
        for (int i = 0; i < allElementsList.getLength(); i++) {
            Node parentNode = allElementsList.item(i);
            if (parentNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) parentNode;
                if (parentNode.getNodeName().equals("cmd")) {
                    for (Node node = parentNode.getFirstChild(); node != null; node = node.getNextSibling()) {
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            String name = node.getNodeName();
                            String value = null;
                            if (node.getFirstChild() != null)
                                value = node.getFirstChild().getNodeValue();

                            if (name.equals("cmdObject"))
                                isForSpray = true;

                            if (isForSpray)
                                if (name.equals("cmdPara")) {
                                    if (value.equals("1")) {
                                        sprayPara = 1;
                                    } else if (value.equals("2")) {
                                        sprayPara = 2;
                                    }
                                }
                        }
                    }
                }
            }
        }

        Message message = Message.obtain();
        Bundle data = new Bundle();
        data.putInt("spray",sprayPara);
        message.what=03;
        message.setData(data);
        data_update.sendMessage(message);
    }

}