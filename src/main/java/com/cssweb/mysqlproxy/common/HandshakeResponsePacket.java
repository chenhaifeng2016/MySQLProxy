package com.cssweb.mysqlproxy.common;

import io.netty.buffer.ByteBufUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by chenh on 2017/1/4.
 */
public class HandshakeResponsePacket extends MsgPacket {
    private short capabilityFlag1;
    private short capabilityFlag2;
    private int maxPacketSize;
    private byte characterSet;
    private byte[] reserved = new byte[23];
    private String username;

    private byte[] authResponse = new byte[20];
    private String database;
    private String authPluginName;

    private byte connAttributesLen;
    private Map<String, String> connAttributes = new LinkedHashMap<String, String>();



    @Override
    public byte[] create(byte seqID)
    {
        connAttributes.put("_os", "Win64");
        connAttributes.put("_client_name", "libmysql");
        connAttributes.put("_pid", "10592");
        connAttributes.put("_thread", "11120");
        connAttributes.put("_platform", "x86_64");
        connAttributes.put("program_name", "MySQLWorkbench");
        connAttributes.put("_client_version", "5.6.24");


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        try {

            capabilityFlag1 = (short) 0xA68D;
            capabilityFlag1 = BigEndian2LittleEndian16(capabilityFlag1);
            out.writeShort(capabilityFlag1);

            capabilityFlag2 = 0x007F;
            capabilityFlag2 = BigEndian2LittleEndian16(capabilityFlag2);
            out.writeShort(capabilityFlag2);

            maxPacketSize = 1073741824;
            maxPacketSize = BigEndian2LittleEndian32(maxPacketSize);
            out.writeInt(maxPacketSize);

            characterSet = 33;//0x21
            out.writeByte(characterSet);


            out.write(reserved);

            username = "web";
            out.writeBytes(username);
            out.writeByte(0x00);

            out.writeByte(authResponse.length);
            out.write(authResponse);


            database = "web";
            out.writeBytes(database);
            out.writeByte(0x00);

            authPluginName = "mysql_native_password";
            out.writeBytes(authPluginName);
            out.writeByte(0x00);

            //计算属性总长度
            for (Map.Entry<String, String> entry : connAttributes.entrySet())
            {
                connAttributesLen += 1 + entry.getKey().length() + 1 + entry.getValue().length();
            }
            out.writeByte(connAttributesLen);

            for (Map.Entry<String, String> entry : connAttributes.entrySet())
            {
                out.writeByte(entry.getKey().length());
                out.writeBytes(entry.getKey());

                out.writeByte(entry.getValue().length());
                out.writeBytes(entry.getValue());
            }
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
        System.out.println("HandshakeResponsePacket:" + ByteBufUtil.hexDump(body).toUpperCase());

    }

    public  void password(byte[] salt1, byte[] salt2)
    {
        byte[] salt = new byte[salt1.length + salt2.length];
        System.arraycopy(salt1, 0, salt, 0, salt1.length);
        System.arraycopy(salt2, 0, salt, salt1.length, salt2.length);

        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA1");



            //SHA1( password ) XOR SHA1( "20-bytes random data from server" <concat> SHA1( SHA1( password ) ) )
            String pwd = "chf123!@#";
            sha1.update(pwd.getBytes());
            byte[] result1 = sha1.digest();

            sha1.update(result1);
            byte[] result = sha1.digest();

            byte[] src = new byte[salt.length + result.length];
            System.arraycopy(salt, 0x00, src, 0x00, salt.length);
            System.arraycopy(result, 0x00, src, salt.length, result.length);

            sha1.update(src);
            result = sha1.digest();

            for (int i=0; i<result.length; i++)
            {
                authResponse[i] =  (byte)((byte)result1[i] ^ (byte)result[i]);
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        System.out.println("password=" + ByteBufUtil.hexDump(authResponse).toUpperCase());

    }

}
