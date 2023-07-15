package samepuzzle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GUI extends JFrame {
    private static final int NUM_ROWS = 20;
    private static final int NUM_COLS = 20;
    private static final int BLOCK_SIZE = 20;
    private static final int BOARD_X = 10;
    private static final int BOARD_Y = 35;

    private static final int OFFSET = 2;

    private int[][] board;
    private boolean[][] visited;
    private int score;

    private JLabel scoreLabel;

    private Score s = new Score();
    public GUI() {
        setTitle("SameGame");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(BOARD_X + NUM_COLS * BLOCK_SIZE+100, BOARD_Y + NUM_ROWS * BLOCK_SIZE+100);
        setResizable(false);
        setLocationRelativeTo(null);

        initializeBoard();
        createScoreLabel();

        //クリック
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = (e.getY() - BOARD_Y) / (BLOCK_SIZE + OFFSET);
                int col = (e.getX() - BOARD_X) / (BLOCK_SIZE + OFFSET);

                if (row >= 0 && row < NUM_ROWS && col >= 0 && col < NUM_COLS && board[row][col] != 0) {
                    List<Point> connectedBlocks = findConnectedBlocks(row, col);
                    if (connectedBlocks.size() >= 2) {
                        removeBlocks(connectedBlocks);
                        compressBoard();
                        scoreLabel.setText("Score: " + score);
                        repaint();
                        
                    }
                }
            }
        });
    }

    private void initializeBoard() {
        Random random = new Random();
        board = new int[NUM_ROWS][NUM_COLS];
        visited = new boolean[NUM_ROWS][NUM_COLS];
        score = 0;

        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                board[row][col] = random.nextInt(5) + 1; // Generate a random number between 1 and 5
                visited[row][col] = false;
            }
        }
    }

    private List<Point> findConnectedBlocks(int startRow, int startCol) {
        int color = board[startRow][startCol];
        visited[startRow][startCol] = true;

        List<Point> connectedBlocks = new ArrayList<>();
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

        return connectedBlocks;
    }

    public void removeBlocks(List<Point> blocks) {
        for (Point block : blocks) {
            int row = block.x;
            int col = block.y;
            board[row][col] = 0;
            visited[row][col] = false;
        }
        //score += blocks.size();
        score += s.calc(blocks.size());
        showStdOut();

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
            boolean columnIsEmpty = true;  // 列が空かどうかを示すフラグ

            for (int row = NUM_ROWS - 1; row >= 0; row--) {
                if (board[row][col] != 0) {
                    board[emptyRow][col] = board[row][col];
                    emptyRow--;
                    columnIsEmpty = false;
                }
            }

            if (columnIsEmpty) {
                // 列が空の場合、左隣の列を左に詰める
                for (int shiftCol = col + 1; shiftCol < NUM_COLS; shiftCol++) {
                    for (int row = 0; row < NUM_ROWS; row++) {
                        board[row][shiftCol - 1] = board[row][shiftCol];
                        board[row][shiftCol] = 0;
                    }
                }
            } else {
                // 列が空でない場合、残りの空行を0で埋める
                for (int row = emptyRow; row >= 0; row--) {
                    board[row][col] = 0;
                }
            }
        }
    }

    private void createScoreLabel() {
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setBounds(BOARD_X, 0, NUM_COLS * BLOCK_SIZE, BOARD_Y);
        scoreLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(scoreLabel);
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);


        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                int blockColor = board[row][col];
                int y = BOARD_Y + row * (BLOCK_SIZE + OFFSET);
                int x = BOARD_X + col * (BLOCK_SIZE + OFFSET);

                if (blockColor != 0) {
                    g.setColor(getColor(blockColor));
                    g.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
                    g.setColor(Color.BLACK);
                    g.drawRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
                }
            }
        }
    }

    private Color getColor(int blockColor) {
        switch (blockColor) {
            case 1:
                return Color.RED;
            case 2:
                return Color.GREEN;
            case 3:
                return Color.BLUE;
            case 4:
                return Color.YELLOW;
            case 5:
                return Color.ORANGE;
            default:
                return Color.WHITE;
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
            System.out.println("");
            
        }
        System.out.println("------------------------");
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUI game = new GUI();
            game.setVisible(true);
        });


    }
}


