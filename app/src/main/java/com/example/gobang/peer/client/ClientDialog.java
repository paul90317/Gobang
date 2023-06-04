package com.example.gobang.peer.client;

import android.content.Context;
import android.text.InputType;
import androidx.annotation.UiThread;
import com.example.gobang.util.NestedDialog;

public abstract class ClientDialog extends NestedDialog {
    private void parseAddressPort(String addressPort)throws Exception{
        String[] temp=addressPort.split(":");
        if(temp.length==2){
            String address=temp[0];
            int port=Integer.parseInt(temp[1]);
            if(port<=0||address.length()==0)
                throw new Exception();
            else
                onCreate(address,port);
        }else{
            throw new Exception();
        }
    }
    public final static int FORMAT_ERROR =-1;
    public final static int THREAD_ERROR=-3;
    @UiThread
    public abstract void onCreate(String address, int port);
    @UiThread
    public ClientDialog(Context self){
        super(self);
        input("進入房間","請輸入房間位址 (包含 port)", InputType.TYPE_CLASS_TEXT, "確定", data->{
            try {
                parseAddressPort(data);
            }catch (Exception e) {
                onCreate("",FORMAT_ERROR);
                return;
            }
            alert("進入房間","正在嘗試連線 "+data,"取消連線",()->{
                exit();
            });
        },"取消",data->{
            exit();
        });
    }
}
