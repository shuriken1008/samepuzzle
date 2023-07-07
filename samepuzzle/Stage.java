package samepuzzle;

import java.util.Random;
import java.util.Scanner;

public class Stage {
    public static int PINK = 0xff4bb3;
    public static int ORANGE = 0xd67d00;
    public static int PURPLE = 0x6a2199;
    public static int GREEN = 0x1a6700;
    public static int BLUE = 0x0064d9;


    private int plot[][];
    private int stageCount = 0;

    public Stage(int size){
        plot = new int[size][size];
    }


    public void generateNewStage(){
        Random rand = new Random();

        for(int x=0; x<plot.length; x++){
            for(int y=0; y<plot.length; y++){
                plot[x][y] = rand.nextInt(5) + 1;
            }
        }
    }

    public int breakBlock(int x, int y){
        int totalBlocks = 0;
        int blockId;

        if(isBreakable(x, y)){
            blockId = plot[x][y];

            totalBlocks = 1;
            plot[x][y] = 0;
            totalBlocks += breakBlock(x+1, y, x, y, blockId);
            totalBlocks += breakBlock(x-1, y, x, y, blockId);
            totalBlocks += breakBlock(x, y+1, x, y, blockId);
            totalBlocks += breakBlock(x, y-1, x, y, blockId);
            


        }
        updatePlot();
        return totalBlocks;
    }
    private int breakBlock(int x, int y, int beforeX, int beforeY, int blockId){
        int totalBlocks = 0;
        if(
            x<0 || x >= plot.length ||
            y<0 || y >= plot[x].length
        ){return 0;}

        if(plot[x][y] == blockId){
            
        

            totalBlocks = 1;
            plot[x][y] = 0;
            if(x+1 != beforeX){
                totalBlocks += breakBlock(x+1, y, x, y, blockId);
            }
            if(x-1 != beforeX){
                totalBlocks += breakBlock(x-1, y, x, y, blockId);
            }
            if(y+1 != beforeY){
                totalBlocks += breakBlock(x, y+1, x, y, blockId);
            }
            if(y-1 != beforeY){
                totalBlocks += breakBlock(x, y-1, x, y, blockId);
            }
            
        }
        return totalBlocks;
    }


    public boolean isBreakable(int x, int y){
        if(
            x<0 || x >= plot.length ||
            y<0 || y >= plot[x].length
        ){return false;}

        if(plot[x][y] == 0){
            return false;
        }

        int blockId = plot[x][y];
        if(
            isBreakable(x+1, y, blockId) || isBreakable(x-1, y, blockId) ||
            isBreakable(x, y+1, blockId) || isBreakable(x, y-1, blockId)

        ){
            return true;
        }else{
            return false;
        }
        
    }
    public boolean isBreakable(int x, int y, int blockId){
        if(
            x<0 || x >= plot.length ||
            y<0 || y >= plot[x].length
        ){return false;}

        if(plot[x][y] == blockId){
            return true;
        }

        return false;
    }

public static int[] moveZerosToStart(int[] array) {
        int[] result = new int[array.length];
        int index = result.length - 1;
        
        // 0以外の要素を新しい配列の末尾から詰めていく
        for (int i = array.length - 1; i >= 0; i--) {
            if (array[i] != 0) {
                result[index] = array[i];
                index--;
            }
        }
        
        // 残りの要素に0を詰める
        while (index >= 0) {
            result[index] = 0;
            index--;
        }
        
        return result;
    }

    public void updatePlot(){
        for(int y=0; y<plot.length; y++){
            int[] result = new int[plot.length];
            int index = result.length - 1;
            // 0以外の要素を新しい配列の末尾から詰めていく
            for (int x = plot.length - 1; x >= 0; x--) {
                if (plot[x][y] != 0) {
                    result[index] = plot[x][y];
                    index--;
                }
            }
            // 残りの要素に0を詰める
            while (index >= 0) {
                result[index] = 0;
                index--;
            }

            for(int x =0;x<plot.length; x++){
                plot[x][y] = result[x];
            }
        }
    }

    public void isGameOver(){
        for(int x=0; x<plot.length; x++){
            for(int y=0; y<plot.length; y++){
                if(plot[x][y] == 0){
                    continue;
                }else{

                }
            }
        }
    }

    public void showStdOut(){
        for(int x=0; x<plot.length; x++){
            for(int y:plot[x]){
                String t = "";
                switch(y){
                    case(0)->{
                        t = "×";
                    }
                    case(1)->{
                        t = "■";
                    }
                    case(2)->{
                        t = "\u001b[31m■\u001b[0m";
                    }
                    case(3)->{
                        t = "\u001b[32m■\u001b[0m";
                    }
                    case(4)->{
                        t = "\u001b[33m■\u001b[0m";
                    }
                    case(5)->{
                        t = "\u001b[34m■\u001b[0m";
                    }
                }
                System.out.print(t);
            }
            System.out.println();
        }
    }


    //DEBUG
    public static void main(String[] args){
        Stage s = new Stage(10);

        s.generateNewStage();
        s.showStdOut();
        
        Scanner scan;
        String str;
        int x;
        int y;
        while(true){
            scan = new Scanner(System.in);
            str = scan.nextLine();
            
            
            String[] ls = str.split(",");

            x = Integer.valueOf(ls[0]);
            y = Integer.valueOf(ls[1]);

            s.breakBlock(x,y);
            s.showStdOut();
            System.out.println();
            
        }
    }
}
