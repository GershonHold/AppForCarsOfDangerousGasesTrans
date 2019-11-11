package com.example.dangerousproj_20191102;

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


public class Alarm_state extends Thread {
    private String serverAddStr = null;
    private int serverPort = -1;
    private String xml_alarm_state = "<?xml version=\"1.0\" encoding=\"utf-8\"?><root><cmd><cmdType>WRITE</cmdType><cmdObject>alarm_state</cmdObject><cmdPara></cmdPara></cmd><content><time>1523478954</time><gatewayID>1111111</gatewayID><item><DataValue>firstPara</DataValue><TimeValue>secondPara</TimeValue></item><item><DataValue>thirdPara</DataValue><TimeValue>forthPara</TimeValue></item><item><DataValue>fifthPara</DataValue><TimeValue>sixthPara</TimeValue></item><item><DataValue>seventhPara</DataValue><TimeValue>eigthPara</TimeValue></item></content></root>";
    private int[] paras = new int[8];


    public Alarm_state() {
        this.serverAddStr = "127.0.0.1";
        this.serverPort = 10003;
        // 随意分配
        for (int i = 0; i < 8; i++) {
            this.paras[i] = i % 3;
        }
        String tmp = xml_alarm_state;
        tmp = tmp.replace("firstPara", this.paras[0] + "");
        tmp = tmp.replace("secondPara", this.paras[1] + "");
        tmp = tmp.replace("thirdPara", this.paras[2] + "");
        tmp = tmp.replace("forthPara", this.paras[3] + "");

        tmp = tmp.replace("fifthPara", this.paras[4] + "");
        tmp = tmp.replace("sixthPara", this.paras[5] + "");
        tmp = tmp.replace("seventhPara", this.paras[6] + "");
        tmp = tmp.replace("eigthPara", this.paras[7] + "");
        xml_alarm_state = tmp;
    }

    public Alarm_state(String serverAddStr, int serverPort, int[] paras) {
        this.serverAddStr = serverAddStr;
        this.serverPort = serverPort;
        this.paras = paras;
        String tmp = xml_alarm_state;
        tmp = tmp.replace("firstPara", paras[0] + "");
        tmp = tmp.replace("secondPara", paras[1] + "");
        tmp = tmp.replace("thirdPara", paras[2] + "");
        tmp = tmp.replace("forthPara", paras[3] + "");

        tmp = tmp.replace("fifthPara", paras[4] + "");
        tmp = tmp.replace("sixthPara", paras[5] + "");
        tmp = tmp.replace("seventhPara", paras[6] + "");
        tmp = tmp.replace("eigthPara", paras[7] + "");
        xml_alarm_state = tmp;
    }

    @Override
    public void run(){

    byte[] sentMessage_alarmState = xml_alarm_state.getBytes();

    Socket client = null;
    SocketAddress remoteAddr = null;
    InetAddress serverAdd = null;
    OutputStream os = null;
    InputStream is = null;

		try

    {
        serverAdd = InetAddress.getByName(serverAddStr);
    } catch(
    UnknownHostException e1)

    {
        e1.printStackTrace();
        return;
    }

		System.out.println("Client向服务器发送连接请求。。。");
		do

    {
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
    } while(!client.isConnected());
		System.out.println("Client已经成功与服务器建立连接！");

		if(client.isConnected())

    {
        try {
            byte[] b = new byte[1024];
            System.out.println("端口" + client.getLocalPort() + "正在发送 报警阈值设置 请求报文");
            os.write(sentMessage_alarmState);
            is.read(b);
            new alarmStateHandler(b).start();
            Thread.sleep(500);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    } else

    {
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
        System.out.println("Client与服务器重连成功！");
    }
}
}

class alarmStateHandler extends Thread {
    private byte[] b = null;
    private boolean isSuccessful = false;

    public alarmStateHandler(byte[] b) {
        this.b = b;
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
        boolean isForAlarmState = false;

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
                                if(value.equals("alarm_state"))
                                    isForAlarmState = true;

                            if (isForAlarmState)
                                if (name.equals("cmdResponseInfo")) {
                                    if (value.equals("success")) {
                                        isSuccessful = true;
                                    }
                                }
                        }
                    }
                }
            }
        }
        if (isSuccessful == true)
            System.out.println("已经设置成功！");

    }

}