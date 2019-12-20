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

public class RealTime_SendThread extends Thread {
    private Data_update data_update;
    //private int[] dataArray;
    private boolean keepRunning = true;
    private String serverAddStr = null;
    private int serverPort = -1;
    private final String xml_data_realtime = "<?xml version=\"1.0\" encoding=\"utf-8\"?><root><cmd><cmdType>READ</cmdType><cmdObject>data_realtime</cmdObject><cmdPara>886</cmdPara></cmd></root>";
    private final String xml_alarm = "<?xml version=\"1.0\" encoding=\"utf-8\"?><root><cmd><cmdType>READ</cmdType><cmdObject>alarm</cmdObject><cmdPara></cmdPara></cmd></root>";

    public RealTime_SendThread(Data_update data_update) {
        this.serverAddStr = "127.0.0.1";
        this.serverPort = 10000;
        this.data_update = data_update;
    }

    public RealTime_SendThread(String serverAddStr, int serverPort,Data_update data_update) {
        this.serverAddStr = serverAddStr;
        this.serverPort = serverPort;
        this.data_update = data_update;
    }

    @Override
    public void run() {

        byte[] sentMessage_data_realtime = xml_data_realtime.getBytes();
        byte[] sentMessage_alarm = xml_alarm.getBytes();

        Socket client = null;
        SocketAddress remoteAddr = null;
        InetAddress serverAdd = null;
        OutputStream os = null;
        InputStream is = null;

        try {
            serverAdd = InetAddress.getByName(serverAddStr);
        } catch (
                UnknownHostException e1) {
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

        byte[] b;
        while (keepRunning) {
            if (client.isConnected()) {
                try {
                    b = new byte[4096];
                    System.out.println("端口" + client.getLocalPort() + "正在发送 报警状态 请求报文，长度为" + sentMessage_alarm.length);
                    os.write(sentMessage_alarm);
                    Thread.sleep(200);
                    is.read(b);
                    new messageHandler(b, data_update).start();
                    Thread.sleep(300);
                    System.out.println(
                            "端口" + client.getLocalPort() + "正在发送 实时数据 请求报文，长度为" + sentMessage_data_realtime.length);
                    os.write(sentMessage_data_realtime);
                    Thread.sleep(200);
                    is.read(b);
                    new messageHandler(b, data_update).start();
                    Thread.sleep(300);
                    // System.out.println("realtime_data & alarm xml sent successfully");
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(500);
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

class messageHandler extends Thread {
    private byte[] b;
    private Data_update data_update;
    private double[] dataArray = new double[34];
    private int[] alarmArray = new int[6];

    public messageHandler(byte[] b, Data_update data_update) {
        this.b = b;
        this.data_update = data_update;
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
        NodeList allElementsList = null;
        if (doc != null) {
            allElementsList = doc.getElementsByTagName("*");
        }
        boolean isREAD_OK = false;
        boolean isData_realtime = false;
        boolean isSuccess = false;

        if (allElementsList != null) {
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

                                if (name.equals("cmdType"))
                                    if (value.equals("READ_OK")) {
                                        isREAD_OK = true;
                                        //									System.out.println("isREAD_OK = true;");
                                    }

                                if (name.equals("cmdObject"))
                                    if (value.equals("data_realtime")) {
                                        isData_realtime = true;
                                        //									System.out.println("isData_realtime = true;");
                                    }

                                if (name.equals("cmdResponseInfo"))
                                    if (value.equals("success")) {
                                        isSuccess = true;
                                        //									System.out.println("isSuccess = true;");
                                    }

                                //							System.out.println(name + ":" + value + "\t");
                            }
                        }
                        //					System.out.println();
                    } else if (parentNode.getNodeName().equals("content")) {
                        //					int count = 0;
                        //					for (Node node = parentNode.getFirstChild(); node != null; node = node.getNextSibling()) {
                        //						if (node.getNodeType() == Node.ELEMENT_NODE) {
                        //							String name = node.getNodeName();
                        //							String value = null;
                        //							if (name.equals("time")) {
                        //								if (node.getFirstChild() != null)
                        //									value = node.getFirstChild().getNodeValue();
                        //								System.out.println(name + ":" + value + "\t");
                        //							} else if (name.equals("gatewayID")) {
                        //								if (node.getFirstChild() != null)
                        //									value = node.getFirstChild().getNodeValue();
                        //								System.out.println(name + ":" + value + "\t");
                        //							}
                        //						}
                        //					}
                        //					System.out.println();
                    } else if (parentNode.getNodeName().equals("item")) {
                        if (isREAD_OK && isSuccess) {
                            if (isData_realtime) {
                                int index = -1;
                                Node node = parentNode.getFirstChild();
                                if (node.getNodeType() == Node.ELEMENT_NODE) {
                                    String name = node.getNodeName();
                                    String value = null;
                                    if (node.getFirstChild() != null)
                                        value = node.getFirstChild().getNodeValue();

                                    if (name.equals("DataID")) {
                                        index = Integer.parseInt(value) - 1;
                                    }
                                    // System.out.println(name + ":" + value + "\t");
                                }

                                node = node.getNextSibling();
                                if (node.getNodeType() == Node.ELEMENT_NODE) {
                                    String name = node.getNodeName();
                                    String value = null;
                                    if (node.getFirstChild() != null)
                                        value = node.getFirstChild().getNodeValue();

                                    if (name.equals("DataValue")) {
                                        dataArray[index] = Double.parseDouble(value);
                                    }

                                    //								System.out.println(name + ":" + value + "\t");
                                }
                            } else {
                                int index = -1;
                                Node node = parentNode.getFirstChild();
                                if (node.getNodeType() == Node.ELEMENT_NODE) {
                                    String name = node.getNodeName();
                                    String value = null;
                                    if (node.getFirstChild() != null)
                                        value = node.getFirstChild().getNodeValue();

                                    if (name.equals("DataID")) {
                                        index = Integer.parseInt(value) - 1;
                                    }
                                    // System.out.println(name + ":" + value + "\t");
                                }

                                node = node.getNextSibling();
                                if (node.getNodeType() == Node.ELEMENT_NODE) {
                                    String name = node.getNodeName();
                                    String value = null;
                                    if (node.getFirstChild() != null)
                                        value = node.getFirstChild().getNodeValue();

                                    if (name.equals("DataValue")) {
                                        alarmArray[index] = Integer.parseInt(value);
                                    }
                                    //		System.out.println(name + ":" + value + "\t");
                                }
                            }
                        }
                    }

                }

            }
        }

        if (isData_realtime) {
            for (int j = 0; j < 24; j += 6) {
                if (dataArray[j] != -1) {
                    switch (j) {
                        case 0:
                            // 此处设置气体名称为：可燃气体
                            System.out.println("可燃气体");
                            break;
                        case 6:
                            // 此处设置气体名称为：氯气
                            System.out.println("氯气");
                            break;
                        case 12:
                            // 此处设置气体名称为：氨气
                            System.out.println("氨气");
                            break;
                        case 18:
                            // 此处设置气体名称为：环氧乙烷
                            System.out.println("环氧乙烷");
                            break;
                    }

                    for (int k = j; k < j + 6; k++) {
                        // 此处设置device j为：dataArray[j];
                        System.out.println("第" + (k - j + 1) + "个设备的值为" + dataArray[k]);
                    }
                    break;
                }
            }

            System.out.println("温度为" + dataArray[24]);
            System.out.println("湿度为" + dataArray[25]);
            System.out.println("后面的其他数据已经省略。。。");
            // 此处设置温度为dataArray[60]
            // 此处设置湿度为dataArray[61]
            // 依次向后直到33是罐体液位
        } else {
            for (int i = 0; i < 6; i++) {
                System.out.print((i + 1) + "号机报警状态：");
                if (dataArray[i] == 0) {
                    System.out.println("正常");
                } else if (dataArray[i] == 1) {
                    System.out.println("低报");
                } else if (dataArray[i] == 2) {
                    System.out.println("高报");
                }
            }

        }
        Message message = Message.obtain();
        Bundle data = new Bundle();

        data.putDoubleArray("data", dataArray);
        data.putIntArray("alarm",alarmArray);
        data.putBoolean("isData_realtime",isData_realtime);
        message.what = 01;
        message.setData(data);
        data_update.sendMessage(message);
    }
}