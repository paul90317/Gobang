package com.example.gobang.peer;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.example.gobang.R;

public class ChessBoardASync extends View {
    static public interface ChessPlaceHandler {
        void run(byte x, byte y);
    }
    Context context;
    private final Paint mBitmapPaint;
    private final Bitmap background;
    //畫筆
    private final Paint circlePaint;
    private float gridSize;
    private final Gobang game;
    private final Paint redPaint;
    private final Paint greenPaint;
    public ChessBoardASync(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        // chess
        gridSize =(float)getHeight()/13;
        game=new Gobang();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        //畫點選畫面時顯示的圈圈
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLACK);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(0f);

        redPaint=new Paint();
        redPaint.setColor(Color.RED);

        greenPaint=new Paint();
        greenPaint.setColor(Color.GREEN);

        background = BitmapFactory.decodeResource(getResources(), R.drawable.chessboard);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //初始化空畫布
        gridSize =(float)getHeight()/13;
    }
    public int placeChess(int x,int y){
        int temp = game.placeChess(x,y);
        invalidate();
        return temp;
    }
    public int repentChess(){
        return game.repentChess();
    }
    public int getRound(){
        return game.getRound();
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
        if(checkX>=0){
            canvas.drawRect(0, checkY*gridSize+gridSize*0.4f, getWidth(), checkY*gridSize+gridSize*0.6f, greenPaint);
            canvas.drawRect(checkX*gridSize+gridSize*0.4f, 0, checkX*gridSize+gridSize*0.6f, getHeight(), greenPaint);
        }
        int[] point = game.getLastMove();
        if(point[0]>=0){
            canvas.drawRect(point[0]*gridSize+gridSize*0.1f, point[1]*gridSize+gridSize*0.1f, point[0]*gridSize+gridSize*0.9f, point[1]*gridSize+gridSize*0.9f, redPaint);
        }
        //畫圓圈圈
        for(int x=0;x<13;x++){
            for(int y=0;y<13;y++){
                circlePaint.setColor(game.getChessColor(x,y));
                canvas.drawCircle(x*gridSize+gridSize/2, y*gridSize+gridSize/2, gridSize*0.4f, circlePaint);
            }
        }
    }
    private ChessPlaceHandler chessPlaceHandler = null;
    public void postClick(ChessPlaceHandler chessPlaceHandler){
        this.chessPlaceHandler = chessPlaceHandler;
    }
    /**覆寫:偵測使用者觸碰螢幕的事件*/
    private int checkX=-1,checkY=-1;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        byte x = (byte) (event.getX() / gridSize);
        byte y = (byte) (event.getY() / gridSize);

        // 確保x, y 不會out of bound
        if(x>12||y>12||x<0||y<0){
            checkY=checkX=-1;
            invalidate();
            return true;
        }
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                if (game.getChessColor(x, y) == 0 && chessPlaceHandler != null) {
                    ChessPlaceHandler temp = chessPlaceHandler;
                    chessPlaceHandler = null;
                    temp.run(x, y);
                }
                checkY=checkX=-1;
                break;
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (game.getChessColor(x, y) == 0) {
                    checkY=y;
                    checkX=x;
                }else{
                    checkY=checkX=-1;
                }
                break;
            default:
                checkY=checkX=-1;
        }
        // 確保這個落點是合理的，並且 getTouchPointSync 正在等

        invalidate();
        return true;
    }
    public void reset(){
        game.reset();
        invalidate();
    }
}
