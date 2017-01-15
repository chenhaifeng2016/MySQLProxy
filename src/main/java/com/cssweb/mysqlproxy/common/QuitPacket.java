package com.cssweb.mysqlproxy.common;

import io.netty.buffer.ByteBufUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by chenh on 2017/1/3.
 */
public class QuitPacket extends MsgPacket {
    @Override
    public byte[] create(byte seqID)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        try {
            out.writeByte(0x01);

        } catch (IOException e) {
            e.printStackTrace();
        }

        body = baos.toByteArray();

        header = new MsgHeader();
        header.setLen(body.length);
        header.setSeqID(seqID);



        return body;
    }

    @Override
    public void printPacket()
    {
        System.out.println("QuitPacket:" + ByteBufUtil.hexDump(body));
    }
}
