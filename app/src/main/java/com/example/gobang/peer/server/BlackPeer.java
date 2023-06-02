package com.example.gobang.peer.server;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import com.example.gobang.peer.CreationDialog;
import com.example.gobang.peer.PeerActivity;
import com.example.gobang.web.IPAddressUtil;
import com.example.gobang.web.WebSocket;

import java.net.ServerSocket;
import java.util.List;

public class BlackPeer extends PeerActivity {
    ServerSocket server;
    private void init(){
        server=null;
        int port;
        for(port = 7777; port<=8080; port ++){
            try{
                server=new ServerSocket(port);
            }catch (Exception e){
                continue;
            }
            break;
        }
        if(port==8881){
            toast("建立失敗，檢查你的網路功能");
            exit();
            return;
        }
        List<String> ips;
        try{
            ips = IPAddressUtil.getIPAddress();
            if(ips.size()==0){
                throw new Exception();
            }
        }catch (Exception e){
            toast("建立失敗，檢查你的網路功能");
            exit();
            return;
        }
        String msg="請讓對手輸入以下位址 (包含 port)\n";
        for (String ip:ips) {
            msg+=ip+":"+port+"\n";
        }
        CreationDialog preDialog=new CreationDialog(self);
        final String msgFinal=msg;
        UIHandler.post(()->preDialog.alert("等待對手",msgFinal,"取消",()->{
            webHandler.submit(()->{
                try{
                    server.close();
                }catch (Exception e){
                    Log.i("ERROR",e.toString());
                }
            });
            exit();
        }));
        try{
            anotherPlayer=new WebSocket(server.accept());
            server.close();
            preDialog.cancel();
            UIHandler.post(()->loop());
        }catch (Exception e){
            toast("等待中斷，檢查你的網路功能");
            Log.i("ERROR",e.toString());
            exit();
        }
    }

    private boolean ifContinue(int win){
        CreationDialog dialog=new CreationDialog(self);
        switch(win){
            case Color.BLACK:
                dialog.alert("遊戲結束","你贏了!","回主畫面",()->{
                    exit();
                });
                break;
            case Color.WHITE:
                dialog.alert("遊戲結束","你輸了!","回主畫面",()->{
                    exit();
                });
                break;
            case -2:
                dialog.alert("遊戲結束","平局!","回主畫面",()->{
                    exit();
                });
                break;
            default:
                return true;
        }
        dotShow=false;
        dotUpdateOnce();
        statusView.setText("遊戲結束");
        return false;
    }
    private void loop(){
        dotShow=false;
        dotUpdateOnce();
        statusView.setText("輪到你了");
        player.postClick((x, y)->{
            int win=player.placeChess(x,y);
            if(ifContinue(win)){
                dotShow=true;
                statusView.setText("等待對手行動");
                webHandler.submit(()->{
                    byte[] point;
                    try{
                        anotherPlayer.send(new byte[]{x,y});
                        point = anotherPlayer.receive();
                    }catch (Exception e){
                        Log.i("ERROR",e.toString());
                        toast("與對手失去連線");
                        exit();
                        return;
                    }
                    UIHandler.post(()->{
                        int win1=player.placeChess(point[0],point[1]);
                        if(ifContinue(win1))
                            loop();
                    });
                });
            }else{
                webHandler.submit(()->{
                    try{
                        anotherPlayer.send(new byte[]{x,y});
                    }catch (Exception e){
                        Log.i("ERROR",e.toString());
                    }
                });
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        colorView.setText("你的顏色:黑");
        webHandler.submit(()->{
            Looper.prepare();
            init();
        });
    }
}
