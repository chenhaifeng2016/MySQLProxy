package com.cssweb.mysqlproxy.common;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by chenh on 2017/1/3.
 */
public interface IMsgPacket {
    boolean resolve(byte[] buf);
    byte[] create(byte seqID);
    void printPacket();

}
