package com.example.gobang;

import android.graphics.Color;

import java.util.*;

public class Gobang {
    public Gobang(){
        chessMap=new int[15][15];
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
        for(r=0;x+r<15&&chessMap[x+r][y]==chessMap[x][y];r++);
        if(r-l>5)
            return chessMap[x][y];
        for(l=0;y+l>=0&&chessMap[x][y+l]==chessMap[x][y];l--);
        for(r=0;y+r<15&&chessMap[x][y+r]==chessMap[x][y];r++);
        if(r-l>5)
            return chessMap[x][y];
        for(l=0;x+l>=0&&y+l>=0&&chessMap[x+l][y+l]==chessMap[x][y];l--);
        for(r=0;x+r<15&&y+r<15&&chessMap[x+r][y+r]==chessMap[x][y];r++);
        if(r-l>5)
            return chessMap[x][y];
        for(l=0;x+l>=0&&y-l<15&&chessMap[x+l][y-l]==chessMap[x][y];l--);
        for(r=0;x+r<15&&y-r>=0&&chessMap[x+r][y-r]==chessMap[x][y];r++);
        if(r-l>5)
            return chessMap[x][y];
        return 0;
    }
    /* 悔棋，悔到爽*/
    public boolean repentChess() {
        if(moves.size()==0)
            return false;
        byte [] last= moves.get(moves.size() - 1);
        chessMap[last[0]][last[1]] = 0;
        moves.remove(last);
        return true;
    }
    private int getScore(){
        int score=0;
        for(int i=0;i+4<15;i++){
            for(int j=0;j<15;j++){
                int count=0;
                for(int k=0;k<5;k++){
                    if(chessMap[i+k][j]==Color.BLACK){
                        if(count<0){
                            count=0;
                            break;
                        }
                        count++;
                    }
                    if(chessMap[i+k][j]==Color.WHITE){
                        if(count>0){
                            count=0;
                            break;
                        }
                        count--;
                    }
                }
                if(count<0){
                    score-=scoreMap[-count];
                }else{
                    score+=scoreMap[count];
                }
            }
        }
        for(int i=0;i<15;i++){
            for(int j=0;j+4<15;j++){
                int count=0;
                for(int k=0;k<5;k++){
                    if(chessMap[i][j+k]==Color.BLACK){
                        if(count<0){
                            count=0;
                            break;
                        }
                        count++;
                    }
                    if(chessMap[i][j+k]==Color.WHITE){
                        if(count>0){
                            count=0;
                            break;
                        }
                        count--;
                    }
                }
                if(count<0){
                    score-=scoreMap[-count];
                }else{
                    score+=scoreMap[count];
                }
            }
        }
        for(int i=0;i+4<15;i++){
            for(int j=0;j+4<15;j++){
                int count=0;
                for(int k=0;k<5;k++){
                    if(chessMap[i+k][j+k]==Color.BLACK){
                        if(count<0){
                            count=0;
                            break;
                        }
                        count++;
                    }
                    if(chessMap[i+k][j+k]==Color.WHITE){
                        if(count>0){
                            count=0;
                            break;
                        }
                        count--;
                    }
                }
                if(count<0){
                    score-=scoreMap[-count];
                }else{
                    score+=scoreMap[count];
                }
            }
        }
        for(int i=0;i+4<15;i++){
            for(int j=4;j<15;j++){
                int count=0;
                for(int k=0;k<5;k++){
                    if(chessMap[i+k][j-k]==Color.BLACK){
                        if(count<0){
                            count=0;
                            break;
                        }
                        count++;
                    }
                    if(chessMap[i+k][j-k]==Color.WHITE){
                        if(count>0){
                            count=0;
                            break;
                        }
                        count--;
                    }
                }
                if(count<0){
                    score-=scoreMap[-count];
                }else{
                    score+=scoreMap[count];
                }
            }
        }
        return score;
    }
    private final static Comparator<Strategy> inc=(a,b)->a.s-b.s;
    private final static Comparator<Strategy> dec=(a,b)->b.s-a.s;
    private class Strategy{
        public byte x,y;
        public int s;
        public Strategy(byte x,byte y,int s){
            this.x=x;
            this.y=y;
            this.s=s;
        }
    }
    private static final int children=5;
    private static final int a=6,b=7;
    private static final int deep=5;
    static private final int[] scoreMap=new int[]{0,10,100,1000,10000,1000000};
    private int AIScore(int deep, boolean turn){
        if(deep<=0)
            return 0;
        List<Strategy> candidates=new ArrayList<>();
        if(!turn){
            for(byte i=0;i<15;i++){
                for(byte j=0;j<15;j++){
                    if(chessMap[i][j]==0){
                        chessMap[i][j]=Color.BLACK;
                        candidates.add(new Strategy(i,j,getScore()));
                        chessMap[i][j]=0;
                    }
                }
            }
            candidates.sort(dec);
            Strategy s=candidates.get(0);
            chessMap[s.x][s.y]=Color.BLACK;
            int best=s.s+AIScore(deep-1,true);
            chessMap[s.x][s.y]=0;
            for(int i=1;i<children&&i<candidates.size();i++){
                s=candidates.get(i);
                chessMap[s.x][s.y]=Color.BLACK;
                int temp=s.s+AIScore(deep-1,true);
                if(temp>best)
                    best=temp;
                chessMap[s.x][s.y]=0;
            }
            return best*a/b;
        }else{
            for(byte i=0;i<15;i++){
                for(byte j=0;j<15;j++){
                    if(chessMap[i][j]==0){
                        chessMap[i][j]=Color.WHITE;
                        candidates.add(new Strategy(i,j,getScore()));
                        chessMap[i][j]=0;
                    }
                }
            }
            candidates.sort(inc);
            Strategy s=candidates.get(0);
            chessMap[s.x][s.y]=Color.WHITE;
            int best=s.s+AIScore(deep-1,false);
            chessMap[s.x][s.y]=0;
            for(int i=1;i<children&&i<candidates.size();i++){
                s=candidates.get(i);
                chessMap[s.x][s.y]=Color.WHITE;
                int temp=s.s+AIScore(deep-1,false);
                if(temp<best)
                    best=temp;
                chessMap[s.x][s.y]=0;
            }
            return best*a/b;
        }
    }
    public int AIPlaceChess(){
        if(deep<=0)
            return 0;
        List<Strategy> candidates=new ArrayList<>();
        if(getRound()%2==0){
            for(byte i=0;i<15;i++){
                for(byte j=0;j<15;j++){
                    if(chessMap[i][j]==0){
                        chessMap[i][j]=Color.BLACK;
                        candidates.add(new Strategy(i,j,getScore()));
                        chessMap[i][j]=0;
                    }
                }
            }
            candidates.sort(dec);
            Strategy s=candidates.get(0);
            chessMap[s.x][s.y]=Color.BLACK;
            s.s+=AIScore(deep-1,true);
            Strategy best=s;
            chessMap[s.x][s.y]=0;
            for(int i=1;i<5&&i<candidates.size();i++){
                s=candidates.get(i);
                chessMap[s.x][s.y]=Color.BLACK;
                s.s+=AIScore(deep-1,true);
                if(s.s>best.s)
                    best=s;
                chessMap[s.x][s.y]=0;
            }
            return placeChess(best.x,best.y);
        }else{
            for(byte i=0;i<15;i++){
                for(byte j=0;j<15;j++){
                    if(chessMap[i][j]==0){
                        chessMap[i][j]=Color.WHITE;
                        candidates.add(new Strategy(i,j,getScore()));
                        chessMap[i][j]=0;
                    }
                }
            }
            candidates.sort(inc);
            Strategy s=candidates.get(0);
            chessMap[s.x][s.y]=Color.WHITE;
            s.s+=AIScore(deep-1,false);
            Strategy best=s;
            chessMap[s.x][s.y]=0;
            for(int i=1;i<5&&i<candidates.size();i++){
                s=candidates.get(i);
                chessMap[s.x][s.y]=Color.WHITE;
                s.s+=AIScore(deep-1,false);
                if(s.s<best.s)
                    best=s;
                chessMap[s.x][s.y]=0;
            }
            return placeChess(best.x,best.y);
        }
    }
    public void reset(){
        for(int i=0;i<15;i++){
            for(int j=0;j<15;j++){
                chessMap[i][j]=0;
            }
        }
        moves.clear();
    }
}
