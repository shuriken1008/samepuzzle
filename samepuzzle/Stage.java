package samepuzzle;

import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import javax.swing.SwingUtilities;



public class Stage {
    public static int PINK = 0xff4bb3;
    public static int ORANGE = 0xd67d00;
    public static int PURPLE = 0x6a2199;
    public static int GREEN = 0x1a6700;
    public static int BLUE = 0x0064d9;


    private static int data[][];
    private int stageCount = 0;
    private int stageLevel = 0;
    private int stageSize;


    public Stage(int size){
        this.stageSize = size;
        data = new int[size][size];
    }
    public Stage(int size, int stageLevel){
        this.stageSize = size;
        this.stageLevel = stageLevel;
        data = new int[size][size];
    }


    public void generateNewStage(){
        Random rand = new Random();

        for(int x=0; x<data.length; x++){
            for(int y=0; y<data.length; y++){
                data[x][y] = rand.nextInt(5) + 1;
            }
        }
    }

    public void importStage(int d[][]){
        data = d.clone();
    }

    public int[][] exportStage(){
        return data.clone();
    }

    public int breakBlock(int x, int y){
        int totalBlocks = 0;
        int blockId;

        if(isBreakable(x, y)){
            blockId = data[x][y];

            totalBlocks = 1;
            data[x][y] = 0;
            totalBlocks += breakBlock(x+1, y, x, y, blockId);
            totalBlocks += breakBlock(x-1, y, x, y, blockId);
            totalBlocks += breakBlock(x, y+1, x, y, blockId);
            totalBlocks += breakBlock(x, y-1, x, y, blockId);
            


        }
        updatedata();
        System.out.println(dataToString());
        return totalBlocks;
    }
    private int breakBlock(int x, int y, int beforeX, int beforeY, int blockId){
        int totalBlocks = 0;
        if(
            x<0 || x >= data.length ||
            y<0 || y >= data[x].length
        ){return 0;}

        if(data[x][y] == blockId){
            
        

            totalBlocks = 1;
            data[x][y] = 0;
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
            x<0 || x >= data.length ||
            y<0 || y >= data[x].length
        ){return false;}

        if(data[x][y] == 0){
            return false;
        }

        int blockId = data[x][y];
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
            x<0 || x >= data.length ||
            y<0 || y >= data[x].length
        ){return false;}

        if(data[x][y] == blockId){
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

    public void updatedata(){
        for(int y=0; y<data.length; y++){
            int[] result = new int[data.length];
            int index = result.length - 1;
            // 0以外の要素を新しい配列の末尾から詰めていく
            for (int x = data.length - 1; x >= 0; x--) {
                if (data[x][y] != 0) {
                    result[index] = data[x][y];
                    index--;
                }
            }
            // 残りの要素に0を詰める
            while (index >= 0) {
                result[index] = 0;
                index--;
            }

            for(int x =0;x<data.length; x++){
                data[x][y] = result[x];
            }
        }
    }

    public void isGameOver(){
        for(int x=0; x<data.length; x++){
            for(int y=0; y<data.length; y++){
                if(data[x][y] == 0){
                    continue;
                }else{

                }
            }
        }
    }

    public void showStdOut(){
        for(int x=0; x<data.length; x++){
            for(int y:data[x]){
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

    public static String dataToString(){
        String str = "";
        for(int[] x: data){
            for(int i: x){
                str+=i;
            }
        }

        return str;
    }

    public String toJson(int stageLevel){
        return Json.toJson(new HashMap<>(){{
            put("type", "blockData");
            put("stageLevel", stageLevel);
            put("stageSize", stageSize);
            put("data", dataToString());
        }});
    }

    //DEBUG
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            GUI game = new GUI();
            game.setVisible(true);
        });

        
        
        
    }
}
