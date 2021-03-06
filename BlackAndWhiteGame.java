import java.util.*;
import java.lang.*;
import java.math.*;
import java.io.*;
public class BlackAndWhiteGame
{
    String[] makeConnected(String[] board) {
        int maxLengthMove=board.length;
        long startTime=System.nanoTime();
        String[] ans=new String[0];
        boolean[][] b=new boolean[board.length][board[0].length()];
        for(int i=0;i<board.length;i++) {
            for(int j=0;j<board[i].length();j++) {
                if(board[i].charAt(j)=='X')
                    b[i][j]=true;
            }
        }
        boolean[][] oldB=new boolean[board.length][board[0].length()];
        for(int i=0;i<board.length;i++) for(int j=0;j<board[0].length();j++) oldB[i][j]=b[i][j];
        //beginning colors
        boolean[][] tempB=new boolean[board.length][board[0].length()];
        for(int i=0;i<b.length;i++) {
            for(int j=0;j<b[i].length;j++) {
                tempB[i][j]=b[i][j];
            }
        }
        int[][] colors=color(tempB);
        int maxNum=0;
        for(int i=0;i<b.length;i++) for(int j=0;j<b.length;j++) maxNum=Math.max(maxNum,colors[i][j]);
        int[] colorNums=new int[maxNum+1];
        for(int i=0;i<b.length;i++) for(int j=0;j<b.length;j++) colorNums[colors[i][j]]++;
        int maxColor=1;
        for(int i=1;i<colorNums.length;i++) {
            if(colorNums[maxColor]<colorNums[i]) maxColor=i;
        }
        int checkNum=colorNums[maxColor];
        ArrayList<String> beginning=new ArrayList<String>();
        int totalCount=0;
        for(int i=0;i<b.length;i++) for(int j=0;j<b.length;j++) if(b[i][j]) totalCount++;
        double timer=5;
        boolean anyChanges=true;
        int counter=1;
        while(anyChanges&&((System.nanoTime()-startTime)/Math.pow(10,9))<timer) {
            ArrayList<String> maxColors=new ArrayList<String>();
            ArrayList<ArrayList<String>> possibleMoves=new ArrayList<ArrayList<String>>();
            possibleMoves.add(maxColors);
            boolean improved=false;
            while(((System.nanoTime()-startTime)/Math.pow(10,9))<timer&&possibleMoves.size()>0&&!improved) {
                ArrayList<String> currMoves=possibleMoves.remove(0);
                boolean[][] tempB2=new boolean[b.length][b.length];
                for(int i=0;i<b.length;i++) for(int j=0;j<b.length;j++) tempB2[i][j]=b[i][j];
                boolean totalTileWhite=false;
                for(int i=0;i<currMoves.size();i++) {
                    totalTileWhite=move(tempB2,currMoves.get(i),totalTileWhite);
                }
                if(!totalTileWhite) {
                    colors=colorSome(tempB2);
                    maxNum=0;
                    for(int i=0;i<b.length;i++) for(int j=0;j<b.length;j++) maxNum=Math.max(maxNum,colors[i][j]);
                    colorNums=new int[maxNum+1];
                    for(int i=0;i<b.length;i++) for(int j=0;j<b.length;j++) colorNums[colors[i][j]]++;
                    maxColor=1;
                    for(int i=1;i<colorNums.length;i++) {
                        if(colorNums[maxColor]<colorNums[i]) maxColor=i;
                    }
                    if(colorNums[maxColor]>checkNum) {
                        checkNum=colorNums[maxColor];
                        maxColors=currMoves;
                        improved=true;
                    }
                }
                if(!improved&&currMoves.size()<counter) {
                    for(int i=0;i<b.length;i++) {
                        if(count(tempB2[i])>0) {
                            ArrayList<String> currMoves2=new ArrayList<String>();
                            for(int j=0;j<currMoves.size();j++) currMoves2.add(currMoves.get(j));
                            for(int j=0;j<counter;j++) currMoves2.add(i+" -1");
                            possibleMoves.add(currMoves2);
                            currMoves2=new ArrayList<String>();
                            for(int j=0;j<currMoves.size();j++) currMoves2.add(currMoves.get(j));
                            for(int j=0;j<counter;j++) currMoves2.add(i+" "+b.length);
                            possibleMoves.add(currMoves2);
                        }
                        if(count(tempB2,i)>0) {
                            ArrayList<String> currMoves2=new ArrayList<String>();
                            for(int j=0;j<currMoves.size();j++) currMoves2.add(currMoves.get(j));
                            for(int j=0;j<counter;j++) currMoves2.add("-1 "+i);
                            possibleMoves.add(currMoves2);
                            currMoves2=new ArrayList<String>();
                            for(int j=0;j<currMoves.size();j++) currMoves2.add(currMoves.get(j));
                            for(int j=0;j<counter;j++) currMoves2.add(b.length+" "+i);
                            possibleMoves.add(currMoves2);
                        }
                    }
                }
            }
            //DFS for best set of 1-3 moves that changes and improves maxColorNum
            boolean tempBool=false;
            for(int i=0;i<maxColors.size();i++) {
                beginning.add(maxColors.get(i));
                tempBool=move(b,maxColors.get(i),tempBool);
            }
            if(maxColors.size()>0&&counter>1) counter=1;
            else if(maxColors.size()==0) counter++;
            if(counter>maxLengthMove) break;
        }
        while(anyChanges&&((System.nanoTime()-startTime)/Math.pow(10,9))<timer) {
            ArrayList<String> maxColors=new ArrayList<String>();
            ArrayList<ArrayList<String>> possibleMoves=new ArrayList<ArrayList<String>>();
            possibleMoves.add(maxColors);
            boolean improved=false;
            while(((System.nanoTime()-startTime)/Math.pow(10,9))<timer&&possibleMoves.size()>0&&!improved) {
                ArrayList<String> currMoves=possibleMoves.remove(0);
                boolean[][] tempB2=new boolean[b.length][b.length];
                boolean addMove=true;
                for(int i=0;i<b.length;i++) for(int j=0;j<b.length;j++) tempB2[i][j]=b[i][j];
                boolean totalTileWhite=false;
                for(int i=0;i<currMoves.size();i++) totalTileWhite=move(tempB2,currMoves.get(i),totalTileWhite);
                if(!totalTileWhite) {
                    colors=colorSome(tempB2);
                    maxNum=0;
                    for(int i=0;i<b.length;i++) for(int j=0;j<b.length;j++) maxNum=Math.max(maxNum,colors[i][j]);
                    colorNums=new int[maxNum+1];
                    for(int i=0;i<b.length;i++) for(int j=0;j<b.length;j++) colorNums[colors[i][j]]++;
                    maxColor=1;
                    for(int i=1;i<colorNums.length;i++) {
                        if(colorNums[maxColor]<colorNums[i]) maxColor=i;
                    }
                    if(colorNums[maxColor]<checkNum) addMove=true;
                    if(colorNums[maxColor]>checkNum) {
                        checkNum=colorNums[maxColor];
                        maxColors=currMoves;
                        improved=true;
                    }
                }
                if(addMove&&currMoves.size()<counter) {
                    for(int i=0;i<b.length;i++) {
                        if(count(tempB2[i])>0) {
                            ArrayList<String> currMoves2=new ArrayList<String>();
                            for(int j=0;j<currMoves.size();j++) currMoves2.add(currMoves.get(j));
                            currMoves2.add(i+" -1");
                            possibleMoves.add(currMoves2);
                            currMoves2=new ArrayList<String>();
                            for(int j=0;j<currMoves.size();j++) currMoves2.add(currMoves.get(j));
                            currMoves2.add(i+" "+b.length);
                            possibleMoves.add(currMoves2);
                        }
                        if(count(tempB2,i)>0) {
                            ArrayList<String> currMoves2=new ArrayList<String>();
                            for(int j=0;j<currMoves.size();j++) currMoves2.add(currMoves.get(j));
                            currMoves2.add("-1 "+i);
                            possibleMoves.add(currMoves2);
                            currMoves2=new ArrayList<String>();
                            for(int j=0;j<currMoves.size();j++) currMoves2.add(currMoves.get(j));
                            currMoves2.add(b.length+" "+i);
                            possibleMoves.add(currMoves2);
                        }
                    }
                }
            }
            //DFS for best set of 1-3 moves that changes and improves maxColorNum
            boolean tempBool=false;
            for(int i=0;i<maxColors.size();i++) {
                beginning.add(maxColors.get(i));
                tempBool=move(b,maxColors.get(i),tempBool);
            }
            if(maxColors.size()>0&&counter>1) counter=1;
            else if(maxColors.size()==0) counter++;
            if(counter>b.length) break;
        }
        if(done(b,false)) {
            ans=new String[beginning.size()];
            for(int i=0;i<beginning.size();i++) ans[i]=beginning.get(i);
            return ans;
        }
        String[] temp;
        try {
            int a5=-1;
            while((System.nanoTime()-startTime)/Math.pow(10,9)<7) {
                a5++;
                if(a5>b.length/2) break;
                int a1=a5;
                int a2=a5;
                int a3=a5;
                int a4=a5;
        try {
                tempB=new boolean[board.length][board[0].length()];
                for(int i=0;i<b.length;i++) {
                    for(int j=0;j<b[i].length;j++) {
                        tempB[i][j]=oldB[i][j];
                    }
                }
                temp=makeConnect5(tempB,a1,a2,a3,a4);
                if(temp.length>0&&temp.length<ans.length||ans.length<=beginning.size()) {
                    ans=temp;
                }
                tempB=new boolean[board.length][board[0].length()];
                for(int i=0;i<b.length;i++) {
                    for(int j=0;j<b[i].length;j++) {
                        tempB[j][i]=oldB[i][j];
                    }
                }
                temp=makeConnect5(tempB,a1,a2,a3,a4);
                if(temp.length>0&&temp.length<ans.length||ans.length<=beginning.size()) {
                    for(int i=0;i<temp.length;i++) {
                        String t="";
                        StringTokenizer st=new StringTokenizer(temp[i]);
                        t+=st.nextToken();
                        t=" "+t;
                        t=st.nextToken()+t;
                        temp[i]=t;
                    }
                    ans=temp;
                }
                tempB=new boolean[board.length][board[0].length()];
                for(int i=0;i<b.length;i++) {
                    for(int j=0;j<b[i].length;j++) {
                        tempB[i][b.length-j-1]=oldB[i][j];
                    }
                }
                temp=makeConnect5(tempB,a1,a2,a3,a4);
                if(temp.length>0&&temp.length<ans.length||ans.length<=beginning.size()) {
                    for(int i=0;i<temp.length;i++) {
                        String t="";
                        StringTokenizer st=new StringTokenizer(temp[i]);
                        t=st.nextToken();
                        t+=" ";
                        t+=""+(b.length-Integer.parseInt(st.nextToken())-1);
                        temp[i]=t;
                    }
                    ans=temp;
                }
                tempB=new boolean[board.length][board[0].length()];
                for(int i=0;i<b.length;i++) {
                    for(int j=0;j<b[i].length;j++) {
                        tempB[b.length-j-1][i]=oldB[i][j];
                    }
                }
                temp=makeConnect5(tempB,a1,a2,a3,a4);
                if(temp.length>0&&temp.length<ans.length||ans.length<=beginning.size()) {
                    for(int i=0;i<temp.length;i++) {
                        String t="";
                        StringTokenizer st=new StringTokenizer(temp[i]);
                        t=""+(b.length-Integer.parseInt(st.nextToken())-1);
                        t=" "+t;
                        t=st.nextToken()+t;
                        temp[i]=t;
                    }
                    ans=temp;
                }
                long totalTime=System.nanoTime()-startTime;
                if(totalTime/Math.pow(10,9)>7) {
                    String[] newAns=new String[ans.length];
                    for(int i=0;i<ans.length;i++) newAns[i]=ans[i];
                    return newAns;
                }
            } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed Zero");
        }
        try {
                tempB=new boolean[board.length][board[0].length()];
                for(int i=0;i<b.length;i++) {
                    for(int j=0;j<b[i].length;j++) {
                        tempB[i][j]=b[i][j];
                    }
                }
                temp=makeConnect5(tempB,a1,a2,a3,a4);
                if(temp.length>0&&temp.length+beginning.size()<ans.length||ans.length<=beginning.size()) {
                    ans=new String[beginning.size()+temp.length];
                    for(int i=0;i<beginning.size();i++) ans[i]=beginning.get(i);
                    for(int i=0;i<temp.length;i++) ans[i+beginning.size()]=temp[i];
                }
                tempB=new boolean[board.length][board[0].length()];
                for(int i=0;i<b.length;i++) {
                    for(int j=0;j<b[i].length;j++) {
                        tempB[j][i]=b[i][j];
                    }
                }
                temp=makeConnect5(tempB,a1,a2,a3,a4);
                if(temp.length>0&&temp.length+beginning.size()<ans.length||ans.length<=beginning.size()) {
                    for(int i=0;i<temp.length;i++) {
                        String t="";
                        StringTokenizer st=new StringTokenizer(temp[i]);
                        t+=st.nextToken();
                        t=" "+t;
                        t=st.nextToken()+t;
                        temp[i]=t;
                    }
                    ans=new String[beginning.size()+temp.length];
                    for(int i=0;i<beginning.size();i++) ans[i]=beginning.get(i);
                    for(int i=0;i<temp.length;i++) ans[i+beginning.size()]=temp[i];
                }
                tempB=new boolean[board.length][board[0].length()];
                for(int i=0;i<b.length;i++) {
                    for(int j=0;j<b[i].length;j++) {
                        tempB[i][b.length-j-1]=b[i][j];
                    }
                }
                temp=makeConnect5(tempB,a1,a2,a3,a4);
                if(temp.length>0&&temp.length+beginning.size()<ans.length||ans.length<=beginning.size()) {
                    for(int i=0;i<temp.length;i++) {
                        String t="";
                        StringTokenizer st=new StringTokenizer(temp[i]);
                        t=st.nextToken();
                        t+=" ";
                        t+=""+(b.length-Integer.parseInt(st.nextToken())-1);
                        temp[i]=t;
                    }
                    ans=new String[beginning.size()+temp.length];
                    for(int i=0;i<beginning.size();i++) ans[i]=beginning.get(i);
                    for(int i=0;i<temp.length;i++) ans[i+beginning.size()]=temp[i];
                }
                tempB=new boolean[board.length][board[0].length()];
                for(int i=0;i<b.length;i++) {
                    for(int j=0;j<b[i].length;j++) {
                        tempB[b.length-j-1][i]=b[i][j];
                    }
                }
                temp=makeConnect5(tempB,a1,a2,a3,a4);
                if(temp.length>0&&temp.length+beginning.size()<ans.length||ans.length<=beginning.size()) {
                    for(int i=0;i<temp.length;i++) {
                        String t="";
                        StringTokenizer st=new StringTokenizer(temp[i]);
                        t=""+(b.length-Integer.parseInt(st.nextToken())-1);
                        t=" "+t;
                        t=st.nextToken()+t;
                        temp[i]=t;
                    }
                    ans=new String[beginning.size()+temp.length];
                    for(int i=0;i<beginning.size();i++) ans[i]=beginning.get(i);
                    for(int i=0;i<temp.length;i++) ans[i+beginning.size()]=temp[i];
                }
                long totalTime=System.nanoTime()-startTime;
                if(totalTime/Math.pow(10,9)>7) {
                    String[] newAns=new String[ans.length];
                    for(int i=0;i<ans.length;i++) newAns[i]=ans[i];
                    return newAns;
                }
                } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed Zero");
        }
                }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed Zero");
        }
        String[] newAns=new String[ans.length];
        for(int i=0;i<ans.length;i++) newAns[i]=ans[i];
        return newAns;
    }
    String[] makeConnect5(boolean[][] b,int diff1,int diff2,int diff3,int diff4) {
        boolean[][] newB1=new boolean[b.length][b.length];
        for(int i=0;i<b.length;i++) for(int j=0;j<b.length;j++) newB1[i][j]=b[i][j];
        String[] temp1=makeConnect6(newB1,diff1,diff2,diff3,diff4);
        boolean[][] newB2=new boolean[b.length][b.length];
        for(int i=0;i<b.length;i++) for(int j=0;j<b.length;j++) newB2[i][j]=b[i][j];
        String[] temp2=makeConnect7(newB2,diff1,diff2,diff3,diff4);
        if(temp1.length>temp2.length&&temp2.length>0&&temp1.length>0||temp1.length==0) {
            return temp2;
        }
        return temp1;
    }
    String[] makeConnect7(boolean[][] b,int diff1,int diff2,int diff3,int diff4) {
        if(done(b,false)) return new String[0];
        long startTime=System.nanoTime();
        boolean nextTileWhite=false;
        ArrayList<String> moves=new ArrayList<String>();
        int mostSquares=0;
        int[] areas=new int[4];
        int countTotal=0;
        for(int a=0;a<b.length;a++) {
            for(int c=0;c<b.length;c++) {
                if(b[a][c]) countTotal++;
            }
        }
        int[][] colors=color(b);
        int maxNum=0;
        for(int i=0;i<b.length;i++) for(int j=0;j<b.length;j++) maxNum=Math.max(maxNum,colors[i][j]);
        int[] colorNums=new int[maxNum+1];
        for(int i=0;i<b.length;i++) for(int j=0;j<b.length;j++) colorNums[colors[i][j]]++;
        int maxColor=1;
        for(int i=1;i<colorNums.length;i++) {
            if(colorNums[maxColor]<colorNums[i]) maxColor=i;
        }
        int smallX=b.length;
        int bigX=0;
        int smallY=b.length;
        int bigY=0;
        for(int i=0;i<b.length;i++) for(int j=0;j<b.length;j++) {
            if(colors[i][j]==maxColor) {
                smallX=Math.min(smallX,i);
                bigX=Math.max(bigX,i);
                smallY=Math.min(smallY,j);
                bigY=Math.max(bigY,j);
            }
        }
        smallX+=diff1;
        smallY+=diff2;
        bigX-=diff3;
        bigY-=diff4;
        if(bigX<=smallX) return new String[0];
        if(bigY<=smallY) return new String[0];
        areas[0]=smallX;
        areas[1]=smallY;
        areas[2]=bigX-smallX+1;
        areas[3]=bigY-smallY+1;
        int originala0=areas[0];
        int originala1=areas[1];
        int originala2=areas[2];
        int originala3=areas[3];
        //TODO move all squares in the big area that aren't connected out
        if(areas[2]<b.length-10) {
            for(int i=areas[0];i<areas[0]+areas[2];i++) {
                int min=b.length;
                int max=0;
                ArrayList<Integer> points=new ArrayList<Integer>();
                for(int j=originala1;j<originala3+originala1;j++) {
                    if(b[i][j]&&colors[i][j]!=maxColor) {
                        min=Math.min(j,min);
                        max=Math.max(j,max);
                        points.add(j);
                    }
                }
                int count=0;
                int added=0;
                if(min>=(originala1+originala3)/2&&areas[1]+areas[3]-min-1<Math.min(b.length-areas[2],10)) {
                    for(int j=0;j<=areas[1]+areas[3]-min-1-added;j++) {
                        int c1=0;
                        moves.add(i+" -1");
                        nextTileWhite=move(b,i+" -1",nextTileWhite);
                        if(points.contains(areas[1]+areas[3]-j-1-added)) {
                            while(c1<b.length*4&&nextTileWhite) {
                                c1++;
                                moves.add(i+" -1");
                                nextTileWhite=move(b,i+" -1",nextTileWhite);
                                count++;
                                if(count>=b.length) count-=b.length;
                            }
                            if(areas[1]+areas[3]+count<b.length) {
                                while(c1<b.length*4&&(nextTileWhite||b[i][areas[1]+areas[3]+count])) {
                                    c1++;
                                    if(nextTileWhite) {
                                        if((areas[1]+areas[3]+count+1)<b.length) {
                                            moves.add("-1 "+(areas[1]+areas[3]+count+1));
                                            nextTileWhite=move(b,"-1 "+(areas[1]+areas[3]+count+1),nextTileWhite);
                                        } else {
                                            moves.add("-1 0");
                                            nextTileWhite=move(b,"-1 0",nextTileWhite);
                                        }
                                    } else {
                                        moves.add("-1 "+(areas[1]+areas[3]+count));
                                        nextTileWhite=move(b,"-1 "+(areas[1]+areas[3]+count),nextTileWhite);
                                    }
                                    while(areas[1]+areas[3]+count<b.length&&count(b,areas[1]+areas[3]+count)==b.length) {
                                        areas[3]++;
                                        added++;
                                    }
                                    while(areas[1]+areas[3]+count>=b.length&&count(b,areas[1]+areas[3]+count-b.length)==b.length) {
                                        areas[3]++;
                                        added++;
                                    }
                                }
                            } else {
                                while(c1<b.length*4&&(areas[1]+areas[3]+count-b.length-1<0||nextTileWhite)) {
                                    c1++;
                                    moves.add(i+" -1");
                                    nextTileWhite=move(b,i+" -1",nextTileWhite);
                                    count++;
                                    if(count>=b.length) count-=b.length;
                                }
                                while(c1<b.length*4&&(nextTileWhite||b[i][areas[1]+areas[3]+count-b.length-1])) {
                                    c1++;
                                    if(nextTileWhite) {
                                        if((areas[1]+areas[3]+count-b.length-2)>=0) {
                                            moves.add("-1 "+(areas[1]+areas[3]+count-b.length-2));
                                            nextTileWhite=move(b,"-1 "+(areas[1]+areas[3]+count-b.length-2),nextTileWhite);
                                        } else {
                                            moves.add("-1 "+(b.length-1));
                                            nextTileWhite=move(b,"-1 "+(b.length-1),nextTileWhite);
                                        }
                                    } else {
                                        moves.add("-1 "+(areas[1]+areas[3]+count-b.length-1));
                                        nextTileWhite=move(b,"-1 "+(areas[1]+areas[3]+count-b.length-1),nextTileWhite);
                                    }
                                    while(areas[1]+areas[3]+count-1<b.length&&count(b,areas[1]+areas[3]+count-1)==b.length) {
                                        areas[3]++;
                                        added++;
                                    }
                                    while(areas[1]+areas[3]+count-1>=b.length&&count(b,areas[1]+areas[3]+count-b.length-1)==b.length) {
                                        areas[3]++;
                                        added++;
                                    }
                                }
                            }
                        }
                    }
                    for(int k=0;k<=areas[1]+areas[3]-min-1-added+count;k++) {
                        moves.add(i+" "+b.length);
                        nextTileWhite=move(b,i+" "+b.length,nextTileWhite);
                    }
                } else if(max-areas[1]<Math.min(b.length-areas[2],10)) {
                    for(int j=0;j<=max-areas[1]+added;j++) {
                        int c1=0;
                        moves.add(i+" "+b.length);
                        nextTileWhite=move(b,i+" "+b.length,nextTileWhite);
                        if(points.contains(areas[1]+added+j)) {
                            while(c1<b.length*4&&nextTileWhite) {
                                c1++;
                                moves.add(i+" "+b.length);
                                nextTileWhite=move(b,i+" "+b.length,nextTileWhite);
                                count++;
                                if(count>=b.length) count-=b.length;
                            }
                            if(areas[1]-count-1>=0) {
                                while(c1<b.length*4&&(nextTileWhite||b[i][areas[1]-count-1])) {
                                    c1++;
                                    if(nextTileWhite) {
                                        if((areas[1]-count-2)>=0) {
                                            moves.add("-1 "+(areas[1]-count-2));
                                            nextTileWhite=move(b,"-1 "+(areas[1]-count-2),nextTileWhite);
                                        } else {
                                            moves.add("-1 "+(b.length-1));
                                            nextTileWhite=move(b,"-1 "+(b.length-1),nextTileWhite);
                                        }
                                    } else {
                                        moves.add("-1 "+(areas[1]-count-1));
                                        nextTileWhite=move(b,"-1 "+(areas[1]-count-1),nextTileWhite);
                                    }
                                    while(areas[1]-count-1>=0&&count(b,areas[1]-count-1)==b.length) {
                                        areas[1]--;
                                        added++;
                                    }
                                    while(areas[1]-count-1<0&&count(b,areas[1]-count-1+b.length)==b.length) {
                                        areas[1]--;
                                        added++;
                                    }
                                }
                            } else {
                                while(c1<b.length*4&&(areas[1]-count+b.length>=b.length||nextTileWhite)) {
                                    c1++;
                                    moves.add(i+" "+b.length);
                                    nextTileWhite=move(b,i+" "+b.length,nextTileWhite);
                                    count++;
                                    if(count>=b.length) count-=b.length;
                                }
                                while(c1<b.length*4&&(nextTileWhite||b[i][areas[1]-count+b.length])) {
                                    c1++;
                                    if(nextTileWhite) {
                                        if((areas[1]-count+b.length+1)<b.length) {
                                            moves.add("-1 "+(areas[1]-count+b.length+1));
                                            nextTileWhite=move(b,"-1 "+(areas[1]-count+b.length+1),nextTileWhite);
                                        } else {
                                            moves.add("-1 0");
                                            nextTileWhite=move(b,"-1 0",nextTileWhite);
                                        }
                                    } else {
                                        moves.add("-1 "+(areas[1]-count+b.length));
                                        nextTileWhite=move(b,"-1 "+(areas[1]-count+b.length),nextTileWhite);
                                    }
                                    while(areas[1]-count>=0&&count(b,areas[1]-count)==b.length) {
                                        areas[1]--;
                                        added++;
                                    }
                                    while(areas[1]-count<0&&count(b,areas[1]-count+b.length)==b.length) {
                                        areas[1]--;
                                        added++;
                                    }
                                }
                            }
                        }
                    }
                    for(int k=0;k<=max-areas[1]+added+count;k++) {
                        moves.add(i+" -1");
                        nextTileWhite=move(b,i+" -1",nextTileWhite);
                    }
                }
            }
        }
        areas[0]=smallX;
        areas[1]=smallY;
        areas[2]=bigX-smallX+1;
        areas[3]=bigY-smallY+1;
        originala0=areas[0];
        originala1=areas[1];
        originala2=areas[2];
        originala3=areas[3];
        boolean alreadyQuit=false;
        for(int i=areas[1];i<areas[1]+areas[3];i++) {
            int min=b.length;
            int max=0;
            boolean quit=false;
            ArrayList<Integer> points=new ArrayList<Integer>();
            for(int j=originala0;j<originala0+originala2;j++) {
                if(b[j][i]&&colors[j][i]!=maxColor) {
                    min=Math.min(j,min);
                    max=Math.max(j,max);
                    points.add(j);
                }
            }
            int count=0;
            int added=0;
            if(min>=(originala0+originala2)/2) {
                int moved=0;
                for(int j=0;!quit&&j<=areas[0]+areas[2]-min-1-added;j++) {
                    int c1=0;
                    moves.add("-1 "+i);
                    nextTileWhite=move(b,"-1 "+i,nextTileWhite);
                    moved++;
                    if(points.contains(areas[0]+areas[2]-j-1-added)) {
                        while(c1<b.length*4&&nextTileWhite) {
                            c1++;
                            moves.add("-1 "+i);
                            nextTileWhite=move(b,"-1 "+i,nextTileWhite);
                            moved++;
                            count++;
                            if(count>=b.length) count-=b.length;
                        }
                        if(areas[0]+areas[2]+count<b.length) {
                            while(c1<b.length*4&&(nextTileWhite||b[areas[0]+areas[2]+count][i])) {
                                c1++;
                                moves.add((areas[0]+areas[2]+count)+" -1");
                                nextTileWhite=move(b,(areas[0]+areas[2]+count)+" -1",nextTileWhite);
                                while(areas[0]+areas[2]+count<b.length&&count(b,areas[0]+areas[2]+count)==b.length) {
                                    areas[2]++;
                                    added++;
                                }
                                while(areas[0]+areas[2]+count>=b.length&&count(b,areas[0]+areas[2]+count-b.length)==b.length) {
                                    areas[2]++;
                                    added++;
                                }
                            }
                        } else {
                            while(c1<b.length*4&&(areas[0]+areas[2]+count-1-b.length<0||nextTileWhite)) {
                                c1++;
                                moves.add("-1 "+i);
                                nextTileWhite=move(b,"-1 "+i,nextTileWhite);
                                moved++;
                                count++;
                                if(count>=b.length) count-=b.length;
                            }
                            while(c1<b.length*4&&(nextTileWhite||b[areas[0]+areas[2]+count-1-b.length][i])) {
                                c1++;
                                moves.add((areas[0]+areas[2]+count-1-b.length)+" -1");
                                nextTileWhite=move(b,(areas[0]+areas[2]+count-1-b.length)+" -1",nextTileWhite);
                                while(areas[0]+areas[2]+count-1<b.length&&count(b,areas[0]+areas[2]+count-1)==b.length) {
                                    areas[2]++;
                                    added++;
                                }
                                while(areas[0]+areas[2]+count-1>=b.length&&count(b,areas[0]+areas[2]+count-1-b.length)==b.length) {
                                    areas[2]++;
                                    added++;
                                }
                            }
                        }
                        int index=0;
                        for(index=0;index<points.size();index++) {
                            if(points.get(index)==areas[0]+areas[2]-j-1-added) {
                                points.remove(index);
                                index--;
                            }
                        }
                        int newmin=b.length;
                        int newmax=0;
                        for(index=0;index<points.size();index++) {
                            newmin=Math.min(j,newmin);
                            newmax=Math.max(j,newmax);
                        }
                        if(newmin<(originala0+originala2)/2) {
                            if(!alreadyQuit) quit=true;
                        }
                    }
                }
                for(int k=0;k<moved;k++) {
                    moves.add(b.length+" "+i);
                    nextTileWhite=move(b,b.length+" "+i,nextTileWhite);
                }
            } else {
                int moved=0;
                for(int j=0;!quit&&j<=max-areas[0]+added;j++) {
                    int c1=0;
                    moves.add(b.length+" "+i);
                    nextTileWhite=move(b,b.length+" "+i,nextTileWhite);
                    moved++;
                    if(points.contains(areas[0]+added+j)) {
                        while(c1<b.length*4&&nextTileWhite) {
                            c1++;
                            moves.add(b.length+" "+i);
                            nextTileWhite=move(b,b.length+" "+i,nextTileWhite);
                            moved++;
                            count++;
                            if(count>=b.length) count-=b.length;
                        }
                        if(areas[0]-count-1>=0) {
                            while(c1<b.length*4&&(nextTileWhite||b[areas[0]-count-1][i])) {
                                c1++;
                                moves.add((areas[0]-count-1)+" -1");
                                nextTileWhite=move(b,(areas[0]-count-1)+" -1",nextTileWhite);
                                while(areas[0]-count-1>=0&&count(b,areas[0]-count-1)==b.length) {
                                    areas[0]--;
                                    added++;
                                }
                                while(areas[0]-count-1<0&&count(b,areas[0]-count-1+b.length)==b.length) {
                                    areas[0]--;
                                    added++;
                                }
                            }
                        } else {
                            while(c1<b.length*4&&(areas[0]-count+b.length>=b.length||areas[0]-count+b.length<0||nextTileWhite)) {
                                c1++;
                                moves.add(b.length+" "+i);
                                nextTileWhite=move(b,b.length+" "+i,nextTileWhite);
                                moved++;
                                count++;
                                if(count>=b.length) count-=b.length;
                            }
                            while(c1<b.length*4&&(nextTileWhite||b[areas[0]-count+b.length][i])) {
                                c1++;
                                moves.add((areas[0]-count+b.length)+" -1");
                                nextTileWhite=move(b,(areas[0]-count+b.length)+" -1",nextTileWhite);
                                while(areas[0]-count>=0&&count(b,areas[0]-count)==b.length) {
                                    areas[0]--;
                                    added++;
                                }
                                while(areas[0]-count<0&&count(b,areas[0]-count+b.length)==b.length) {
                                    areas[0]--;
                                    added++;
                                }
                            }
                        }
                        int index=0;
                        for(index=0;index<points.size();index++) {
                            if(points.get(index)==areas[0]+areas[2]-j-1-added) {
                                points.remove(index);
                                index--;
                            }
                        }
                        int newmin=b.length;
                        int newmax=0;
                        for(index=0;index<points.size();index++) {
                            newmin=Math.min(j,newmin);
                            newmax=Math.max(j,newmax);
                        }
                        if(newmax>=(originala0+originala2)/2) {
                            if(!alreadyQuit) quit=true;
                        }
                    }
                }
                for(int k=0;k<moved;k++) {
                    moves.add("-1 "+i);
                    nextTileWhite=move(b,"-1 "+i,nextTileWhite);
                }
            }
            if(quit) {
                i--;
                alreadyQuit=true;
            } else {
                alreadyQuit=false;
            }
        }
        areas[0]=smallX;
        areas[1]=smallY;
        areas[2]=bigX-smallX+1;
        areas[3]=bigY-smallY+1;
        if(areas[0]<0) areas[0]=0;
        if(areas[2]+areas[0]>b.length) areas[2]=b.length-areas[0];
        int currRow=areas[0]+areas[2];
        if(currRow>=b.length) {
            currRow=areas[0]-1;
        }
        if(currRow<0) {
            currRow=areas[0]+areas[2]-1;
        }
        int totalCount=0;
        for(int i=0;i<areas[1];i++) {
            int count=0;
            for(int j=areas[0];j<areas[0]+areas[2];j++) {
                if(b[j][i]) count++;
            }
            int c=0;
            boolean done=false;
            while(!done&&c<b.length*2&&count>0) {
                c++;
                if(b.length-areas[0]-areas[2]>areas[0]) {
                    while(areas[0]+areas[2]<b.length&&count(b[areas[0]+areas[2]])<b.length&&b[areas[0]+areas[2]][i]) {
                        moves.add((areas[0]+areas[2])+" -1");
                        nextTileWhite=move(b,(areas[0]+areas[2])+" -1",nextTileWhite);
                    }
                    while(nextTileWhite&&areas[0]+areas[2]<b.length-1&&count(b[areas[0]+areas[2]+1])<b.length) {
                        moves.add((areas[0]+areas[2]+1)+" -1");
                        nextTileWhite=move(b,(areas[0]+areas[2]+1)+" -1",nextTileWhite);
                    }
                    moves.add("-1 "+i);
                    nextTileWhite=move(b,"-1 "+i,nextTileWhite);
                    while(areas[0]+areas[2]<b.length&&count(b[areas[0]+areas[2]])<b.length&&b[areas[0]+areas[2]][i]) {
                        moves.add((areas[0]+areas[2])+" -1");
                        nextTileWhite=move(b,(areas[0]+areas[2])+" -1",nextTileWhite);
                    }
                } else {
                    while(areas[0]>0&&count(b[areas[0]-1])<b.length&&b[areas[0]-1][i]) {
                        moves.add((areas[0]-1)+" -1");
                        nextTileWhite=move(b,(areas[0]-1)+" -1",nextTileWhite);
                    }
                    while(nextTileWhite&&areas[0]-1>0&&count(b[areas[0]-2])<b.length&&b[areas[0]-2][i]) {
                        moves.add((areas[0]-2)+" -1");
                        nextTileWhite=move(b,(areas[0]-2)+" -1",nextTileWhite);
                    }
                    moves.add(b.length+" "+i);
                    nextTileWhite=move(b,b.length+" "+i,nextTileWhite);
                    while(areas[0]>0&&count(b[areas[0]-1])<b.length&&b[areas[0]-1][i]) {
                        moves.add((areas[0]-1)+" -1");
                        nextTileWhite=move(b,(areas[0]-1)+" -1",nextTileWhite);
                    }
                }
                while(nextTileWhite) {
                    if(count(b[currRow])==b[currRow].length) {
                        if(currRow>=areas[0]+areas[2]) {
                            currRow++;
                        } else {
                            currRow--;
                        }
                        if(currRow>=b.length) {
                            currRow=areas[0]-1;
                        }
                        if(currRow<0) {
                            currRow=areas[0]+areas[2]-1;
                        }
                    }
                    moves.add(currRow+" "+b.length);
                    nextTileWhite=move(b,currRow+" "+b.length,nextTileWhite);
                    if(i<b.length-1&&b[currRow][i+1]) {
                        if(currRow>=areas[0]+areas[2]) {
                            currRow++;
                        } else {
                            currRow--;
                        }
                        if(currRow>=b.length) {
                            currRow=areas[0]-1;
                        }
                        if(currRow<0) {
                            currRow=areas[0]+areas[2]-1;
                        }
                    }
                }
                count=0;
                for(int j=areas[0];j<areas[0]+areas[2];j++) {
                    if(b[j][i]) count++;
                }
            }
            while(nextTileWhite) {
                if(currRow>=b.length) {
                    currRow=areas[0]-1;
                }
                if(currRow<0) {
                    currRow=areas[0]+areas[2]-1;
                }
                moves.add(currRow+" -1");
                nextTileWhite=move(b,currRow+" -1",nextTileWhite);
                if(i<b.length-1&&b[currRow][i+1]) {
                    if(currRow>=areas[0]+areas[2]) {
                        currRow++;
                    } else {
                        currRow--;
                    }
                    if(currRow>=b.length) {
                        currRow=areas[0]-1;
                    }
                    if(currRow<0) {
                        currRow=areas[0]+areas[2]-1;
                    }
                }
            }
        }
        for(int i=areas[1]+areas[3];i<b.length;i++) {
            int count=0;
            for(int j=areas[0];j<areas[0]+areas[2];j++) {
                if(b[j][i]) count++;
            }
            int c=0;
            boolean done=false;
            while(!done&&c<b.length*10&&count>0) {
                c++;
                if(b.length-areas[0]-areas[2]>areas[0]) {
                    while(areas[0]+areas[2]<b.length&&count(b[areas[0]+areas[2]])<b.length&&b[areas[0]+areas[2]][i]) {
                        moves.add((areas[0]+areas[2])+" -1");
                        nextTileWhite=move(b,(areas[0]+areas[2])+" -1",nextTileWhite);
                    }
                    while(nextTileWhite&&areas[0]+areas[2]<b.length-1&&count(b[areas[0]+areas[2]+1])<b.length) {
                        moves.add((areas[0]+areas[2]+1)+" -1");
                        nextTileWhite=move(b,(areas[0]+areas[2]+1)+" -1",nextTileWhite);
                    }
                    moves.add("-1 "+i);
                    nextTileWhite=move(b,"-1 "+i,nextTileWhite);
                    int c4=0;
                    while(areas[0]+areas[2]<b.length&&count(b[areas[0]+areas[2]])<b.length&&b[areas[0]+areas[2]][i]) {
                        c4++;
                        moves.add((areas[0]+areas[2])+" -1");
                        nextTileWhite=move(b,(areas[0]+areas[2])+" -1",nextTileWhite);
                    }
                } else {
                    while(areas[0]>0&&count(b[areas[0]-1])<b.length&&b[areas[0]-1][i]) {
                        moves.add((areas[0]-1)+" -1");
                        nextTileWhite=move(b,(areas[0]-1)+" -1",nextTileWhite);
                    }
                    while(nextTileWhite&&areas[0]-1>0&&count(b[areas[0]-2])<b.length&&b[areas[0]-2][i]) {
                        moves.add((areas[0]-2)+" -1");
                        nextTileWhite=move(b,(areas[0]-2)+" -1",nextTileWhite);
                    }
                    moves.add(b.length+" "+i);
                    nextTileWhite=move(b,b.length+" "+i,nextTileWhite);
                    while(areas[0]>0&&count(b[areas[0]-1])<b.length&&b[areas[0]-1][i]) {
                        moves.add((areas[0]-1)+" -1");
                        nextTileWhite=move(b,(areas[0]-1)+" -1",nextTileWhite);
                    }
                }
                while(nextTileWhite) {
                    if(count(b[currRow])==b[currRow].length) {
                        if(currRow>=areas[0]+areas[2]) {
                            currRow++;
                        } else {
                            currRow--;
                        }
                        if(currRow==b.length) {
                            currRow=areas[0]-1;
                        }
                        if(currRow<0) {
                            currRow=areas[0]+areas[2]-1;
                        }
                    }
                    moves.add(currRow+" -1");
                    nextTileWhite=move(b,currRow+" -1",nextTileWhite);
                    if(i>0&&b[currRow][i-1]) {
                        if(currRow>=areas[0]+areas[2]) {
                            currRow++;
                        } else {
                            currRow--;
                        }
                        if(currRow>=b.length) {
                            currRow=areas[0]-1;
                        }
                        if(currRow<0) {
                            currRow=areas[0]+areas[2]-1;
                        }
                    }
                }
                count=0;
                for(int j=areas[0];j<areas[0]+areas[2];j++) {
                    if(b[j][i]) count++;
                }
            }
            while(nextTileWhite) {
                if(currRow>=b.length) {
                    currRow=areas[0]-1;
                }
                if(currRow<0) {
                    currRow=areas[0]+areas[2]-1;
                }
                moves.add(currRow+" -1");
                nextTileWhite=move(b,currRow+" -1",nextTileWhite);
                if(i>0&&b[currRow][i-1]) {
                    if(currRow>=areas[0]+areas[2]) {
                        currRow++;
                    } else {
                        currRow--;
                    }
                    if(currRow>=b.length) {
                        currRow=areas[0]-1;
                    }
                    if(currRow<0) {
                        currRow=areas[0]+areas[2]-1;
                    }
                }
            }
        }
        currRow=areas[0]+areas[2];
        if(currRow>=b.length) {
            currRow=areas[0]-1;
        }
        if(currRow<0) {
            currRow=areas[0]+areas[2]-1;
        }
        while(count(b[currRow])==b.length) {
            currRow++;
            if(currRow>=b.length) {
                currRow=areas[0]-1;
            }
            if(currRow<0) {
                currRow=areas[0]+areas[2]-1;
            }
        }
        int c=0;
        while(c<b.length*2&&!done(b,nextTileWhite)&&currRow<b.length-1&&count(b[currRow])>0&&!connected(b[currRow])) {
            c++;
            if(nextTileWhite) {
                moves.add((currRow+1)+" -1");
                nextTileWhite=move(b,(currRow+1)+" -1",nextTileWhite);
            } else {
                moves.add(currRow+" "+b.length);
                nextTileWhite=move(b,currRow+" "+b.length,nextTileWhite);
            }
        }
        c=0;
        while(c<b.length&&currRow<b.length-1&&!done(b,nextTileWhite)&&nextTileWhite) {
            c++;
            moves.add((currRow+1)+" -1");
            nextTileWhite=move(b,(currRow+1)+" -1",nextTileWhite);
        }
        for(int i=0;!done(b,nextTileWhite)&&i<areas[0];i++) {
            if(i<areas[0]&&i<currRow||i>currRow&&i>areas[0]+areas[2]) {
                while(i!=currRow&&count(b[i])>0) {
                    if(nextTileWhite) {
                        if(currRow>=b.length) {
                            currRow=areas[0]-1;
                        }
                        if(currRow<0) {
                            currRow=areas[0]+areas[2]-1;
                        }
                        moves.add(currRow+" -1");
                        nextTileWhite=move(b,currRow+" -1",nextTileWhite);
                        if(currRow>=areas[0]+areas[2]&&(currRow%2==(areas[0]+areas[2])%2&&
                            count(b[currRow])==b[currRow].length||currRow%2!=(areas[0]+areas[2])%2&&
                            count(b[currRow])>=1)||
                        currRow<areas[0]+areas[2]&&(currRow%2==(areas[0]-1)%2&&
                            count(b[currRow])==b[currRow].length||currRow%2!=(areas[0]-1)%2&&
                            count(b[currRow])>=1)) {
                            if(currRow>=areas[0]+areas[2]) {
                                currRow++;
                            } else {
                                currRow--;
                            }
                            if(currRow==b.length) {
                                currRow=areas[0]-1;
                            }
                        }
                    } else {
                        if(closerRight(b[i])) {
                            moves.add(i+" -1");
                            nextTileWhite=move(b,i+" -1",nextTileWhite);
                        } else {
                            moves.add(i+" "+b.length);
                            nextTileWhite=move(b,i+" "+b.length,nextTileWhite);
                        }
                    }
                }
                while(!done(b,nextTileWhite)&&nextTileWhite) {
                    if(currRow>=b.length) {
                        currRow=areas[0]-1;
                    }
                    if(currRow<0) {
                        currRow=areas[0]+areas[2]-1;
                    }
                    moves.add(currRow+" -1");
                    nextTileWhite=move(b,currRow+" -1",nextTileWhite);
                    if(count(b[currRow])==b[currRow].length) {
                        if(currRow>=areas[0]+areas[2]) {
                            currRow++;
                        } else {
                            currRow--;
                        }
                        if(currRow==b.length) {
                            currRow=areas[0]-1;
                        }
                    }
                }
            }
        }
        for(int i=b.length-1;!done(b,nextTileWhite)&&i>=0;i--) {
            if(i<areas[0]&&i<currRow||i>currRow&&i>areas[0]+areas[2]) {
                while(!done(b,nextTileWhite)&&i!=currRow&&count(b[i])>0) {
                    if(nextTileWhite) {
                        if(currRow>=b.length) {
                            currRow=areas[0]-1;
                        }
                        if(currRow<0) {
                            currRow=areas[0]+areas[2]-1;
                        }
                        moves.add(currRow+" -1");
                        nextTileWhite=move(b,currRow+" -1",nextTileWhite);
                        if(currRow>=areas[0]+areas[2]&&(currRow%2==(areas[0]+areas[2])%2&&
                            count(b[currRow])==b[currRow].length||currRow%2!=(areas[0]+areas[2])%2&&
                            count(b[currRow])>=1)||
                        currRow<areas[0]+areas[2]&&(currRow%2==(areas[0]-1)%2&&
                            count(b[currRow])==b[currRow].length||currRow%2!=(areas[0]-1)%2&&
                            count(b[currRow])>=1)) {
                            if(currRow>=areas[0]+areas[2]) {
                                currRow++;
                            } else {
                                currRow--;
                            }
                            if(currRow==b.length) {
                                currRow=areas[0]-1;
                            }
                        }
                    } else {
                        if(closerRight(b[i])) {
                            moves.add(i+" -1");
                            nextTileWhite=move(b,i+" -1",nextTileWhite);
                        } else {
                            moves.add(i+" "+b.length);
                            nextTileWhite=move(b,i+" "+b.length,nextTileWhite);
                        }
                    }
                }
                while(!done(b,nextTileWhite)&&nextTileWhite) {
                    if(currRow>=b.length) {
                        currRow=areas[0]-1;
                    }
                    if(currRow<0) {
                        currRow=areas[0]+areas[2]-1;
                    }
                    moves.add(currRow+" -1");
                    nextTileWhite=move(b,currRow+" -1",nextTileWhite);
                    if(count(b[currRow])==b[currRow].length) {
                        if(currRow>=areas[0]+areas[2]) {
                            currRow++;
                        } else {
                            currRow--;
                        }
                        if(currRow==b.length) {
                            currRow=areas[0]-1;
                        }
                    }
                }
            }
        }
        c=0;
        while(c<b.length*2&&!done(b,nextTileWhite)) {
            c++;
            if(nextTileWhite) {
                if(currRow>=areas[0]+areas[2]&&currRow>=1) {
                    moves.add(currRow-1+" -1");
                    nextTileWhite=move(b,currRow-1+" -1",nextTileWhite);
                } else if(currRow<b.length-1) {
                    moves.add(currRow+1+" -1");
                    nextTileWhite=move(b,currRow+1+" -1",nextTileWhite);
                } else {
                    moves.add(currRow+" -1");
                    nextTileWhite=move(b,currRow+" -1",nextTileWhite);
                }
                if(count(b[currRow])==b[currRow].length) {
                    if(currRow>=areas[0]+areas[2]) {
                        currRow--;
                    } else {
                        currRow++;
                    }
                    if(currRow==b.length) {
                        currRow=areas[0]-1;
                    }
                    if(currRow<0) {
                        currRow=areas[0]+areas[2];
                    }
                }
            } else {
                moves.add(currRow+" -1");
                nextTileWhite=move(b,currRow+" -1",nextTileWhite);
                while(count(b[currRow])<=0) {
                    if(currRow>=areas[0]+areas[2]) {
                        currRow++;
                    } else {
                        currRow--;
                    }
                    if(currRow==b.length) {
                        currRow=areas[0]-1;
                    }
                    if(currRow<0) {
                        currRow=areas[0]+areas[2]-1;
                    }
                }
            }
        }
        String[] ans=new String[0];
        if(done(b,nextTileWhite)) {
            ans=new String[moves.size()];
            for(int i=0;i<moves.size();i++) ans[i]=moves.get(i);
        }
        return ans;
    }
    String[] makeConnect6(boolean[][] b,int diff1,int diff2,int diff3,int diff4) {
        if(done(b,false)) return new String[0];
        long startTime=System.nanoTime();
        boolean nextTileWhite=false;
        ArrayList<String> moves=new ArrayList<String>();
        int mostSquares=0;
        int[] areas=new int[4];
        int countTotal=0;
        for(int a=0;a<b.length;a++) {
            for(int c=0;c<b.length;c++) {
                if(b[a][c]) countTotal++;
            }
        }
        int[][] colors=color(b);
        int maxNum=0;
        for(int i=0;i<b.length;i++) for(int j=0;j<b.length;j++) maxNum=Math.max(maxNum,colors[i][j]);
        int[] colorNums=new int[maxNum+1];
        for(int i=0;i<b.length;i++) for(int j=0;j<b.length;j++) colorNums[colors[i][j]]++;
        int maxColor=1;
        for(int i=1;i<colorNums.length;i++) {
            if(colorNums[maxColor]<colorNums[i]) maxColor=i;
        }
        int smallX=b.length;
        int bigX=0;
        int smallY=b.length;
        int bigY=0;
        for(int i=0;i<b.length;i++) for(int j=0;j<b.length;j++) {
            if(colors[i][j]==maxColor) {
                smallX=Math.min(smallX,i);
                bigX=Math.max(bigX,i);
                smallY=Math.min(smallY,j);
                bigY=Math.max(bigY,j);
            }
        }
        smallX+=diff1;
        smallY+=diff2;
        bigX-=diff3;
        bigY-=diff4;
        if(bigX<=smallX) return new String[0];
        if(bigY<=smallY) return new String[0];
        areas[0]=smallX;
        areas[1]=smallY;
        areas[2]=bigX-smallX+1;
        areas[3]=bigY-smallY+1;
        int originala0=areas[0];
        int originala1=areas[1];
        int originala2=areas[2];
        int originala3=areas[3];
        //TODO move all squares in the big area that aren't connected out
        if(areas[2]<b.length-10) {
            for(int i=areas[0];i<areas[0]+areas[2];i++) {
                int min=b.length;
                int max=0;
                ArrayList<Integer> points=new ArrayList<Integer>();
                for(int j=originala1;j<originala3+originala1;j++) {
                    if(b[i][j]&&colors[i][j]!=maxColor) {
                        min=Math.min(j,min);
                        max=Math.max(j,max);
                        points.add(j);
                    }
                }
                int count=0;
                int added=0;
                if(min>=(originala1+originala3)/2&&areas[1]+areas[3]-min-1<Math.min(b.length-areas[2],10)) {
                    for(int j=0;j<=areas[1]+areas[3]-min-1-added;j++) {
                        int c1=0;
                        moves.add(i+" -1");
                        nextTileWhite=move(b,i+" -1",nextTileWhite);
                        if(points.contains(areas[1]+areas[3]-j-1-added)) {
                            while(c1<b.length*4&&nextTileWhite) {
                                c1++;
                                moves.add(i+" -1");
                                nextTileWhite=move(b,i+" -1",nextTileWhite);
                                count++;
                                if(count>=b.length) count-=b.length;
                            }
                            if(areas[1]+areas[3]+count<b.length) {
                                while(c1<b.length*4&&(nextTileWhite||b[i][areas[1]+areas[3]+count])) {
                                    c1++;
                                    moves.add("-1 "+(areas[1]+areas[3]+count));
                                    nextTileWhite=move(b,"-1 "+(areas[1]+areas[3]+count),nextTileWhite);
                                    while(areas[1]+areas[3]+count<b.length&&count(b,areas[1]+areas[3]+count)==b.length) {
                                        areas[3]++;
                                        added++;
                                    }
                                    while(areas[1]+areas[3]+count>=b.length&&count(b,areas[1]+areas[3]+count-b.length)==b.length) {
                                        areas[3]++;
                                        added++;
                                    }
                                }
                            } else {
                                while(c1<b.length*4&&(areas[1]+areas[3]+count-b.length-1<0||nextTileWhite)) {
                                    c1++;
                                    moves.add(i+" -1");
                                    nextTileWhite=move(b,i+" -1",nextTileWhite);
                                    count++;
                                    if(count>=b.length) count-=b.length;
                                }
                                while(c1<b.length*4&&(nextTileWhite||b[i][areas[1]+areas[3]+count-b.length-1])) {
                                    c1++;
                                    moves.add("-1 "+(areas[1]+areas[3]+count-b.length-1));
                                    nextTileWhite=move(b,"-1 "+(areas[1]+areas[3]+count-b.length-1),nextTileWhite);
                                    while(areas[1]+areas[3]+count-1<b.length&&count(b,areas[1]+areas[3]+count-1)==b.length) {
                                        areas[3]++;
                                        added++;
                                    }
                                    while(areas[1]+areas[3]+count-1>=b.length&&count(b,areas[1]+areas[3]+count-b.length-1)==b.length) {
                                        areas[3]++;
                                        added++;
                                    }
                                }
                            }
                        }
                    }
                    for(int k=0;k<=areas[1]+areas[3]-min-1-added+count;k++) {
                        moves.add(i+" "+b.length);
                        nextTileWhite=move(b,i+" "+b.length,nextTileWhite);
                    }
                } else if(max-areas[1]<Math.min(b.length-areas[2],10)) {
                    for(int j=0;j<=max-areas[1]+added;j++) {
                        int c1=0;
                        moves.add(i+" "+b.length);
                        nextTileWhite=move(b,i+" "+b.length,nextTileWhite);
                        if(points.contains(areas[1]+added+j)) {
                            while(c1<b.length*4&&nextTileWhite) {
                                c1++;
                                moves.add(i+" "+b.length);
                                nextTileWhite=move(b,i+" "+b.length,nextTileWhite);
                                count++;
                                if(count>=b.length) count-=b.length;
                            }
                            if(areas[1]-count-1>=0) {
                                while(c1<b.length*4&&(nextTileWhite||b[i][areas[1]-count-1])) {
                                    c1++;
                                    moves.add("-1 "+(areas[1]-count-1));
                                    nextTileWhite=move(b,"-1 "+(areas[1]-count-1),nextTileWhite);
                                    while(areas[1]-count-1>=0&&count(b,areas[1]-count-1)==b.length) {
                                        areas[1]--;
                                        added++;
                                    }
                                    while(areas[1]-count-1<0&&count(b,areas[1]-count-1+b.length)==b.length) {
                                        areas[1]--;
                                        added++;
                                    }
                                }
                            } else {
                                while(c1<b.length*4&&(areas[1]-count+b.length>=b.length||nextTileWhite)) {
                                    c1++;
                                    moves.add(i+" "+b.length);
                                    nextTileWhite=move(b,i+" "+b.length,nextTileWhite);
                                    count++;
                                    if(count>=b.length) count-=b.length;
                                }
                                while(c1<b.length*4&&(nextTileWhite||b[i][areas[1]-count+b.length])) {
                                    c1++;
                                    moves.add("-1 "+(areas[1]-count+b.length));
                                    nextTileWhite=move(b,"-1 "+(areas[1]-count+b.length),nextTileWhite);
                                    while(areas[1]-count>=0&&count(b,areas[1]-count)==b.length) {
                                        areas[1]--;
                                        added++;
                                    }
                                    while(areas[1]-count<0&&count(b,areas[1]-count+b.length)==b.length) {
                                        areas[1]--;
                                        added++;
                                    }
                                }
                            }
                        }
                    }
                    for(int k=0;k<=max-areas[1]+added+count;k++) {
                        moves.add(i+" -1");
                        nextTileWhite=move(b,i+" -1",nextTileWhite);
                    }
                }
            }
        }
        areas[0]=smallX;
        areas[1]=smallY;
        areas[2]=bigX-smallX+1;
        areas[3]=bigY-smallY+1;
        originala0=areas[0];
        originala1=areas[1];
        originala2=areas[2];
        originala3=areas[3];
        boolean alreadyQuit=false;
        for(int i=areas[1];i<areas[1]+areas[3];i++) {
            int min=b.length;
            int max=0;
            boolean quit=false;
            ArrayList<Integer> points=new ArrayList<Integer>();
            for(int j=originala0;j<originala0+originala2;j++) {
                if(b[j][i]&&colors[j][i]!=maxColor) {
                    min=Math.min(j,min);
                    max=Math.max(j,max);
                    points.add(j);
                }
            }
            int count=0;
            int added=0;
            if(min>=(originala0+originala2)/2) {
                int moved=0;
                for(int j=0;!quit&&j<=areas[0]+areas[2]-min-1-added;j++) {
                    int c1=0;
                    moves.add("-1 "+i);
                    nextTileWhite=move(b,"-1 "+i,nextTileWhite);
                    moved++;
                    if(points.contains(areas[0]+areas[2]-j-1-added)) {
                        while(c1<b.length*4&&nextTileWhite) {
                            c1++;
                            moves.add("-1 "+i);
                            nextTileWhite=move(b,"-1 "+i,nextTileWhite);
                            moved++;
                            count++;
                            if(count>=b.length) count-=b.length;
                        }
                        if(areas[0]+areas[2]+count<b.length) {
                            while(c1<b.length*4&&(nextTileWhite||b[areas[0]+areas[2]+count][i])) {
                                c1++;
                                moves.add((areas[0]+areas[2]+count)+" -1");
                                nextTileWhite=move(b,(areas[0]+areas[2]+count)+" -1",nextTileWhite);
                                while(areas[0]+areas[2]+count<b.length&&count(b,areas[0]+areas[2]+count)==b.length) {
                                    areas[2]++;
                                    added++;
                                }
                                while(areas[0]+areas[2]+count>=b.length&&count(b,areas[0]+areas[2]+count-b.length)==b.length) {
                                    areas[2]++;
                                    added++;
                                }
                            }
                        } else {
                            while(c1<b.length*4&&(areas[0]+areas[2]+count-1-b.length<0||nextTileWhite)) {
                                c1++;
                                moves.add("-1 "+i);
                                nextTileWhite=move(b,"-1 "+i,nextTileWhite);
                                moved++;
                                count++;
                                if(count>=b.length) count-=b.length;
                            }
                            while(c1<b.length*4&&(nextTileWhite||b[areas[0]+areas[2]+count-1-b.length][i])) {
                                c1++;
                                moves.add((areas[0]+areas[2]+count-1-b.length)+" -1");
                                nextTileWhite=move(b,(areas[0]+areas[2]+count-1-b.length)+" -1",nextTileWhite);
                                while(areas[0]+areas[2]+count-1<b.length&&count(b,areas[0]+areas[2]+count-1)==b.length) {
                                    areas[2]++;
                                    added++;
                                }
                                while(areas[0]+areas[2]+count-1>=b.length&&count(b,areas[0]+areas[2]+count-1-b.length)==b.length) {
                                    areas[2]++;
                                    added++;
                                }
                            }
                        }
                        int index=0;
                        for(index=0;index<points.size();index++) {
                            if(points.get(index)==areas[0]+areas[2]-j-1-added) {
                                points.remove(index);
                                index--;
                            }
                        }
                        int newmin=b.length;
                        int newmax=0;
                        for(index=0;index<points.size();index++) {
                            newmin=Math.min(j,newmin);
                            newmax=Math.max(j,newmax);
                        }
                        if(newmin<(originala0+originala2)/2) {
                            if(!alreadyQuit) quit=true;
                        }
                    }
                }
                for(int k=0;k<moved;k++) {
                    moves.add(b.length+" "+i);
                    nextTileWhite=move(b,b.length+" "+i,nextTileWhite);
                }
            } else {
                int moved=0;
                for(int j=0;!quit&&j<=max-areas[0]+added;j++) {
                    int c1=0;
                    moves.add(b.length+" "+i);
                    nextTileWhite=move(b,b.length+" "+i,nextTileWhite);
                    moved++;
                    if(points.contains(areas[0]+added+j)) {
                        while(c1<b.length*4&&nextTileWhite) {
                            c1++;
                            moves.add(b.length+" "+i);
                            nextTileWhite=move(b,b.length+" "+i,nextTileWhite);
                            moved++;
                            count++;
                            if(count>=b.length) count-=b.length;
                        }
                        if(areas[0]-count-1>=0) {
                            while(c1<b.length*4&&(nextTileWhite||b[areas[0]-count-1][i])) {
                                c1++;
                                moves.add((areas[0]-count-1)+" -1");
                                nextTileWhite=move(b,(areas[0]-count-1)+" -1",nextTileWhite);
                                while(areas[0]-count-1>=0&&count(b,areas[0]-count-1)==b.length) {
                                    areas[0]--;
                                    added++;
                                }
                                while(areas[0]-count-1<0&&count(b,areas[0]-count-1+b.length)==b.length) {
                                    areas[0]--;
                                    added++;
                                }
                            }
                        } else {
                            while(c1<b.length*4&&(areas[0]-count+b.length>=b.length||areas[0]-count+b.length<0||nextTileWhite)) {
                                c1++;
                                moves.add(b.length+" "+i);
                                nextTileWhite=move(b,b.length+" "+i,nextTileWhite);
                                moved++;
                                count++;
                                if(count>=b.length) count-=b.length;
                            }
                            while(c1<b.length*4&&(nextTileWhite||b[areas[0]-count+b.length][i])) {
                                c1++;
                                moves.add((areas[0]-count+b.length)+" -1");
                                nextTileWhite=move(b,(areas[0]-count+b.length)+" -1",nextTileWhite);
                                while(areas[0]-count>=0&&count(b,areas[0]-count)==b.length) {
                                    areas[0]--;
                                    added++;
                                }
                                while(areas[0]-count<0&&count(b,areas[0]-count+b.length)==b.length) {
                                    areas[0]--;
                                    added++;
                                }
                            }
                        }
                        int index=0;
                        for(index=0;index<points.size();index++) {
                            if(points.get(index)==areas[0]+areas[2]-j-1-added) {
                                points.remove(index);
                                index--;
                            }
                        }
                        int newmin=b.length;
                        int newmax=0;
                        for(index=0;index<points.size();index++) {
                            newmin=Math.min(j,newmin);
                            newmax=Math.max(j,newmax);
                        }
                        if(newmax>=(originala0+originala2)/2) {
                            if(!alreadyQuit) quit=true;
                        }
                    }
                }
                for(int k=0;k<moved;k++) {
                    moves.add("-1 "+i);
                    nextTileWhite=move(b,"-1 "+i,nextTileWhite);
                }
            }
            if(quit) {
                i--;
                alreadyQuit=true;
            } else {
                alreadyQuit=false;
            }
        }
        colors=color(b);
        maxNum=0;
        for(int i=0;i<b.length;i++) for(int j=0;j<b.length;j++) maxNum=Math.max(maxNum,colors[i][j]);
        colorNums=new int[maxNum+1];
        for(int i=0;i<b.length;i++) for(int j=0;j<b.length;j++) colorNums[colors[i][j]]++;
        maxColor=1;
        for(int i=1;i<colorNums.length;i++) {
            if(colorNums[maxColor]<colorNums[i]) maxColor=i;
        }
        areas[0]=smallX;
        areas[1]=smallY;
        areas[2]=bigX-smallX+1;
        areas[3]=bigY-smallY+1;
        originala0=areas[0];
        originala1=areas[1];
        originala2=areas[2];
        originala3=areas[3];
        if(areas[2]<b.length-10) {
            for(int i=areas[0];i<areas[0]+areas[2];i++) {
                int min=b.length;
                int max=0;
                ArrayList<Integer> points=new ArrayList<Integer>();
                for(int j=originala1;j<originala3+originala1;j++) {
                    if(b[i][j]&&colors[i][j]!=maxColor) {
                        min=Math.min(j,min);
                        max=Math.max(j,max);
                        points.add(j);
                    }
                }
                int count=0;
                int added=0;
                if(min>=(originala1+originala3)/2&&areas[1]+areas[3]-min-1<Math.min(b.length-areas[2],10)) {
                    for(int j=0;j<=areas[1]+areas[3]-min-1-added;j++) {
                        int c1=0;
                        moves.add(i+" -1");
                        nextTileWhite=move(b,i+" -1",nextTileWhite);
                        if(points.contains(areas[1]+areas[3]-j-1-added)) {
                            while(c1<b.length*4&&nextTileWhite) {
                                c1++;
                                moves.add(i+" -1");
                                nextTileWhite=move(b,i+" -1",nextTileWhite);
                                count++;
                                if(count>=b.length) count-=b.length;
                            }
                            if(areas[1]+areas[3]+count<b.length) {
                                while(c1<b.length*4&&(nextTileWhite||b[i][areas[1]+areas[3]+count])) {
                                    c1++;
                                    moves.add("-1 "+(areas[1]+areas[3]+count));
                                    nextTileWhite=move(b,"-1 "+(areas[1]+areas[3]+count),nextTileWhite);
                                    while(areas[1]+areas[3]+count<b.length&&count(b,areas[1]+areas[3]+count)==b.length) {
                                        areas[3]++;
                                        added++;
                                    }
                                    while(areas[1]+areas[3]+count>=b.length&&count(b,areas[1]+areas[3]+count-b.length)==b.length) {
                                        areas[3]++;
                                        added++;
                                    }
                                }
                            } else {
                                while(c1<b.length*4&&(areas[1]+areas[3]+count-1-b.length<0||nextTileWhite)) {
                                    c1++;
                                    moves.add(i+" -1");
                                    nextTileWhite=move(b,i+" -1",nextTileWhite);
                                    count++;
                                    if(count>=b.length) count-=b.length;
                                }
                                while(c1<b.length*4&&(nextTileWhite||b[i][areas[1]+areas[3]+count-1-b.length])) {
                                    c1++;
                                    moves.add("-1 "+(areas[1]+areas[3]+count-1-b.length));
                                    nextTileWhite=move(b,"-1 "+(areas[1]+areas[3]+count-1-b.length),nextTileWhite);
                                    while(areas[1]+areas[3]+count-1<b.length&&count(b,areas[1]+areas[3]+count-1)==b.length) {
                                        areas[3]++;
                                        added++;
                                    }
                                    while(areas[1]+areas[3]+count-1>=b.length&&count(b,areas[1]+areas[3]+count-1-b.length)==b.length) {
                                        areas[3]++;
                                        added++;
                                    }
                                }
                            }
                        }
                    }
                    for(int k=0;k<=areas[1]+areas[3]-min-1-added+count;k++) {
                        moves.add(i+" "+b.length);
                        nextTileWhite=move(b,i+" "+b.length,nextTileWhite);
                    }
                } else if(max-areas[1]<Math.min(b.length-areas[2],10)) {
                    for(int j=0;j<=max-areas[1]+added;j++) {
                        int c1=0;
                        moves.add(i+" "+b.length);
                        nextTileWhite=move(b,i+" "+b.length,nextTileWhite);
                        if(points.contains(areas[1]+added+j)) {
                            while(c1<b.length*4&&nextTileWhite) {
                                c1++;
                                moves.add(i+" "+b.length);
                                nextTileWhite=move(b,i+" "+b.length,nextTileWhite);
                                count++;
                                if(count>=b.length) count-=b.length;
                            }
                            if(areas[1]-count-1>=0) {
                                while(c1<b.length*4&&(nextTileWhite||b[i][areas[1]-count-1])) {
                                    c1++;
                                    moves.add("-1 "+(areas[1]-count-1));
                                    nextTileWhite=move(b,"-1 "+(areas[1]-count-1),nextTileWhite);
                                    while(areas[1]-count-1>=0&&count(b,areas[1]-count-1)==b.length) {
                                        areas[1]--;
                                        added++;
                                    }
                                    while(areas[1]-count-1<0&&count(b,areas[1]-count-1+b.length)==b.length) {
                                        areas[1]--;
                                        added++;
                                    }
                                }
                            } else {
                                while(c1<b.length*4&&(areas[1]-count+b.length>=b.length||nextTileWhite)) {
                                    c1++;
                                    moves.add(i+" "+b.length);
                                    nextTileWhite=move(b,i+" "+b.length,nextTileWhite);
                                    count++;
                                    if(count>=b.length) count-=b.length;
                                }
                                while(c1<b.length*4&&(nextTileWhite||b[i][areas[1]-count+b.length])) {
                                    c1++;
                                    moves.add("-1 "+(areas[1]-count+b.length));
                                    nextTileWhite=move(b,"-1 "+(areas[1]-count+b.length),nextTileWhite);
                                    while(areas[1]-count>=0&&count(b,areas[1]-count)==b.length) {
                                        areas[1]--;
                                        added++;
                                    }
                                    while(areas[1]-count<0&&count(b,areas[1]-count+b.length)==b.length) {
                                        areas[1]--;
                                        added++;
                                    }
                                }
                            }
                        }
                    }
                    for(int k=0;k<=max-areas[1]+added+count;k++) {
                        moves.add(i+" -1");
                        nextTileWhite=move(b,i+" -1",nextTileWhite);
                    }
                }
            }
        }
        areas[0]=smallX;
        areas[1]=smallY;
        areas[2]=bigX-smallX+1;
        areas[3]=bigY-smallY+1;
        originala0=areas[0];
        originala1=areas[1];
        originala2=areas[2];
        originala3=areas[3];
        for(int i=areas[1];i<areas[1]+areas[3];i++) {
            int min=b.length;
            int max=0;
            ArrayList<Integer> points=new ArrayList<Integer>();
            for(int j=originala0;j<originala0+originala2;j++) {
                if(b[j][i]&&colors[j][i]!=maxColor) {
                    min=Math.min(j,min);
                    max=Math.max(j,max);
                    points.add(j);
                }
            }
            int count=0;
            int added=0;
            if(min>=(originala0+originala2)/2) {
                for(int j=0;j<=areas[0]+areas[2]-min-1-added;j++) {
                    int c1=0;
                    moves.add("-1 "+i);
                    nextTileWhite=move(b,"-1 "+i,nextTileWhite);
                    if(points.contains(areas[0]+areas[2]-j-1-added)) {
                        while(c1<b.length*4&&nextTileWhite) {
                            c1++;
                            moves.add("-1 "+i);
                            nextTileWhite=move(b,"-1 "+i,nextTileWhite);
                            count++;
                            if(count>=b.length) count-=b.length;
                        }
                        if(areas[0]+areas[2]+count<b.length) {
                            while(c1<b.length*4&&(nextTileWhite||b[areas[0]+areas[2]+count][i])) {
                                c1++;
                                moves.add((areas[0]+areas[2]+count)+" -1");
                                nextTileWhite=move(b,(areas[0]+areas[2]+count)+" -1",nextTileWhite);
                                while(areas[0]+areas[2]+count<b.length&&count(b,areas[0]+areas[2]+count)==b.length) {
                                    areas[2]++;
                                    added++;
                                }
                                while(areas[0]+areas[2]+count>=b.length&&count(b,areas[0]+areas[2]+count-b.length)==b.length) {
                                    areas[2]++;
                                    added++;
                                }
                            }
                        } else {
                            while(c1<b.length*4&&(areas[0]+areas[2]+count-1-b.length<0||nextTileWhite)) {
                                c1++;
                                moves.add("-1 "+i);
                                nextTileWhite=move(b,"-1 "+i,nextTileWhite);
                                count++;
                                if(count>=b.length) count-=b.length;
                            }
                            while(c1<b.length*4&&(nextTileWhite||b[areas[0]+areas[2]+count-1-b.length][i])) {
                                c1++;
                                moves.add((areas[0]+areas[2]+count-1-b.length)+" -1");
                                nextTileWhite=move(b,(areas[0]+areas[2]+count-1-b.length)+" -1",nextTileWhite);
                                while(areas[0]+areas[2]+count-1<b.length&&count(b,areas[0]+areas[2]+count-1)==b.length) {
                                    areas[2]++;
                                    added++;
                                }
                                while(areas[0]+areas[2]+count-1>=b.length&&count(b,areas[0]+areas[2]+count-1-b.length)==b.length) {
                                    areas[2]++;
                                    added++;
                                }
                            }
                        }
                    }
                }
                for(int k=0;k<=areas[0]+areas[2]-min-1-added+count;k++) {
                    moves.add(b.length+" "+i);
                    nextTileWhite=move(b,b.length+" "+i,nextTileWhite);
                }
            } else {
                for(int j=0;j<=max-areas[0]+added;j++) {
                    int c1=0;
                    moves.add(b.length+" "+i);
                    nextTileWhite=move(b,b.length+" "+i,nextTileWhite);
                    if(points.contains(areas[0]+added+j)) {
                        while(c1<b.length*4&&nextTileWhite) {
                            c1++;
                            moves.add(b.length+" "+i);
                            nextTileWhite=move(b,b.length+" "+i,nextTileWhite);
                            count++;
                            if(count>=b.length) count-=b.length;
                        }
                        if(areas[0]-count-1>=0) {
                            while(c1<b.length*4&&(nextTileWhite||b[areas[0]-count-1][i])) {
                                c1++;
                                moves.add((areas[0]-count-1)+" -1");
                                nextTileWhite=move(b,(areas[0]-count-1)+" -1",nextTileWhite);
                                while(areas[0]-count-1>=0&&count(b,areas[0]-count-1)==b.length) {
                                    areas[0]--;
                                    added++;
                                }
                                while(areas[0]-count-1<0&&count(b,areas[0]-count-1+b.length)==b.length) {
                                    areas[0]--;
                                    added++;
                                }
                            }
                        } else {
                            while(c1<b.length*4&&(areas[0]-count+b.length>=b.length||nextTileWhite)) {
                                c1++;
                                moves.add(b.length+" "+i);
                                nextTileWhite=move(b,b.length+" "+i,nextTileWhite);
                                count++;
                                if(count>=b.length) count-=b.length;
                            }
                            while(c1<b.length*4&&(nextTileWhite||b[areas[0]-count+b.length][i])) {
                                c1++;
                                moves.add((areas[0]-count+b.length)+" -1");
                                nextTileWhite=move(b,(areas[0]-count+b.length)+" -1",nextTileWhite);
                                while(areas[0]-count>=0&&count(b,areas[0]-count)==b.length) {
                                    areas[0]--;
                                    added++;
                                }
                                while(areas[0]-count<0&&count(b,areas[0]-count+b.length)==b.length) {
                                    areas[0]--;
                                    added++;
                                }
                            }
                        }
                    }
                }
                for(int k=0;k<=max-areas[0]+added+count;k++) {
                    moves.add("-1 "+i);
                    nextTileWhite=move(b,"-1 "+i,nextTileWhite);
                }
            }
        }
        areas[0]=smallX;
        areas[1]=smallY;
        areas[2]=bigX-smallX+1;
        areas[3]=bigY-smallY+1;
        if(areas[0]<0) areas[0]=0;
        if(areas[2]+areas[0]>b.length) areas[2]=b.length-areas[0];
        int currRow=areas[0]+areas[2];
        if(currRow>=b.length) {
            currRow=areas[0]-1;
        }
        if(currRow<0) {
            currRow=areas[0]+areas[2]-1;
        }
        int totalCount=0;
        for(int i=0;i<areas[1];i++) {
            int count=0;
            for(int j=areas[0];j<areas[0]+areas[2];j++) {
                if(b[j][i]) count++;
            }
            int c=0;
            boolean done=false;
            while(!done&&c<b.length*2&&count>0) {
                c++;
                if(b.length-areas[0]-areas[2]>areas[0]) {
                    while(areas[0]+areas[2]<b.length&&count(b[areas[0]+areas[2]])<b.length&&b[areas[0]+areas[2]][i]) {
                        moves.add((areas[0]+areas[2])+" -1");
                        nextTileWhite=move(b,(areas[0]+areas[2])+" -1",nextTileWhite);
                    }
                    while(nextTileWhite&&areas[0]+areas[2]<b.length-1&&count(b[areas[0]+areas[2]+1])<b.length) {
                        moves.add((areas[0]+areas[2]+1)+" -1");
                        nextTileWhite=move(b,(areas[0]+areas[2]+1)+" -1",nextTileWhite);
                    }
                    moves.add("-1 "+i);
                    nextTileWhite=move(b,"-1 "+i,nextTileWhite);
                    while(areas[0]+areas[2]<b.length&&count(b[areas[0]+areas[2]])<b.length&&b[areas[0]+areas[2]][i]) {
                        moves.add((areas[0]+areas[2])+" -1");
                        nextTileWhite=move(b,(areas[0]+areas[2])+" -1",nextTileWhite);
                    }
                } else {
                    while(areas[0]>0&&count(b[areas[0]-1])<b.length&&b[areas[0]-1][i]) {
                        moves.add((areas[0]-1)+" -1");
                        nextTileWhite=move(b,(areas[0]-1)+" -1",nextTileWhite);
                    }
                    while(nextTileWhite&&areas[0]-1>0&&count(b[areas[0]-2])<b.length&&b[areas[0]-2][i]) {
                        moves.add((areas[0]-2)+" -1");
                        nextTileWhite=move(b,(areas[0]-2)+" -1",nextTileWhite);
                    }
                    moves.add(b.length+" "+i);
                    nextTileWhite=move(b,b.length+" "+i,nextTileWhite);
                    while(areas[0]>0&&count(b[areas[0]-1])<b.length&&b[areas[0]-1][i]) {
                        moves.add((areas[0]-1)+" -1");
                        nextTileWhite=move(b,(areas[0]-1)+" -1",nextTileWhite);
                    }
                }
                while(nextTileWhite) {
                    if(count(b[currRow])==b[currRow].length) {
                        if(currRow>=areas[0]+areas[2]) {
                            currRow++;
                        } else {
                            currRow--;
                        }
                        if(currRow>=b.length) {
                            currRow=areas[0]-1;
                        }
                        if(currRow<0) {
                            currRow=areas[0]+areas[2]-1;
                        }
                    }
                    moves.add(currRow+" "+b.length);
                    nextTileWhite=move(b,currRow+" "+b.length,nextTileWhite);
                    if(i<b.length-1&&b[currRow][i+1]) {
                        if(currRow>=areas[0]+areas[2]) {
                            currRow++;
                        } else {
                            currRow--;
                        }
                        if(currRow>=b.length) {
                            currRow=areas[0]-1;
                        }
                        if(currRow<0) {
                            currRow=areas[0]+areas[2]-1;
                        }
                    }
                }
                count=0;
                for(int j=areas[0];j<areas[0]+areas[2];j++) {
                    if(b[j][i]) count++;
                }
            }
            while(nextTileWhite) {
                if(currRow>=b.length) {
                    currRow=areas[0]-1;
                }
                if(currRow<0) {
                    currRow=areas[0]+areas[2]-1;
                }
                moves.add(currRow+" -1");
                nextTileWhite=move(b,currRow+" -1",nextTileWhite);
                if(i<b.length-1&&b[currRow][i+1]) {
                    if(currRow>=areas[0]+areas[2]) {
                        currRow++;
                    } else {
                        currRow--;
                    }
                    if(currRow>=b.length) {
                        currRow=areas[0]-1;
                    }
                    if(currRow<0) {
                        currRow=areas[0]+areas[2]-1;
                    }
                }
            }
        }
        for(int i=areas[1]+areas[3];i<b.length;i++) {
            int count=0;
            for(int j=areas[0];j<areas[0]+areas[2];j++) {
                if(b[j][i]) count++;
            }
            int c=0;
            boolean done=false;
            while(!done&&c<b.length*10&&count>0) {
                c++;
                if(b.length-areas[0]-areas[2]>areas[0]) {
                    while(areas[0]+areas[2]<b.length&&count(b[areas[0]+areas[2]])<b.length&&b[areas[0]+areas[2]][i]) {
                        moves.add((areas[0]+areas[2])+" -1");
                        nextTileWhite=move(b,(areas[0]+areas[2])+" -1",nextTileWhite);
                    }
                    while(nextTileWhite&&areas[0]+areas[2]<b.length-1&&count(b[areas[0]+areas[2]+1])<b.length) {
                        moves.add((areas[0]+areas[2]+1)+" -1");
                        nextTileWhite=move(b,(areas[0]+areas[2]+1)+" -1",nextTileWhite);
                    }
                    moves.add("-1 "+i);
                    nextTileWhite=move(b,"-1 "+i,nextTileWhite);
                    int c4=0;
                    while(areas[0]+areas[2]<b.length&&count(b[areas[0]+areas[2]])<b.length&&b[areas[0]+areas[2]][i]) {
                        c4++;
                        moves.add((areas[0]+areas[2])+" -1");
                        nextTileWhite=move(b,(areas[0]+areas[2])+" -1",nextTileWhite);
                    }
                } else {
                    while(areas[0]>0&&count(b[areas[0]-1])<b.length&&b[areas[0]-1][i]) {
                        moves.add((areas[0]-1)+" -1");
                        nextTileWhite=move(b,(areas[0]-1)+" -1",nextTileWhite);
                    }
                    while(nextTileWhite&&areas[0]-1>0&&count(b[areas[0]-2])<b.length&&b[areas[0]-2][i]) {
                        moves.add((areas[0]-2)+" -1");
                        nextTileWhite=move(b,(areas[0]-2)+" -1",nextTileWhite);
                    }
                    moves.add(b.length+" "+i);
                    nextTileWhite=move(b,b.length+" "+i,nextTileWhite);
                    while(areas[0]>0&&count(b[areas[0]-1])<b.length&&b[areas[0]-1][i]) {
                        moves.add((areas[0]-1)+" -1");
                        nextTileWhite=move(b,(areas[0]-1)+" -1",nextTileWhite);
                    }
                }
                while(nextTileWhite) {
                    if(count(b[currRow])==b[currRow].length) {
                        if(currRow>=areas[0]+areas[2]) {
                            currRow++;
                        } else {
                            currRow--;
                        }
                        if(currRow==b.length) {
                            currRow=areas[0]-1;
                        }
                        if(currRow<0) {
                            currRow=areas[0]+areas[2]-1;
                        }
                    }
                    moves.add(currRow+" -1");
                    nextTileWhite=move(b,currRow+" -1",nextTileWhite);
                    if(i>0&&b[currRow][i-1]) {
                        if(currRow>=areas[0]+areas[2]) {
                            currRow++;
                        } else {
                            currRow--;
                        }
                        if(currRow>=b.length) {
                            currRow=areas[0]-1;
                        }
                        if(currRow<0) {
                            currRow=areas[0]+areas[2]-1;
                        }
                    }
                }
                count=0;
                for(int j=areas[0];j<areas[0]+areas[2];j++) {
                    if(b[j][i]) count++;
                }
            }
            while(nextTileWhite) {
                if(currRow>=b.length) {
                    currRow=areas[0]-1;
                }
                if(currRow<0) {
                    currRow=areas[0]+areas[2]-1;
                }
                moves.add(currRow+" -1");
                nextTileWhite=move(b,currRow+" -1",nextTileWhite);
                if(i>0&&b[currRow][i-1]) {
                    if(currRow>=areas[0]+areas[2]) {
                        currRow++;
                    } else {
                        currRow--;
                    }
                    if(currRow>=b.length) {
                        currRow=areas[0]-1;
                    }
                    if(currRow<0) {
                        currRow=areas[0]+areas[2]-1;
                    }
                }
            }
        }
        currRow=areas[0]+areas[2];
        if(currRow>=b.length) {
            currRow=areas[0]-1;
        }
        if(currRow<0) {
            currRow=areas[0]+areas[2]-1;
        }
        while(count(b[currRow])==b.length) {
            currRow++;
            if(currRow>=b.length) {
                currRow=areas[0]-1;
            }
            if(currRow<0) {
                currRow=areas[0]+areas[2]-1;
            }
        }
        int c=0;
        while(c<b.length*2&&!done(b,nextTileWhite)&&currRow<b.length-1&&count(b[currRow])>0&&!connected(b[currRow])) {
            c++;
            if(nextTileWhite) {
                moves.add((currRow+1)+" -1");
                nextTileWhite=move(b,(currRow+1)+" -1",nextTileWhite);
            } else {
                moves.add(currRow+" "+b.length);
                nextTileWhite=move(b,currRow+" "+b.length,nextTileWhite);
            }
        }
        c=0;
        while(c<b.length&&currRow<b.length-1&&!done(b,nextTileWhite)&&nextTileWhite) {
            c++;
            moves.add((currRow+1)+" -1");
            nextTileWhite=move(b,(currRow+1)+" -1",nextTileWhite);
        }
        for(int i=0;!done(b,nextTileWhite)&&i<areas[0];i++) {
            if(i<areas[0]&&i<currRow||i>currRow&&i>areas[0]+areas[2]) {
                while(i!=currRow&&count(b[i])>0) {
                    if(nextTileWhite) {
                        if(currRow>=b.length) {
                            currRow=areas[0]-1;
                        }
                        if(currRow<0) {
                            currRow=areas[0]+areas[2]-1;
                        }
                        moves.add(currRow+" -1");
                        nextTileWhite=move(b,currRow+" -1",nextTileWhite);
                        if(currRow>=areas[0]+areas[2]&&(currRow%2==(areas[0]+areas[2])%2&&
                            count(b[currRow])==b[currRow].length||currRow%2!=(areas[0]+areas[2])%2&&
                            count(b[currRow])>=1)||
                        currRow<areas[0]+areas[2]&&(currRow%2==(areas[0]-1)%2&&
                            count(b[currRow])==b[currRow].length||currRow%2!=(areas[0]-1)%2&&
                            count(b[currRow])>=1)) {
                            if(currRow>=areas[0]+areas[2]) {
                                currRow++;
                            } else {
                                currRow--;
                            }
                            if(currRow==b.length) {
                                currRow=areas[0]-1;
                            }
                        }
                    } else {
                        if(closerRight(b[i])) {
                            moves.add(i+" -1");
                            nextTileWhite=move(b,i+" -1",nextTileWhite);
                        } else {
                            moves.add(i+" "+b.length);
                            nextTileWhite=move(b,i+" "+b.length,nextTileWhite);
                        }
                    }
                }
                while(!done(b,nextTileWhite)&&nextTileWhite) {
                    if(currRow>=b.length) {
                        currRow=areas[0]-1;
                    }
                    if(currRow<0) {
                        currRow=areas[0]+areas[2]-1;
                    }
                    moves.add(currRow+" -1");
                    nextTileWhite=move(b,currRow+" -1",nextTileWhite);
                    if(count(b[currRow])==b[currRow].length) {
                        if(currRow>=areas[0]+areas[2]) {
                            currRow++;
                        } else {
                            currRow--;
                        }
                        if(currRow==b.length) {
                            currRow=areas[0]-1;
                        }
                    }
                }
            }
        }
        for(int i=b.length-1;!done(b,nextTileWhite)&&i>=0;i--) {
            if(i<areas[0]&&i<currRow||i>currRow&&i>areas[0]+areas[2]) {
                while(!done(b,nextTileWhite)&&i!=currRow&&count(b[i])>0) {
                    if(nextTileWhite) {
                        if(currRow>=b.length) {
                            currRow=areas[0]-1;
                        }
                        if(currRow<0) {
                            currRow=areas[0]+areas[2]-1;
                        }
                        moves.add(currRow+" -1");
                        nextTileWhite=move(b,currRow+" -1",nextTileWhite);
                        if(currRow>=areas[0]+areas[2]&&(currRow%2==(areas[0]+areas[2])%2&&
                            count(b[currRow])==b[currRow].length||currRow%2!=(areas[0]+areas[2])%2&&
                            count(b[currRow])>=1)||
                        currRow<areas[0]+areas[2]&&(currRow%2==(areas[0]-1)%2&&
                            count(b[currRow])==b[currRow].length||currRow%2!=(areas[0]-1)%2&&
                            count(b[currRow])>=1)) {
                            if(currRow>=areas[0]+areas[2]) {
                                currRow++;
                            } else {
                                currRow--;
                            }
                            if(currRow==b.length) {
                                currRow=areas[0]-1;
                            }
                        }
                    } else {
                        if(closerRight(b[i])) {
                            moves.add(i+" -1");
                            nextTileWhite=move(b,i+" -1",nextTileWhite);
                        } else {
                            moves.add(i+" "+b.length);
                            nextTileWhite=move(b,i+" "+b.length,nextTileWhite);
                        }
                    }
                }
                while(!done(b,nextTileWhite)&&nextTileWhite) {
                    if(currRow>=b.length) {
                        currRow=areas[0]-1;
                    }
                    if(currRow<0) {
                        currRow=areas[0]+areas[2]-1;
                    }
                    moves.add(currRow+" -1");
                    nextTileWhite=move(b,currRow+" -1",nextTileWhite);
                    if(count(b[currRow])==b[currRow].length) {
                        if(currRow>=areas[0]+areas[2]) {
                            currRow++;
                        } else {
                            currRow--;
                        }
                        if(currRow==b.length) {
                            currRow=areas[0]-1;
                        }
                    }
                }
            }
        }
        c=0;
        while(c<b.length*2&&!done(b,nextTileWhite)) {
            c++;
            if(nextTileWhite) {
                if(currRow>=areas[0]+areas[2]&&currRow>=1) {
                    moves.add(currRow-1+" -1");
                    nextTileWhite=move(b,currRow-1+" -1",nextTileWhite);
                } else if(currRow<b.length-1) {
                    moves.add(currRow+1+" -1");
                    nextTileWhite=move(b,currRow+1+" -1",nextTileWhite);
                } else {
                    moves.add(currRow+" -1");
                    nextTileWhite=move(b,currRow+" -1",nextTileWhite);
                }
                if(count(b[currRow])==b[currRow].length) {
                    if(currRow>=areas[0]+areas[2]) {
                        currRow--;
                    } else {
                        currRow++;
                    }
                    if(currRow==b.length) {
                        currRow=areas[0]-1;
                    }
                    if(currRow<0) {
                        currRow=areas[0]+areas[2];
                    }
                }
            } else {
                moves.add(currRow+" -1");
                nextTileWhite=move(b,currRow+" -1",nextTileWhite);
                while(count(b[currRow])<=0) {
                    if(currRow>=areas[0]+areas[2]) {
                        currRow++;
                    } else {
                        currRow--;
                    }
                    if(currRow==b.length) {
                        currRow=areas[0]-1;
                    }
                    if(currRow<0) {
                        currRow=areas[0]+areas[2]-1;
                    }
                }
            }
        }
        String[] ans=new String[0];
        if(done(b,nextTileWhite)) {
            ans=new String[moves.size()];
            for(int i=0;i<moves.size();i++) ans[i]=moves.get(i);
        }
        return ans;
    }
    boolean connected(boolean[] b) {
        int index=0;
        while(index<b.length&&b[index]) {
            index++;
        }
        while(index<b.length&&!b[index]) {
            index++;
        }
        return index==b.length;
    }

    boolean closerRight(boolean[] b) {
        if(count(b)==0) return true;
        int countLeft=0;
        int countRight=0;
        while(countLeft<b.length&&!b[countLeft]) countLeft++;
        while(countRight<b.length&&!b[b.length-countRight-1]) countRight++;
        return countRight<countLeft;
    }

    boolean allRight(boolean[] b) {
        int index=0;
        while(index<b.length&&b[index]) index++;
        while(index<b.length&&!b[index]) {
            index++;
            if(b[index]) return false;
        }
        return true;
    }
    int[][] colorSome(boolean[][] grid) {
        final int[] dr = {0, 1, 0, -1}, dc = {1, 0, -1, 0};
        int[][] g = new int[grid.length][grid[0].length];
        // find the first X, BFS from it and see whether there are any Xs left
        int totalCount=0;
        for(int i=0;i<grid.length;i++) for(int j=0;j<grid.length;j++) if(grid[i][j]) totalCount++;
        int color=1;
        for (int r0 = 0; r0 < grid.length; ++r0)
            for (int c0 = 0; c0 < grid[0].length; ++c0) {
                if (g[r0][c0]==0&&grid[r0][c0]) {
                    // start BFS from this point
                    int[] rs = new int[grid.length*grid[0].length];
                    int[] cs = new int[grid.length*grid[0].length];
                    int ns = 1;
                    rs[0] = r0;
                    cs[0] = c0;
                    g[r0][c0] = color;
                    for (int i = 0; i < ns; ++i) {
                        for (int k = 0; k < 4; ++k) {
                            if (rs[i] + dr[k] >= 0 && rs[i] + dr[k] < grid.length && 
                            cs[i] + dc[k] >= 0 && cs[i] + dc[k] < grid[0].length && 
                            g[rs[i] + dr[k]][cs[i] + dc[k]] == 0 && grid[rs[i] + dr[k]][cs[i] + dc[k]]) {
                                g[rs[i] + dr[k]][cs[i] + dc[k]] = color;
                                rs[ns] = rs[i] + dr[k];
                                cs[ns] = cs[i] + dc[k];
                                ++ns;
                            }
                        }
                    }
                    if(ns>totalCount/2) return g;
                    color++;
                }
        }
        return g;
    }
    
    int[][] color(boolean[][] grid) {
        final int[] dr = {0, 1, 0, -1}, dc = {1, 0, -1, 0};
        int[][] g = new int[grid.length][grid[0].length];
        // find the first X, BFS from it and see whether there are any Xs left
        int color=1;
        for (int r0 = 0; r0 < grid.length; ++r0)
            for (int c0 = 0; c0 < grid[0].length; ++c0) {
                if (g[r0][c0]==0&&grid[r0][c0]) {
                    if(r0>0&&g[r0-1][c0]>0) {
                        g[r0][c0]=g[r0-1][c0];
                    } else if(c0>0&&g[r0][c0-1]>0) {
                        g[r0][c0]=g[r0][c0-1];
                    } else if(r0<g.length-1&&g[r0+1][c0]>0) {
                        g[r0][c0]=g[r0+1][c0];
                    } else if(c0<g.length-1&&g[r0][c0+1]>0) {
                        g[r0][c0]=g[r0][c0+1];
                    }
                    if(g[r0][c0]==0) {
                        g[r0][c0]=color;
                        color++;
                    }
                    if(r0>0&&g[r0-1][c0]>0&&g[r0-1][c0]!=g[r0][c0]) {
                        int newColor=g[r0-1][c0];
                        for (int r1 = 0; r1 < grid.length; ++r1)
                            for (int c1 = 0; c1 < grid[0].length; ++c1) {
                                if(g[r1][c1]==newColor) {
                                    g[r1][c1]=g[r0][c0];
                                }
                            }
                    } else if(c0>0&&g[r0][c0-1]>0&&g[r0][c0-1]!=g[r0][c0]) {
                        int newColor=g[r0][c0-1];
                        for (int r1 = 0; r1 < grid.length; ++r1)
                            for (int c1 = 0; c1 < grid[0].length; ++c1) {
                                if(g[r1][c1]==newColor) {
                                    g[r1][c1]=g[r0][c0];
                                }
                            }
                    } else if(r0<g.length-1&&g[r0+1][c0]>0&&g[r0+1][c0]!=g[r0][c0]) {
                        int newColor=g[r0+1][c0];
                        for (int r1 = 0; r1 < grid.length; ++r1)
                            for (int c1 = 0; c1 < grid[0].length; ++c1) {
                                if(g[r1][c1]==newColor) {
                                    g[r1][c1]=g[r0][c0];
                                }
                            }
                    } else if(c0<g.length-1&&g[r0][c0+1]>0&&g[r0][c0+1]!=g[r0][c0]) {
                        int newColor=g[r0][c0+1];
                        for (int r1 = 0; r1 < grid.length; ++r1)
                            for (int c1 = 0; c1 < grid[0].length; ++c1) {
                                if(g[r1][c1]==newColor) {
                                    g[r1][c1]=g[r0][c0];
                                }
                            }
                    }
                }
        }
        return g;
    }

    int[][] colorSlow(boolean[][] grid) {
        final int[] dr = {0, 1, 0, -1}, dc = {1, 0, -1, 0};
        int[][] g = new int[grid.length][grid[0].length];
        // find the first X, BFS from it and see whether there are any Xs left
        int color=1;
        for (int r0 = 0; r0 < grid.length; ++r0)
            for (int c0 = 0; c0 < grid[0].length; ++c0) {
                if (g[r0][c0]==0&&grid[r0][c0]) {
                    // start BFS from this point
                    int[] rs = new int[grid.length*grid[0].length];
                    int[] cs = new int[grid.length*grid[0].length];
                    int ns = 1;
                    rs[0] = r0;
                    cs[0] = c0;
                    g[r0][c0] = color;
                    for (int i = 0; i < ns; ++i) {
                        for (int k = 0; k < 4; ++k) {
                            if (rs[i] + dr[k] >= 0 && rs[i] + dr[k] < grid.length && 
                            cs[i] + dc[k] >= 0 && cs[i] + dc[k] < grid[0].length && 
                            g[rs[i] + dr[k]][cs[i] + dc[k]] == 0 && grid[rs[i] + dr[k]][cs[i] + dc[k]]) {
                                g[rs[i] + dr[k]][cs[i] + dc[k]] = color;
                                rs[ns] = rs[i] + dr[k];
                                cs[ns] = cs[i] + dc[k];
                                ++ns;
                            }
                        }
                    }
                    color++;
                }
        }
        return g;
    }

    boolean done(boolean[][] grid, int i, int j, boolean nextTileWhite) {
        boolean[][] newGrid=new boolean[j-i+1][grid.length];
        for(int x=i;x<=j;x++) newGrid[x-i]=grid[x];
        if(j-i+1<=0) return false;
        return done(newGrid,nextTileWhite);
    }

    boolean done(boolean[][] grid, boolean nextTileWhite) {
        if(nextTileWhite) return false;
        final int[] dr = {0, 1, 0, -1}, dc = {1, 0, -1, 0};
        char[][] g = new char[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; ++i) {
            for(int j=0;j<grid[0].length;j++) {
                if(grid[i][j]) g[i][j]='X';
                else g[i][j]='.';
            }
        }
        // find the first X, BFS from it and see whether there are any Xs left
        boolean marked_connected = false;
        for (int r0 = 0; r0 < grid.length; ++r0)
            for (int c0 = 0; c0 < grid[0].length; ++c0) {
                if (g[r0][c0]=='X') {
                    if (marked_connected) {
                        return false;     // already marked the connected component => something is not connected to it
                    }
                    // start BFS from this point
                    int[] rs = new int[grid.length*grid[0].length];
                    int[] cs = new int[grid.length*grid[0].length];
                    int ns = 1;
                    rs[0] = r0;
                    cs[0] = c0;
                    g[r0][c0] = '+';
                    for (int i = 0; i < ns; ++i) {
                        for (int k = 0; k < 4; ++k) {
                            if (rs[i] + dr[k] >= 0 && rs[i] + dr[k] < grid.length && 
                            cs[i] + dc[k] >= 0 && cs[i] + dc[k] < grid[0].length && 
                            g[rs[i] + dr[k]][cs[i] + dc[k]] == 'X') {
                                g[rs[i] + dr[k]][cs[i] + dc[k]] = '+';
                                rs[ns] = rs[i] + dr[k];
                                cs[ns] = cs[i] + dc[k];
                                ++ns;
                            }
                        }
                    }
                    marked_connected = true;
                }
        }
        return true;
    }

    int count(boolean[][] grid,int col) {
        int count=0;
        for(int i=0;i<grid.length;i++) if(grid[i][col]) count++;
        return count;
    }

    int count(boolean[] row) {
        int count=0;
        for(int i=0;i<row.length;i++) if(row[i]) count++;
        return count;
    }

    int move(int[][] board,String m,int white) {
        StringTokenizer st=new StringTokenizer(m);
        int row=Integer.parseInt(st.nextToken());
        int col=Integer.parseInt(st.nextToken());
        int nextTileWhite=0;
        if(row==-1) {
            nextTileWhite=board[board.length-1][col];
            for(int i=board.length-1;i>0;i--)
                board[i][col]=board[i-1][col];
            board[0][col]=white;
        } else if(row==board.length) {
            nextTileWhite=board[0][col];
            for(int i=0;i<board.length-1;i++)
                board[i][col]=board[i+1][col];
            board[board.length-1][col]=white;
        } else if(col==-1) {
            nextTileWhite=board[row][board[0].length-1];
            for(int i=board[0].length-1;i>0;i--)
                board[row][i]=board[row][i-1];
            board[row][0]=white;
        } else {
            nextTileWhite=board[row][0];
            for(int i=0;i<board[0].length-1;i++)
                board[row][i]=board[row][i+1];
            board[row][board[0].length-1]=white;
        }
        return nextTileWhite;
    }

    boolean move(boolean[][] board,String m,boolean white) {
        StringTokenizer st=new StringTokenizer(m);
        int row=Integer.parseInt(st.nextToken());
        int col=Integer.parseInt(st.nextToken());
        boolean nextTileWhite=true;
        if(row==-1) {
            nextTileWhite=board[board.length-1][col];
            for(int i=board.length-1;i>0;i--)
                board[i][col]=board[i-1][col];
            board[0][col]=white;
        } else if(row==board.length) {
            nextTileWhite=board[0][col];
            for(int i=0;i<board.length-1;i++)
                board[i][col]=board[i+1][col];
            board[board.length-1][col]=white;
        } else if(col==-1) {
            nextTileWhite=board[row][board[0].length-1];
            for(int i=board[0].length-1;i>0;i--)
                board[row][i]=board[row][i-1];
            board[row][0]=white;
        } else {
            nextTileWhite=board[row][0];
            for(int i=0;i<board[0].length-1;i++)
                board[row][i]=board[row][i+1];
            board[row][board[0].length-1]=white;
        }
        return nextTileWhite;
    }
}