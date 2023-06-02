package com.example.gobang.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;

public class WebSocket {
    public class InputStreamLV {
        private static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;
        private static final int DEFAULT_BUFFER_SIZE = 8192;
        private final InputStream in;
        public InputStreamLV(InputStream in){
            this.in=in;
        }
        protected byte[] readNBytes(int len) throws IOException {
            if (len < 0) {
                throw new IllegalArgumentException("len < 0");
            }

            List<byte[]> buffers = null;
            byte[] result = null;
            int total = 0;
            int remaining = len;
            int n;
            do {
                byte[] buf = new byte[Math.min(remaining, DEFAULT_BUFFER_SIZE)];
                int nRead = 0;

                // read to EOF which may read more or less than buffer size
                while ((n = in.read(buf, nRead,
                        Math.min(buf.length - nRead, remaining))) > 0) {
                    nRead += n;
                    remaining -= n;
                }

                if (nRead > 0) {
                    if (MAX_BUFFER_SIZE - total < nRead) {
                        throw new OutOfMemoryError("Required array size too large");
                    }
                    total += nRead;
                    if (result == null) {
                        result = buf;
                    } else {
                        if (buffers == null) {
                            buffers = new ArrayList<>();
                            buffers.add(result);
                        }
                        buffers.add(buf);
                    }
                }
                // if the last call to read returned -1 or the number of bytes
                // requested have been read then break
            } while (n == 0 && remaining > 0);

            if (buffers == null) {
                if (result == null) {
                    return new byte[0];
                }
                return result.length == total ?
                        result : Arrays.copyOf(result, total);
            }

            result = new byte[total];
            int offset = 0;
            remaining = total;
            for (byte[] b : buffers) {
                int count = Math.min(b.length, remaining);
                System.arraycopy(b, 0, result, offset, count);
                offset += count;
                remaining -= count;
            }

            return result;
        }
        public void close() throws IOException{
            in.close();
        }
    }
    public final InputStreamLV in;
    public final OutputStream out;
    public final Socket socket;
    public WebSocket(Socket socket)throws IOException {
        in = new InputStreamLV(socket.getInputStream());
        out = socket.getOutputStream();
        this.socket = socket;
    }
    public void send(byte[] data)throws Exception{
        out.write(data.length>>24);
        out.write(data.length>>16);
        out.write(data.length>>8);
        out.write(data.length);
        out.write(data);
    }
    public byte[] receive()throws Exception{
        byte[] data = in.readNBytes(4);
        int len = (int) data[0] << 24 | (int) data[1] << 16 | (int) data[2] << 8 | (int) data[3];
        return in.readNBytes(len);
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
