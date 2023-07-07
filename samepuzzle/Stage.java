package samepuzzle;

import java.util.Random;

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
            totalBlocks += breakBlock(x+1, y, x, y, blockId);
            totalBlocks += breakBlock(x-1, y, x, y, blockId);
            totalBlocks += breakBlock(x, y+1, x, y, blockId);
            totalBlocks += breakBlock(x, y-1, x, y, blockId);
            plot[x][y] = 0;


        }

        return totalBlocks;
    }
    public int breakBlock(int x, int y, int beforeX, int beforeY, int blockId){
        int totalBlocks = 0;
        
        if(isBreakable(x, y, blockId)){
            totalBlocks = 1;
            if(x+1 != beforeX){
                totalBlocks += breakBlock(x+1, y, x, y, blockId);
            }
            if(x-11 != beforeX){
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



    public static void main(String[] args){
        Stage s = new Stage(100);

        s.new
    }
}
