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
    private int localHeuristic(int x,int y){
        //斷定 x,y 為 0
        int k,K,count,score=0;

        k=-4;
        if(x+k<0)
            k=-x;
        K=5;
        if(x+K>15)
            K=15-x;
        for(;k+4<K;k++){
            count=0;
            for(int l=0;l<5;l++){
                if(chessMap[x+k+l][y]==Color.BLACK){
                    if(count<0){
                        count=0;
                        break;
                    }
                    count++;
                }
                if(chessMap[x+k+l][y]==Color.WHITE){
                    if(count>0){
                        count=0;
                        break;
                    }
                    count--;
                }
            }
            if(count<0)
                score-=scoreMap[-count];
            else
                score+=scoreMap[count];
        }


        k=-4;
        if(y+k<0)
            k=-y;
        K=5;
        if(y+K>15)
            K=15-y;
        for(;k+4<K;k++){
            count=0;
            for(int l=0;l<5;l++){
                if(chessMap[x][y+k+l]==Color.BLACK){
                    if(count<0){
                        count=0;
                        break;
                    }
                    count++;
                }
                if(chessMap[x][y+k+l]==Color.WHITE){
                    if(count>0){
                        count=0;
                        break;
                    }
                    count--;
                }
            }
            if(count<0)
                score-=scoreMap[-count];
            else
                score+=scoreMap[count];
        }

        k=-4;
        if(y+k<0)
            k=-y;
        if(x+k<0)
            k=-x;
        K=5;
        if(y+K>15)
            K=15-y;
        if(x+K>15)
            K=15-x;
        for(;k+4<K;k++){
            count=0;
            for(int l=0;l<5;l++){
                if(chessMap[x+k+l][y+k+l]==Color.BLACK){
                    if(count<0){
                        count=0;
                        break;
                    }
                    count++;
                }
                if(chessMap[x+k+l][y+k+l]==Color.WHITE){
                    if(count>0){
                        count=0;
                        break;
                    }
                    count--;
                }
            }
            if(count<0)
                score-=scoreMap[-count];
            else
                score+=scoreMap[count];
        }


        k=-4;
        if(x+k<0)
            k=-x;
        if(y-k>15-1)
            k=y-15+1;
        K=5;
        if(x+K>15)
            K=15-x;
        if(y-(K-1)<0)
            K=y+1;
        for(;k+4<K;k++){
            count=0;
            for(int l=0;l<5;l++){
                if(chessMap[x+k+l][y-k-l]==Color.BLACK){
                    if(count<0){
                        count=0;
                        break;
                    }
                    count++;
                }
                if(chessMap[x+k+l][y-k-l]==Color.WHITE){
                    if(count>0){
                        count=0;
                        break;
                    }
                    count--;
                }
            }
            if(count<0)
                score-=scoreMap[-count];
            else
                score+=scoreMap[count];
        }

        return score;
    }
    private int heuristicDiffAndPlace(int x,int y,int color){
        //斷定 x,y 為 0
        int score=-localHeuristic(x,y);
        chessMap[x][y]=color;
        score+=localHeuristic(x,y);
        return score;
    }

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
    private int alphaBeta(int deep, int alpha, int beta, boolean turn,int score){
        if(deep<=0)
            return score;
        int temp;
        if(!turn){
            for(byte x=0;x<15;x++){
                for(byte y=0;y<15;y++){
                    if(chessMap[x][y]==0){
                        temp= alphaBeta(deep-1,alpha,beta,true,
                                score+heuristicDiffAndPlace(x,y,Color.BLACK));
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
                        temp= alphaBeta(deep-1,alpha,beta,false
                        ,score+heuristicDiffAndPlace(x,y,Color.WHITE));
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

    private int alphaBetaPlaceChess(int deep){
        Strategy temp;
        Strategy alpha=new Strategy((byte)-1,(byte)-1,Integer.MIN_VALUE) ;
        Strategy beta=new Strategy((byte)-1,(byte)-1,Integer.MAX_VALUE) ;
        if(getRound()%2==0){
            for(byte x=0;x<15;x++){
                for(byte y=0;y<15;y++){
                    if(chessMap[x][y]==0){
                        temp=new Strategy(x,y, alphaBeta(deep,alpha.s,beta.s,true
                            ,heuristicDiffAndPlace(x,y,Color.BLACK)));
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
                        temp=new Strategy(x,y, alphaBeta(deep,alpha.s,beta.s,false
                                ,heuristicDiffAndPlace(x,y,Color.WHITE)));
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
    public void reset(){
        for(int i=0;i<15;i++){
            for(int j=0;j<15;j++){
                chessMap[i][j]=0;
            }
        }
        moves.clear();
    }
}
