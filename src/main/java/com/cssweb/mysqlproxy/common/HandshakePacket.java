package com.cssweb.mysqlproxy.common;

import io.netty.buffer.ByteBufUtil;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by chenh on 2017/1/3.
 */
public class HandshakePacket extends MsgPacket{

    private byte protocolVersion;
    private String serverVersion;
    private int connectionID;
    private byte[] authPluginDataPart1 = new byte[8];
    private byte filler;
    private short capabilityFlag1;
    private byte charset;
    private short statusFlags;
    private short capabilityFlag2;
    private byte authPluginDataLen;
    private byte[] unused = new byte[10];
    private byte[] authPluginDataPart2 = new byte[12];
    private String authPluginName;

    @Override
    public boolean resolve(byte[] buf)  {

        body = buf;

        boolean ret = false;
        int offset = 0;
        ByteArrayInputStream bais = new ByteArrayInputStream(body);
        DataInputStream in = new DataInputStream(bais);

        try {
            protocolVersion = in.readByte();


            serverVersion = readNullString(body, 1);
            in.skipBytes(serverVersion.length() + 1);

            connectionID = BigEndian2LittleEndian32(in.readInt());

            in.readFully(authPluginDataPart1);
            in.skipBytes(1); // 0x00

            capabilityFlag1 = BigEndian2LittleEndian16(in.readShort());


            charset = in.readByte();
            statusFlags = BigEndian2LittleEndian16(in.readShort());

            capabilityFlag2 = BigEndian2LittleEndian16(in.readShort());
            //capabilityFlag2 = (in.readShort());



            authPluginDataLen = in.readByte();
            in.skipBytes(10);

            in.readFully(authPluginDataPart2);
            in.skipBytes(1);

            offset = body.length - in.available();
            authPluginName = readNullString(body, offset);
            in.skipBytes(authPluginName.length() + 1);

            offset = in.available();

            ret = true;
        } catch (IOException e) {
            e.printStackTrace();

        }
        finally {
            try {
                bais.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return ret;
    }

    public byte[] getAuthPluginDataPart1() {
        return authPluginDataPart1;
    }

    public void setAuthPluginDataPart1(byte[] authPluginDataPart1) {
        this.authPluginDataPart1 = authPluginDataPart1;
    }

    public byte[] getAuthPluginDataPart2() {
        return authPluginDataPart2;
    }

    public void setAuthPluginDataPart2(byte[] authPluginDataPart2) {
        this.authPluginDataPart2 = authPluginDataPart2;
    }

    @Override
    public void printPacket()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("HandshakePacket：" + ByteBufUtil.hexDump(body).toUpperCase() + "\n");
        sb.append("protocolVersion：" + protocolVersion + "\n");
        sb.append("serverVersion：" + serverVersion + "\n");
        sb.append("connectionID：" + connectionID + "\n");
        sb.append("authPluginDataPart1：" + ByteBufUtil.hexDump(authPluginDataPart1) + "\n");
        sb.append("capabilityFlag1：" + ByteBufUtil.hexDump(shortToBytes(capabilityFlag1)) + "\n");
        sb.append("charset：" + charset + "\n");
        sb.append("statusFlags：" + statusFlags + "\n");
        sb.append("capabilityFlag2：" + ByteBufUtil.hexDump(shortToBytes(capabilityFlag2)) + "\n");
        sb.append("capabilityFlag2：" + Integer.toHexString(capabilityFlag2) + "\n");
        sb.append("authPluginDataPart2：" + ByteBufUtil.hexDump(authPluginDataPart2) + "\n");
        sb.append("authPluginName：" + authPluginName + "\n");


        System.out.println(sb.toString());
    }
}
