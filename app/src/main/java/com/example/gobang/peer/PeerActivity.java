package com.example.gobang.peer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gobang.ChessBoardASync;
import com.example.gobang.MainActivity;
import com.example.gobang.R;
import com.example.gobang.util.WebSocket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

abstract public class PeerActivity extends AppCompatActivity {
    protected PeerActivity self;
    protected WebSocket anotherPlayer;
    protected ChessBoardASync player;
    protected TextView statusView;
    protected TextView colorView;
    protected Handler UIHandler;
    protected void toast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
    protected void exit(){
        Intent attractionIntent = new Intent(this, MainActivity.class);
        startActivity(attractionIntent);
    }
    protected ExecutorService webHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.peer);
        dotView=findViewById(R.id.textView5);
        self=this;
        player=findViewById(R.id.imageView);
        colorView =findViewById(R.id.textView);
        statusView=findViewById(R.id.textView4);
        statusView.setText("");
        UIHandler=new Handler(Looper.getMainLooper());
        webHandler= Executors.newSingleThreadExecutor();
        UIHandler.post(()->dotUpdate());
    }
    private int dotCount;
    private TextView dotView;
    protected boolean dotShow;
    @UiThread
    private void dotUpdate(){
        dotUpdateOnce();
        UIHandler.postDelayed(()->dotUpdate(),800);
    }
    @UiThread
    protected void dotUpdateOnce(){
        dotCount=(dotCount+1)%4;
        String temp="";
        if(dotShow){
            for(int i=0;i<dotCount;i++){
                temp+=".";
            }
        }
        dotView.setText(temp);
    }
    public void surrender(View view) {
        exit();
        if(anotherPlayer != null)
            anotherPlayer.close();
        webHandler.shutdown();
    }
}
