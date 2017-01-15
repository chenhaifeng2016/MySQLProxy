package com.cssweb.mysqlproxy.common;

import io.netty.buffer.ByteBufUtil;

/**
 * Created by chenh on 2017/1/3.
 */
public class MsgHeader {

    private byte[] buf = new byte[4];

    private byte[] lengthBuf;
    private int len;

    private byte seqID;


    public byte[] getLengthBuf() {
        return lengthBuf;
    }

    public void setLengthBuf(byte[] lengthBuf) {
        this.lengthBuf = lengthBuf;
    }



    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
        buf[0] = (byte)len; // 临时处理
    }


    public byte getSeqID() {
        return seqID;
    }

    public void setSeqID(byte seqID) {
        this.seqID = seqID;
        buf[3] = seqID;
    }

    public boolean resolve(byte[] buf)
    {
        len = buf[0];
        seqID = buf[3];
        return true;
    }

    public void printPacket()
    {
        System.out.println("消息头:" + ByteBufUtil.hexDump(buf));
    }

    public byte[] getBytes()
    {
        return buf;
    }
}
