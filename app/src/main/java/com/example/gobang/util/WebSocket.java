package com.example.gobang.util;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;

public class WebSocket {
    public final InputStream in;
    public final OutputStream out;
    public final Socket socket;
    private byte[] read(int len) throws IOException{
        byte[] data= new byte[len];
        int temp;
        for(int i=0;i<len;i++){
            temp=in.read();
            if(temp==-1){
                throw new IOException();
            }
            data[i]=(byte)temp;
        }
        return data;
    }
    public WebSocket(Socket socket)throws IOException {
        in = socket.getInputStream();
        out = socket.getOutputStream();
        this.socket = socket;
    }
    public void send(byte[] data)throws Exception{
        out.write(data.length>>24);
        out.write(data.length>>16);
        out.write(data.length>>8);
        out.write(data.length);
        out.write(data);
        out.flush();
    }
    public byte[] receive()throws Exception{
        byte[] data=read(4);
        int len = (int) data[0] << 24 | (int) data[1] << 16 | (int) data[2] << 8 | (int) data[3];
        return read(len);
    }
    public boolean close(){
        try {
            in.close();
            out.close();
            socket.close();
        }catch (Exception e){
            return false;
        }
        return true;
    }
}
