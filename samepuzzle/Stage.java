package samepuzzle;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

import javax.swing.SwingUtilities;



public class Stage {
    public static int PINK = 0xff4bb3;
    public static int ORANGE = 0xd67d00;
    public static int PURPLE = 0x6a2199;
    public static int GREEN = 0x1a6700;
    public static int BLUE = 0x0064d9;
    private static final int NUM_ROWS = 20;
    private static final int NUM_COLS = 20;
    private static final int BLOCK_SIZE = 20;
    private static final int BOARD_X = 10;
    private static final int BOARD_Y = 35;

    private static final int WINDOW_X = 800;
    private static final int WINDOW_Y = 600;

    private static final int OFFSET = 2;

    private HashSet<Point> lastConnectedBlocks = new HashSet<>();


    private static int board[][];
    private static boolean visited[][];
    private Score s = new Score();

    private int score = 0;

    private int stageLevel = 0;
    private int stageSize;


    public Stage(int size){
        this.stageSize = size;
        board= new int[size][size];
    }
    public Stage(int size, int stageLevel){
        this.stageSize = size;
        this.stageLevel = stageLevel;
        board= new int[size][size];
    }
    public Stage(int size, int stageLevel, int[][] stageData){
        this.stageSize = size;
        this.stageLevel = stageLevel;
        board= stageData;
    }


    public void generateNewStage(){
        Random rand = new Random();

        for(int x=0; x<board.length; x++){
            for(int y=0; y<board.length; y++){
                board[x][y] = rand.nextInt(5) + 1;
            }
        }
    }

    public void importStage(int d[][]){
        board= d.clone();
    }

    public int[][] exportStage(){
        return board.clone();
    }

    public int breakBlock(int x, int y){
        int totalBlocks = 0;
        int blockId;

        if(isBreakable(x, y)){
            blockId = board[x][y];

            totalBlocks = 1;
            board[x][y] = 0;
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
            x<0 || x >= board.length ||
            y<0 || y >= board[x].length
        ){return 0;}

        if(board[x][y] == blockId){
            
        

            totalBlocks = 1;
            board[x][y] = 0;
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
            x<0 || x >= board.length ||
            y<0 || y >= board[x].length
        ){return false;}

        if(board[x][y] == 0){
            return false;
        }

        int blockId = board[x][y];
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
            x<0 || x >= board.length ||
            y<0 || y >= board[x].length
        ){return false;}

        if(board[x][y] == blockId){
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
        for(int y=0; y<board.length; y++){
            int[] result = new int[board.length];
            int index = result.length - 1;
            // 0以外の要素を新しい配列の末尾から詰めていく
            for (int x = board.length - 1; x >= 0; x--) {
                if (board[x][y] != 0) {
                    result[index] = board[x][y];
                    index--;
                }
            }
            // 残りの要素に0を詰める
            while (index >= 0) {
                result[index] = 0;
                index--;
            }

            for(int x =0;x<board.length; x++){
                board[x][y] = result[x];
            }
        }
    }

    public void isGameOver(){
        for(int x=0; x<board.length; x++){
            for(int y=0; y<board.length; y++){
                if(board[x][y] == 0){
                    continue;
                }else{

                }
            }
        }
    }

    public void showStdOut(){
        for(int x=0; x<board.length; x++){
            for(int y:board[x]){
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
    private HashSet<Point> findConnectedBlocks(int startRow, int startCol) {
        int color = board[startRow][startCol];
        visited[startRow][startCol] = true;

        HashSet<Point> connectedBlocks = new HashSet<>();
        connectedBlocks.add(new Point(startRow, startCol));

        if (startRow - 1 >= 0 && board[startRow - 1][startCol] == color && !visited[startRow - 1][startCol]) {
            connectedBlocks.addAll(findConnectedBlocks(startRow - 1, startCol));
        }
        if (startRow + 1 < NUM_ROWS && board[startRow + 1][startCol] == color && !visited[startRow + 1][startCol]) {
            connectedBlocks.addAll(findConnectedBlocks(startRow + 1, startCol));
        }
        if (startCol - 1 >= 0 && board[startRow][startCol - 1] == color && !visited[startRow][startCol - 1]) {
            connectedBlocks.addAll(findConnectedBlocks(startRow, startCol - 1));
        }
        if (startCol + 1 < NUM_COLS && board[startRow][startCol + 1] == color && !visited[startRow][startCol + 1]) {
            connectedBlocks.addAll(findConnectedBlocks(startRow, startCol + 1));
        }

        return new HashSet<>(connectedBlocks);
    }

    public void removeBlocks(HashSet<Point> blocks) {
        for (Point block : blocks) {
            int row = block.x;
            int col = block.y;
            board[row][col] = 0;
            visited[row][col] = false;
        }
        score += s.calc(blocks.size());
    }

    private void compressBoard() {
        for (int col = 0; col < NUM_COLS; col++) {
            int emptyRow = NUM_ROWS - 1;

            for (int row = NUM_ROWS - 1; row >= 0; row--) {
                if (board[row][col] != 0) {
                    board[emptyRow][col] = board[row][col];
                    emptyRow--;
                }
            }

            while (emptyRow >= 0) {
                board[emptyRow][col] = 0;
                emptyRow--;
            }
        }

        for (int col = 0; col < NUM_COLS; col++) {
            int emptyRow = NUM_ROWS - 1;
            boolean columnIsEmpty = true;

            for (int row = NUM_ROWS - 1; row >= 0; row--) {
                if (board[row][col] != 0) {
                    board[emptyRow][col] = board[row][col];
                    emptyRow--;
                    columnIsEmpty = false;
                }
            }

            if (columnIsEmpty) {
                for (int shiftCol = col + 1; shiftCol < NUM_COLS; shiftCol++) {
                    for (int row = 0; row < NUM_ROWS; row++) {
                        board[row][shiftCol - 1] = board[row][shiftCol];
                        board[row][shiftCol] = 0;
                    }
                }
            } else {
                for (int row = emptyRow; row >= 0; row--) {
                    board[row][col] = 0;
                }
            }
        }
    }

    private boolean checkAdjacentBlocks(int row, int col) {
        int color = board[row][col];

        if (row - 1 >= 0 && board[row - 1][col] == color) {
            return false;
        }
        if (row + 1 < NUM_ROWS && board[row + 1][col] == color) {
            return false;
        }
        if (col - 1 >= 0 && board[row][col - 1] == color) {
            return false;
        }
        if (col + 1 < NUM_COLS && board[row][col + 1] == color) {
            return false;
        }

        return true;
    }


    public static String dataToString(){
        String str = "";
        for(int[] x: board){
            for(int i: x){
                str+=i;
            }
        }

        return str;
    }

    public String toJson(){
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
