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
import java.net.ServerSocket;
import java.net.Socket;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ReceiveThread_10000 extends Thread {

    @Override
    public void run() {

        ServerSocket ss = null;
        Socket socket = null;
        InputStream is = null;
        OutputStream os = null;
        try {
            ss = new ServerSocket(10000);

            System.out.println("Port10000服务器 等待Client连接");
            socket = ss.accept();
            System.out.println("Port10000服务器 已经接受连接请求！");
            is = socket.getInputStream();
            os = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            if (!ss.isClosed() && socket.isConnected()) {
                byte[] b = new byte[1024];
                try {
                    is.read(b);
                    new tmpThread_server10000(os, b).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (!socket.isConnected()) {
                try {
                    System.out.println("Port10000服务器等待重连。。。");
                    socket = ss.accept();
                    is = socket.getInputStream();
                    os = socket.getOutputStream();
                    System.out.println("Port10000服务器 已经接受连接请求！");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    System.out.println("Port10000服务器意外关闭，正在打开服务器。。。");
                    ss = new ServerSocket(10000);
                    System.out.println("Port10000服务器等待重连。。。");
                    socket = ss.accept();
                    is = socket.getInputStream();
                    os = socket.getOutputStream();
                    System.out.println("Port10000服务器 已经接受连接请求！");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

class tmpThread_server10000 extends Thread {
    final private String xml_server_data_realtime = "<?xml version=\"1.0\" encoding=\"utf-8\"?><root><cmd><cmdType>READ_OK</cmdType><cmdObject>data_realtime</cmdObject><cmdPara></cmdPara><cmdResponseInfo>success</cmdResponseInfo></cmd><content><time>1523478954</time><gatewayID>1111111</gatewayID><item><DataID>1</DataID><DataValue>-1</DataValue></item><item><DataID>2</DataID><DataValue>-1</DataValue></item><item><DataID>3</DataID><DataValue>-1</DataValue></item><item><DataID>4</DataID><DataValue>-1</DataValue></item><item><DataID>5</DataID><DataValue>-1</DataValue></item><item><DataID>6</DataID><DataValue>-1</DataValue></item><item><DataID>7</DataID><DataValue>22</DataValue></item><item><DataID>8</DataID><DataValue>23</DataValue></item><item><DataID>9</DataID><DataValue>24</DataValue></item><item><DataID>10</DataID><DataValue>25</DataValue></item><item><DataID>11</DataID><DataValue>26</DataValue></item><item><DataID>12</DataID><DataValue>27</DataValue></item><item><DataID>13</DataID><DataValue>-1</DataValue></item><item><DataID>14</DataID><DataValue>-1</DataValue></item><item><DataID>15</DataID><DataValue>-1</DataValue></item><item><DataID>16</DataID><DataValue>-1</DataValue></item><item><DataID>17</DataID><DataValue>-1</DataValue></item><item><DataID>18</DataID><DataValue>-1</DataValue></item><item><DataID>19</DataID><DataValue>-1</DataValue></item><item><DataID>20</DataID><DataValue>-1</DataValue></item><item><DataID>21</DataID><DataValue>-1</DataValue></item><item><DataID>22</DataID><DataValue>-1</DataValue></item><item><DataID>23</DataID><DataValue>-1</DataValue></item><item><DataID>24</DataID><DataValue>-1</DataValue></item><item><DataID>25</DataID><DataValue>30</DataValue></item><item><DataID>26</DataID><DataValue>30</DataValue></item><item><DataID>27</DataID><DataValue>30</DataValue></item><item><DataID>28</DataID><DataValue>30</DataValue></item><item><DataID>29</DataID><DataValue>30</DataValue></item><item><DataID>30</DataID><DataValue>30</DataValue></item><item><DataID>31</DataID><DataValue>30</DataValue></item><item><DataID>32</DataID><DataValue>30</DataValue></item><item><DataID>33</DataID><DataValue>30</DataValue></item><item><DataID>34</DataID><DataValue>30</DataValue></item></content></root>";
    final private String xml_server_alarm = "<root><cmd><cmdType>READ_OK</cmdType><cmdObject>alarm</cmdObject><cmdPara></cmdPara><cmdResponseInfo>success</cmdResponseInfo></cmd><content><time>1523478954</time><gatewayID>1111111</gatewayID><item><DataID>1</DataID><DataValue>0</DataValue></item><item><DataID>2</DataID><DataValue>0</DataValue></item><item><DataID>3</DataID><DataValue>1</DataValue></item><item><DataID>4</DataID><DataValue>1</DataValue></item><item><DataID>5</DataID><DataValue>2</DataValue></item><item><DataID>6</DataID><DataValue>2</DataValue></item></content></root>";

    private OutputStream os = null;
    byte[] b = null;

    public tmpThread_server10000(OutputStream os, byte[] b) {
        this.os = os;
        this.b = b;
    }

    @Override
    public void run() {
        System.out.println("Port10000服务器收到的报文信息为：" + new String(b));
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
                            if (name.equals("cmdObject")) {
                                System.out.println("Port10000服务器接收到 " + value + "请求，准备回复......");

                                try {
                                    if(value.equals("data_realtime")) {
                                        os.write(xml_server_data_realtime.getBytes());
                                    } else if (value.equals("alarm")){
                                        os.write(xml_server_alarm.getBytes());
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
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
//                    for (Node node = parentNode.getFirstChild(); node != null; node = node.getNextSibling()) {
//                        if (node.getNodeType() == Node.ELEMENT_NODE) {
//                            String name = node.getNodeName();
//                            String value = null;
//                            if (node.getFirstChild() != null)
//                                value = node.getFirstChild().getNodeValue();
//                            System.out.println(name + ":" + value + "\t");
//                        }
//                    }
//                    System.out.println();
                }
            }
        }
    }

}