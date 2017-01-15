package com.cssweb.mysqlproxy.common;

import java.io.DataInputStream;

/**
 * Created by chenh on 2017/1/3.
 */
public class MsgPacket implements IMsgPacket {
    protected MsgHeader header;
    protected byte[] body;
    protected byte type;



    public boolean resolve(byte[] buf) {
        return false;
    }
    public byte[] create(byte seqID) {
        return new byte[0];
    }
    public void printPacket() {    }


    public MsgHeader getHeader() {
        return header;
    }
    public void setHeader(MsgHeader header) {
        this.header = header;
    }

    public byte[] getBody() {
        return body;
    }
    public void setBody(byte[] body) {
        this.body = body;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public String readNullString(byte[] buf, int offset) {
        int len = 0;
        int i = offset;

        for (; i < buf.length; i++) {
            if (buf[i] == (byte) 0x00) {
                break;

            }
        }

        len = i - offset;

        byte[] tmp = new byte[len];
        System.arraycopy(buf, offset, tmp, 0, len);

        return new String(tmp);
    }


    int BigEndian2LittleEndian32(int x) {
        return (x & 0xFF) << 24 | (0xFF & x >> 8) << 16 | (0xFF & x >> 16) << 8 | (0xFF & x >> 24);
    }

    short BigEndian2LittleEndian16(short x) {
        return (short) ((x & 0xFF) << 8 | 0xFF & (x >> 8));
    }

    public byte[] shortToBytes(short n) {
        byte[] b = new byte[2];
        b[1] = (byte) (n & 0xff);
        b[0] = (byte) ((n >> 8) & 0xff);
        return b;
    }

}
