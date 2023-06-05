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

    public int AIPlaceChess() {
        if(getRound()==0)
            return placeChess((byte) (15>>1), (byte) (15>>1));
        return alphaBetaPlaceChess(1);
    }

    private class Strategy{
        public byte x,y;
        public int s;
        public Strategy(byte x,byte y,int s){
            this.x=x;
            this.y=y;
            this.s=s;
        }
    }
    static private final int[] scoreMap=new int[]{0,10,100,1000,10000,1000000};
    private int alphaBeta(int deep, int alpha, int beta, boolean turn){
        if(deep<=0)
            return getScore();
        int temp;
        if(!turn){
            for(byte x=0;x<15;x++){
                for(byte y=0;y<15;y++){
                    if(chessMap[x][y]==0){
                        chessMap[x][y]=Color.BLACK;
                        temp= alphaBeta(deep-1,alpha,beta,true);
                        chessMap[x][y]=0;
                        if(temp>alpha)
                            alpha=temp;
                        if(alpha>=beta)
                            break;
                    }
                }
            }
            return alpha;
        }else{
            for(byte x=0;x<15;x++){
                for(byte y=0;y<15;y++){
                    if(chessMap[x][y]==0){
                        chessMap[x][y]=Color.WHITE;
                        temp= alphaBeta(deep-1,alpha,beta,false);
                        chessMap[x][y]=0;
                        if(temp<beta)
                            beta=temp;
                        if(alpha>=beta)
                            break;
                    }
                }
            }
            return beta;
        }
    }
    private int alphaBeta(int deep, boolean turn){
        return alphaBeta(deep, Integer.MIN_VALUE,Integer.MAX_VALUE,turn);
    }
    private int alphaBetaPlaceChess(int deep){
        Strategy temp;
        Strategy alpha=new Strategy((byte)-1,(byte)-1,Integer.MIN_VALUE) ;
        Strategy beta=new Strategy((byte)-1,(byte)-1,Integer.MAX_VALUE) ;
        if(getRound()%2==0){
            for(byte x=0;x<15;x++){
                for(byte y=0;y<15;y++){
                    if(chessMap[x][y]==0){
                        chessMap[x][y]=Color.BLACK;
                        temp=new Strategy(x,y, alphaBeta(deep,alpha.s,beta.s,true));
                        chessMap[x][y]=0;
                        if(temp.s>alpha.s)
                            alpha=temp;
                        if(alpha.s == beta.s)
                            break;
                    }
                }
            }
            return placeChess(alpha.x,alpha.y);
        }else{
            for(byte x=0;x<15;x++){
                for(byte y=0;y<15;y++){
                    if(chessMap[x][y]==0){
                        chessMap[x][y]=Color.WHITE;
                        temp=new Strategy(x,y, alphaBeta(deep,alpha.s,beta.s,false));
                        chessMap[x][y]=0;
                        if(temp.s<beta.s)
                            beta=temp;
                        if(alpha.s == beta.s)
                            break;
                    }
                }
            }
            return placeChess(beta.x,beta.y);
        }
    }
    private int gameTree(int deep, boolean turn){
        if(deep<=0)
            return 0;
        List<Strategy> candidates=new ArrayList<>();
        if(!turn){
            for(byte x=0;x<15;x++){
                for(byte y=0;y<15;y++){
                    if(chessMap[x][y]==0){
                        chessMap[x][y]=Color.BLACK;
                        candidates.add(new Strategy(x,y,alphaBeta(1,true)));
                        chessMap[x][y]=0;
                    }
                }
            }
            candidates.sort(dec);
            Strategy s=candidates.get(0);
            chessMap[s.x][s.y]=Color.BLACK;
            int best=s.s+ gameTree(deep-1,true);
            chessMap[s.x][s.y]=0;
            for(int i=1;i<5&&i<candidates.size();i++){
                s=candidates.get(i);
                chessMap[s.x][s.y]=Color.BLACK;
                int temp=s.s+ gameTree(deep-1,true);
                if(temp>best)
                    best=temp;
                chessMap[s.x][s.y]=0;
            }
            return best*4/5;
        }else{
            for(byte x=0;x<15;x++){
                for(byte y=0;y<15;y++){
                    if(chessMap[x][y]==0){
                        chessMap[x][y]=Color.WHITE;
                        candidates.add(new Strategy(x,y,alphaBeta(1,false)));
                        chessMap[x][y]=0;
                    }
                }
            }
            candidates.sort(inc);
            Strategy s=candidates.get(0);
            chessMap[s.x][s.y]=Color.WHITE;
            int best=s.s+ gameTree(2,false);
            chessMap[s.x][s.y]=0;
            for(int i=1;i<5&&i<candidates.size();i++){
                s=candidates.get(i);
                chessMap[s.x][s.y]=Color.WHITE;
                int temp=s.s+ gameTree(2,false);
                if(temp<best)
                    best=temp;
                chessMap[s.x][s.y]=0;
            }
            return best*4/5;
        }
    }
    private int gameTreePlaceChess(){
        List<Strategy> candidates=new ArrayList<>();
        if(getRound()%2==0){
            for(byte x=0;x<15;x++){
                for(byte y=0;y<15;y++){
                    if(chessMap[x][y]==0){
                        chessMap[x][y]=Color.BLACK;
                        candidates.add(new Strategy(x,y,alphaBeta(1,true)));
                        chessMap[x][y]=0;
                    }
                }
            }
            candidates.sort(dec);
            Strategy s=candidates.get(0);
            chessMap[s.x][s.y]=Color.BLACK;
            s.s+= alphaBeta(2,true);
            Strategy best=s;
            chessMap[s.x][s.y]=0;
            for(int i=1;i<5&&i<candidates.size();i++){
                s=candidates.get(i);
                chessMap[s.x][s.y]=Color.BLACK;
                s.s+= alphaBeta(2,true);
                if(s.s>best.s)
                    best=s;
                chessMap[s.x][s.y]=0;
            }
            return placeChess(best.x,best.y);
        }else{
            for(byte x=0;x<15;x++){
                for(byte y=0;y<15;y++){
                    if(chessMap[x][y]==0){
                        chessMap[x][y]=Color.WHITE;
                        candidates.add(new Strategy(x,y,alphaBeta(1,false)));
                        chessMap[x][y]=0;
                    }
                }
            }
            candidates.sort(inc);
            Strategy s=candidates.get(0);
            chessMap[s.x][s.y]=Color.WHITE;
            s.s+= alphaBeta(2,false);
            Strategy best=s;
            chessMap[s.x][s.y]=0;
            for(int i=1;i<5&&i<candidates.size();i++){
                s=candidates.get(i);
                chessMap[s.x][s.y]=Color.WHITE;
                s.s+= alphaBeta(2,false);
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
