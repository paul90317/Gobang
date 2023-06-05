package com.example.gobang;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gobang.peer.ChessBoardASync;
import com.example.gobang.util.NestedDialog;

public class PlayingActivity extends AppCompatActivity {
    private ChessBoardASync chessBoard;
    private TextView turnView, repentMsgView;
    /* Called by chessBoard when it is clicked
    * This method display this turn is whose turn */
    public void setTurnText(String text){
        turnView.setText(text);
    }
    public void setRepentMsgText(String text){repentMsgView.setText(text);}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playing);
        chessBoard = findViewById(R.id.imageView);
        turnView=findViewById(R.id.textView);
        repentMsgView=findViewById(R.id.textView2);
        self=this;
        loop();
    }
    private void loop(){
        chessBoard.postClick((x, y)->{
            showMessageIfWin(chessBoard.placeChess(x,y));
            setRepentMsgText("");
            //現在是誰
            if(chessBoard.getRound()%2==0){
                setTurnText("現在輪到:黑");
            }else{
                setTurnText("現在輪到:白");
            }
            loop();
        });
    }
    public void restart_listener(View view){
        setTurnText("現在輪到:黑");
        setRepentMsgText("");
        chessBoard.reset();
    }

    public  void repent_listener(View view){
        boolean ret = chessBoard.repentChess();
        if(chessBoard.getRound()%2==0){
            setTurnText("現在輪到:黑");
        }else{
            setTurnText("現在輪到:白");
        }
        if (!ret){
            setRepentMsgText("你還沒有下棋！");
        }
    }
    public  void ai_listener(View view){
        showMessageIfWin(chessBoard.AIPlaceChess());
        setRepentMsgText("");
        //現在是誰
        if(chessBoard.getRound()%2==0){
            setTurnText("現在輪到:黑");
        }else{
            setTurnText("現在輪到:白");
        }
    }

    public  void back_to_home_listener(View view){
        Intent attractionIntent = new Intent(this, MainActivity.class);
        startActivity(attractionIntent);
    }

    private AppCompatActivity self;
    /* Called by chessBoard when it is clicked
     * if color is 0 means no winner, ignore
     * else show the winner and switch to main menu */
    public void showMessageIfWin(int color) {
        if(color==0)
            return;
        NestedDialog dialog=new NestedDialog(this);
        if(color== Color.BLACK){
            dialog.alert("遊戲結束","黑色贏了!","回主畫面",()->{
                Intent attractionIntent = new Intent(self, MainActivity.class);
                startActivity(attractionIntent);
            });
        }else if(color==Color.WHITE) {
            dialog.alert("遊戲結束","白色贏了!","回主畫面",()->{
                Intent attractionIntent = new Intent(self, MainActivity.class);
                startActivity(attractionIntent);
            });
        }
        else if(color==-2){
            dialog.alert("遊戲結束","和局!","回主畫面",()->{
                Intent attractionIntent = new Intent(self, MainActivity.class);
                startActivity(attractionIntent);
            });
        }
    }
}
