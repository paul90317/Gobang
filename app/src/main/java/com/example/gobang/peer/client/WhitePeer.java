package com.example.gobang.peer.client;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.UiThread;
import com.example.gobang.util.NestedDialog;
import com.example.gobang.peer.PeerActivity;
import com.example.gobang.util.WebSocket;

import java.net.Socket;

public class WhitePeer extends PeerActivity {

    @UiThread
    private void init(){
        NestedDialog preDialog=new ClientDialog(self) {
            @Override
            public void onCreate(String address, int port) {
                switch (port){
                    case ClientDialog.THREAD_ERROR:
                        toast("程序因未知原因中斷，請聯絡開發者");
                        exit();
                        return;
                    case ClientDialog.FORMAT_ERROR:
                        toast("位址格式錯誤");
                        exit();
                        return;
                }
                webHandler.submit(()->{
                    try{
                        anotherPlayer=new WebSocket(new Socket(address, port));
                    }catch (Exception e){
                        toast("房間不存在");
                        exit();
                        return;
                    }
                    cancel();
                    UIHandler.post(()->loop());
                });
            }
        };
    }
    @UiThread
    private boolean ifContinue(int win){
        NestedDialog dialog=new NestedDialog(self);
        switch(win){
            case Color.BLACK:
                dialog.alert("遊戲結束","你輸了!","回主畫面",()->{
                    exit();
                });
                break;
            case Color.WHITE:
                dialog.alert("遊戲結束","你贏了!","回主畫面",()->{
                    exit();
                });
                break;
            case Color.GRAY:
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
        dotShow=true;
        statusView.setText("等待對手行動");
        webHandler.submit(()-> {
            byte[] point;
            try{
                point = anotherPlayer.receive();
            }catch (Exception e){
                Log.i("ERROR",e.toString());
                toast("與對手失去連線");
                exit();
                return;
            }
            UIHandler.post(()->{
                if(ifContinue(player.placeChess(point[0],point[1]))){
                    dotShow=false;
                    dotUpdateOnce();
                    statusView.setText("輪到你了");
                    player.postClick((x, y)->{
                        if(ifContinue(player.placeChess(x,y))){
                            webHandler.submit(()->{
                                try{
                                    anotherPlayer.send(new byte[]{x,y});
                                }catch (Exception e){
                                    Log.i("ERROR",e.toString());
                                    toast("與對手失去連線");
                                    exit();
                                    return;
                                }
                                UIHandler.post(()->loop());
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
            });
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        colorView.setText("你的顏色:白");
        webHandler.submit(()->Looper.prepare());
        init();
    }
}
