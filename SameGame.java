package samegame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SameGame extends JFrame {
    private static final int NUM_ROWS = 10;
    private static final int NUM_COLS = 10;
    private static final int BLOCK_SIZE = 20;
    private static final int BOARD_X = 10;
    private static final int BOARD_Y = 60;

    private int[][] board;
    private boolean[][] visited;
    private int score;

    private JLabel scoreLabel;

    public SameGame() {
        setTitle("SameGame");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(BOARD_X + NUM_COLS * BLOCK_SIZE + 100, BOARD_Y + NUM_ROWS * BLOCK_SIZE + 100);
        setResizable(false);
        setLocationRelativeTo(null);

        setContentPane(new TitleScreenPanel());
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                setContentPane(new GamePanel());
                revalidate();
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
                board[row][col] = random.nextInt(5) + 1;
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

    private void removeBlocks(List<Point> blocks) {
        for (Point block : blocks) {
            int row = block.x;
            int col = block.y;
            board[row][col] = 0;
            visited[row][col] = false;
        }
        score += blocks.size();
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

    private class TitleScreenPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 36));

            Dimension size = getSize();
            FontMetrics fm = g.getFontMetrics();

            int x = (size.width - fm.stringWidth("SameGame")) / 2;
            int y = (size.height - fm.getHeight()) / 2 + fm.getAscent();

            g.drawString("SameGame", x, y);

            g.setFont(new Font("Arial", Font.PLAIN, 20));
            fm = g.getFontMetrics();

            x = (size.width - fm.stringWidth("Click to start")) / 2;
            y += fm.getHeight() + 20;

            g.drawString("Click to start", x, y);
        }
    }


    private class GamePanel extends JPanel {
        public GamePanel() {
            initializeBoard();
            createScoreLabel();

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    int row = (e.getY() - BOARD_Y) / BLOCK_SIZE;
                    int col = (e.getX() - BOARD_X) / BLOCK_SIZE;

                    if (row >= 0 && row < NUM_ROWS && col >= 0 && col < NUM_COLS && board[row][col] != 0) {
                        List<Point> connectedBlocks = findConnectedBlocks(row, col);
                        if (connectedBlocks.size() >= 2) {
                            removeBlocks(connectedBlocks);
                            compressBoard();
                            scoreLabel.setText("Score: " + score);
                            repaint();
                        } else if (checkAdjacentBlocks(row, col)) {
                            boolean allBlocksAreAdjacent = true;

                            // すべてのブロックが赤い枠に囲まれているかチェックする
                            for (int r = 0; r < NUM_ROWS; r++) {
                                for (int c = 0; c < NUM_COLS; c++) {
                                    if (board[r][c] != 0 && !checkAdjacentBlocks(r, c)) {
                                        allBlocksAreAdjacent = false;
                                        break;
                                    }
                                }
                                if (!allBlocksAreAdjacent) {
                                    break;
                                }
                            }

                            if (allBlocksAreAdjacent) {
                                showReStartMessage();
                                initializeBoard();
                                repaint();
                            }
                        }
                    }
                }
                });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            for (int row = 0; row < NUM_ROWS; row++) {
                for (int col = 0; col < NUM_COLS; col++) {
                    int blockColor = board[row][col];
                    int x = BOARD_X + col * BLOCK_SIZE;
                    int y = BOARD_Y + row * BLOCK_SIZE;

                    if (blockColor != 0) {
                        g.setColor(getColor(blockColor));
                        g.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
                        g.setColor(Color.BLACK);
                        g.drawRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
                    }
                }
            }
        }
    }

    private void createScoreLabel() {
        String playerName = JOptionPane.showInputDialog(this, "Enter your name:");

        JPanel scorePanel = new JPanel();

        JLabel nameLabel = new JLabel("Player: " + playerName);
        nameLabel.setHorizontalAlignment(SwingConstants.LEFT);

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        scorePanel.add(nameLabel);
        scorePanel.add(scoreLabel);

        scorePanel.setBounds(BOARD_X, 0, NUM_COLS * BLOCK_SIZE, BOARD_Y);
        add(scorePanel);
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

    private void showReStartMessage() {
        JOptionPane.showMessageDialog(this, "No more removable blocks. ReStart.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SameGame game = new SameGame();
            game.setVisible(true);
        });
    }
}
