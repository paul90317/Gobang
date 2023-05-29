package com.example.gobang.peer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import androidx.annotation.Nullable;
import com.example.gobang.Gobang;
import com.example.gobang.PlayingActivity;
import com.example.gobang.R;

import java.util.concurrent.CompletableFuture;

public class ChessBoardSync extends View {

    Context context;
    private final Paint mBitmapPaint;
    private final Bitmap background;
    //畫筆
    private final Paint circlePaint;
    private float gridSize;
    private final Gobang game;
    public ChessBoardSync(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        // chess
        gridSize =getHeight()/13;
        game=new Gobang();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        //畫點選畫面時顯示的圈圈
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLACK);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(4f);

        background = BitmapFactory.decodeResource(getResources(), R.drawable.chessboard);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //初始化空畫布
        gridSize =getHeight()/13;
    }
    public int placeChess(int x,int y){
        return game.placeChess(x,y);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //不要背景圖的話，請從這邊刪
        @SuppressLint("DrawAllocation")
        Bitmap res = Bitmap.createScaledBitmap(background
                ,getWidth(),getHeight(),true);
        canvas.drawBitmap(res,0,0,mBitmapPaint);
        //到這邊

        //畫圓圈圈
        for(int x=0;x<13;x++){
            for(int y=0;y<13;y++){
                circlePaint.setColor(game.getChessColor(x,y));
                canvas.drawCircle(x*gridSize+gridSize/2, y*gridSize+gridSize/2, 30, circlePaint);
            }
        }
    }
    private CompletableFuture<byte[]> touchPoint=null;
    /* 不要讓 android event loop 的 thread call 這個 function，會 block */
    public byte[] getTouchPointSync() throws Exception{
        touchPoint = new CompletableFuture<>();
        byte[] ret= touchPoint.get();
        touchPoint=null;
        return ret;
    }
    /**覆寫:偵測使用者觸碰螢幕的事件*/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        byte x = (byte) (event.getX() / gridSize);
        byte y = (byte) (event.getY() / gridSize);

        // 確保x, y 不會out of bound
        if (x == 13){ x = 12;}
        if (y == 13){ y = 12;}
        // 確保這個落點是合理的，並且 getTouchPointSync 正在等
        if(event.getAction()==MotionEvent.ACTION_UP&&game.getChessColor(x,y)!=0&&touchPoint!=null){
            touchPoint.complete(new byte[]{x, y});
        }
        invalidate();
        return true;
    }
}
