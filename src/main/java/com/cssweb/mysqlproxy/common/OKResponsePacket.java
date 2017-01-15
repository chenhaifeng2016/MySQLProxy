package com.cssweb.mysqlproxy.common;

import io.netty.buffer.ByteBufUtil;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by chenh on 2017/1/3.
 */
public class OKResponsePacket extends MsgPacket {
    private byte header;
    private byte affectedRowsLen;
    private byte lastInsertIDLen;
    private byte[] statusFlags = new byte[2];
    private short warnings;

    @Override
    public boolean resolve(byte[] buf) {
        body = buf;

        boolean ret = false;
        int offset = 0;
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);
        DataInputStream in = new DataInputStream(bais);

        try {
            header = in.readByte();
            System.out.println("header:" + header);
            affectedRowsLen = in.readByte();
            lastInsertIDLen = in.readByte();
            in.readFully(statusFlags);

            byte[] tmp = new byte[2];
            ByteBuffer.wrap(statusFlags).order(ByteOrder.LITTLE_ENDIAN).get(tmp);
            System.out.println("tmp " + ByteBufUtil.hexDump(tmp).toUpperCase() );

            warnings = in.readShort();


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

    @Override
    public void printPacket()
    {
        StringBuffer sb = new StringBuffer();

        sb.append("OKResponsePacket：" + ByteBufUtil.hexDump(body).toUpperCase() + "\n");
        sb.append("affectedRowsLen：" + affectedRowsLen + "\n");
        sb.append("lastInsertIDLen：" + lastInsertIDLen + "\n");
        sb.append("statusFlags：" + ByteBufUtil.hexDump(statusFlags) + "\n");
        sb.append("warnings：" + warnings + "\n");



        System.out.println(sb.toString());
    }
}
