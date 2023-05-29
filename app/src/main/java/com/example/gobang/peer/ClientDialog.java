package com.example.gobang.peer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import com.example.gobang.MainActivity;

import java.util.concurrent.CompletableFuture;

public class ClientDialog {
    public static class HostInfo{
        public final String address;
        public final int port;
        public HostInfo(String addressPort) throws Exception{
            String[] temp=addressPort.split(":");
            if(temp.length==2){
                this.address=temp[0];
                this.port=Integer.parseInt(temp[1]);
                if(this.port<=0)
                    throw new Exception();
            }else{
                throw new Exception();
            }
        }
        public HostInfo(int error){
            this.address="";
            this.port=error;
        }
    }
    public final static int FORMAT_ERROR =-1;
    public final static int THREAD_ERROR=-3;
    private AlertDialog alertDialog;
    private CompletableFuture<HostInfo> hostInfo;
    private Handler handler;
    public ClientDialog(Context self){
        hostInfo=new CompletableFuture<>();
        EditText portEditText = new EditText(self);
        portEditText.setInputType(InputType.TYPE_CLASS_TEXT); // Set input type to phone to show numeric keyboard

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(self);
        dialogBuilder.setTitle("進入房間");
        dialogBuilder.setMessage("請輸入房間位址 (包含 port)");
        dialogBuilder.setView(portEditText);
        AlertDialog.Builder ok = dialogBuilder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                HostInfo temp;
                try {
                    temp=new HostInfo(portEditText.getText().toString());
                }catch (Exception e) {
                    hostInfo.complete(new HostInfo(FORMAT_ERROR));
                    return;
                }
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(self);
                dialogBuilder.setTitle("進入房間");
                dialogBuilder.setMessage("正在嘗試連線");
                AlertDialog.Builder ok = dialogBuilder.setPositiveButton("取消連線", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent attractionIntent = new Intent(self, MainActivity.class);
                        self.startActivity(attractionIntent);
                    }
                });
                alertDialog = dialogBuilder.create();
                alertDialog.setCancelable(false);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
                hostInfo.complete(temp);
            }
        });
        dialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent attractionIntent = new Intent(self, MainActivity.class);
                self.startActivity(attractionIntent);
            }
        });
        handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                alertDialog = dialogBuilder.create();
                alertDialog.setCancelable(false);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        });
    }
    public void cancel(){
        alertDialog.cancel();
    }
    public HostInfo getHostInfo(){
        try{
            return hostInfo.get();
        }catch (Exception e){
            return new HostInfo(THREAD_ERROR);
        }
    }
}
