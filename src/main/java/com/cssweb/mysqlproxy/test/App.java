package com.cssweb.mysqlproxy.test;

import com.cssweb.mysqlproxy.common.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;


public class App 
{
    Socket socket = new Socket();
    DataInputStream in;
    DataOutputStream out;

    public App()
    {

    }

    public void connect()
    {

        //SocketAddress addr = new InetSocketAddress("10.0.0.237", 3306);
        SocketAddress addr = new InetSocketAddress("127.0.0.1", 3306);
        try {
            socket.connect(addr);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handshake()
    {

        System.out.println("接收服务器认证消息包");
        MsgPacket msg = recvPacket();
        if (msg == null){}

            HandshakePacket handshakePacket = new HandshakePacket();
            handshakePacket.setHeader(msg.getHeader());
            if (!handshakePacket.resolve(msg.getBody()))
            {

            }
            handshakePacket.printPacket();


        System.out.println("发送客户端认证消息包");
            HandshakeResponsePacket handshakeResPacket = new HandshakeResponsePacket();
            handshakeResPacket.password(handshakePacket.getAuthPluginDataPart1(), handshakePacket.getAuthPluginDataPart2());
            handshakeResPacket.create((byte)(handshakePacket.getHeader().getSeqID() + 1));
            handshakeResPacket.printPacket();
            sendPacket(handshakeResPacket);


        msg = recvPacket();
        if (msg == null){}

        if (msg.getType() == 0)
        {
            System.out.println("接收服务器响应消息包");
            OKResponsePacket ok = new OKResponsePacket();
            ok.setHeader(msg.getHeader());
            if (!ok.resolve(msg.getBody()))
            {
            }
            ok.printPacket();

        }
        else
        {
            System.out.println("服务器返回其它包类型");
        }
    }

    public void quit()
    {
        System.out.println("发送quit消息包");

        QuitPacket quit = new QuitPacket();
        quit.create((byte)0);

        sendPacket(quit);
    }

    private MsgPacket recvPacket()
    {

        MsgPacket msg = null;

        try {


            byte[] headerBuf = new byte[4];
            in.readFully(headerBuf);

            MsgHeader header = new MsgHeader();
            if (!header.resolve(headerBuf))
            {

            }

            byte[] body = new byte[header.getLen()];
            in.readFully(body);


            byte type = body[0];

            msg = new MsgPacket();
            msg.setHeader(header);
            msg.setBody(body);
            msg.setType(type);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return msg;
    }

    private void sendPacket(MsgPacket packet)
    {
        try {
            out.write(packet.getHeader().getBytes());
            out.write(packet.getBody());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void close()
    {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main( String[] args )
    {
        App app = new App();
        app.connect();
        app.handshake();
        app.quit();
        app.close();
    }
}
