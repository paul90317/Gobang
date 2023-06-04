package com.example.gobang.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class IPAddress {
    public static List<String> getIPAddress() throws Exception{
        List<String> temp=new ArrayList<String>();
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();
                if (inetAddress.getAddress().length == 4) {
                    String ipAddress = inetAddress.getHostAddress();
                    temp.add(ipAddress);
                }
            }
        }
        return temp;
    }
}
