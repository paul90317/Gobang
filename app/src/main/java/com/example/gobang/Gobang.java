package com.example.gobang;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class Gobang {
    public Gobang(){
        chessMap=new int[13][13];
        moves =new ArrayList<>();
    }
    public int getRound(){
        return moves.size();
    }
    public int getChessColor(int x,int y){
        return chessMap[x][y];
    }
    private final int[][] chessMap;
    private final List<byte []> moves;
    public byte[] getLastMove(){
        if(moves.size()>0)
            return moves.get(moves.size() - 1);
        return new byte[]{-1,-1};
    }
    /* 根據 round 下一子，回傳勝利方顏色，沒人勝利則傳 0，滿子和局則回傳灰色*/
    /*黑色值: -16777216，白色值: -1*/
    public int placeChess(byte x, byte y) {
        if (moves.size() % 2 == 0) {
            chessMap[x][y] = Color.BLACK;
        } else {
            chessMap[x][y] = Color.WHITE;
        }
        moves.add(new byte[]{x,y});
        if(moves.size()==169){
            return Color.GRAY;
        }
        int l,r;
        for(l=0;x+l>=0&&chessMap[x+l][y]==chessMap[x][y];l--);
        for(r=0;x+r<13&&chessMap[x+r][y]==chessMap[x][y];r++);
        if(r-l>5)
            return chessMap[x][y];
        for(l=0;y+l>=0&&chessMap[x][y+l]==chessMap[x][y];l--);
        for(r=0;y+r<13&&chessMap[x][y+r]==chessMap[x][y];r++);
        if(r-l>5)
            return chessMap[x][y];
        for(l=0;x+l>=0&&y+l>=0&&chessMap[x+l][y+l]==chessMap[x][y];l--);
        for(r=0;x+r<13&&y+r<13&&chessMap[x+r][y+r]==chessMap[x][y];r++);
        if(r-l>5)
            return chessMap[x][y];
        for(l=0;x+l>=0&&y-l<13&&chessMap[x+l][y-l]==chessMap[x][y];l--);
        for(r=0;x+r<13&&y-r>=0&&chessMap[x+r][y-r]==chessMap[x][y];r++);
        if(r-l>5)
            return chessMap[x][y];
        return 0;
    }
    /* 悔棋，悔到爽*/
    public int repentChess() {
        if(moves.size()==0)
            return -1;
        byte [] last= moves.get(moves.size() - 1);
        chessMap[last[0]][last[1]] = 0;
        moves.remove(last);
        return 0;
    }
    public void reset(){
        for(int i=0;i<13;i++){
            for(int j=0;j<13;j++){
                chessMap[i][j]=0;
            }
        }
        moves.clear();
    }
}
