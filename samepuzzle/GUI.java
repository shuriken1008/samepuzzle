package samepuzzle;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class GUI extends JFrame {
    private static final int NUM_ROWS = 20;
    private static final int NUM_COLS = 20;
    private static final int BLOCK_SIZE = 20;
    private static final int BOARD_X = 10;
    private static final int BOARD_Y = 35;

    private static final int WINDOW_X = 800;
    private static final int WINDOW_Y = 600;    

    private static final int OFFSET = 2;

    private int[][] board;
    private boolean[][] visited;
    private int score;

    private HashSet<Point> lastConnectedBlocks = new HashSet<>();

    private JLabel scoreLabel;
    private JPanel selectBlockPanel;
    private JPanel boardPanel;

    private Score s = new Score();

    private Sound SESelect = new Sound("./SE/select.wav");
    private Sound SEBrake = new Sound("./SE/b2.wav");


    private String displayName;
    private String roomName;
    private String roomPass;

    private Client client;

    public GUI() {
        setTitle("セイムパズル");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setSize(BOARD_X + NUM_COLS * BLOCK_SIZE + 100, BOARD_Y + NUM_ROWS * BLOCK_SIZE + 100);
        setSize(WINDOW_X, WINDOW_Y);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(new ChatPanel());
        add(new TitleScreenPanel());
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                setContentPane(new GamePanel());
                revalidate();
            }
        });
    }

    public static String dataToString(int[][] data){
        String str = "";
        for(int[] x: data){
            for(int i: x){
                str+=i;
            }
        }

        return str;
    }

    // Title
    class TitleScreenPanel extends JPanel {
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

            //ボタン配置

            //テキストボックスPlayer

            //text- RoomName
        }
    }

    // Game Panel
    class GamePanel extends JPanel {
        public GamePanel() {
            initializeBoard();
            createScoreLabel();
            createSelBlockPanel();

            // Click event
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    int row = (e.getY() - BOARD_Y) / (BLOCK_SIZE + OFFSET);
                    int col = (e.getX() - BOARD_X) / (BLOCK_SIZE + OFFSET);

                    boolean[][] _visited = copyVisited(visited);

                    if (row >= 0 && row < NUM_ROWS && col >= 0 && col < NUM_COLS && board[row][col] != 0) {
                        HashSet<Point> connectedBlocks = findConnectedBlocks(row, col);
                        ((JLabel) selectBlockPanel.getComponent(0)).setText("ブロック: " + connectedBlocks.size());
                        //((JLabel) selectBlockPanel.getComponent(1)).setText("スコア: " + score);

                        boolean f = connectedBlocks.equals(lastConnectedBlocks);

                        if (f) {
                            if (connectedBlocks.size() >= 2) {
                                SEBrake.playWav();
                                removeBlocks(connectedBlocks);
                                compressBoard();


                                repaint();
                            } else if (checkAdjacentBlocks(row, col)) {
                                boolean allBlocksAreAdjacent = true;

                                // Check if all blocks are surrounded by red border
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
                        } else {
                            SESelect.playWav();
                            visited = copyVisited(_visited);
                        }

                        lastConnectedBlocks = new HashSet<Point>(connectedBlocks);
                    }
                }
            });
        }

        public void receiveBlockData(int[][] data){
            for(int row = 0; row < NUM_ROWS; row++){
                System.arraycopy(data[row],0,board[row],0,NUM_COLS);
            }
            compressBoard();
            ((JLabel) selectBlockPanel.getComponent(1)).setText("スコア: " + score);
            repaint();
        }
        public boolean[][] copyVisited(boolean[][] arr2D) {
            boolean[][] _arr = new boolean[NUM_ROWS][NUM_COLS];
            for (int x = 0; x < NUM_ROWS; x++) {
                for (int y = 0; y < NUM_COLS; y++) {
                    _arr[x][y] = arr2D[x][y];
                }
            }
            return _arr;
        }

        private void initializeBoard() {
            Random random = new Random();
            board = new int[NUM_ROWS][NUM_COLS];
            visited = new boolean[NUM_ROWS][NUM_COLS];
            score = 0;

            boardPanel = new JPanel();

            for (int row = 0; row < NUM_ROWS; row++) {
                for (int col = 0; col < NUM_COLS; col++) {
                    board[row][col] = random.nextInt(5) + 1; // Generate a random number between 1 and 5
                    visited[row][col] = false;
                }
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

        private void createScoreLabel() {
            String playerName = JOptionPane.showInputDialog(this, "名前を入力してください。");
            String roomName = JOptionPane.showInputDialog(this, "部屋名を入力してください。");
            
            connectToServer(playerName, roomName);
            



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

        private void createSelBlockPanel() {
            selectBlockPanel = new JPanel();
            selectBlockPanel.add(new JLabel("ブロック： 0"));
            selectBlockPanel.add(new JLabel("スコア： 0"));
            selectBlockPanel.setBounds(BOARD_X, 0, NUM_COLS * BLOCK_SIZE, BOARD_Y - 10);

            add(selectBlockPanel);
        }

        @Override
        public void paint(Graphics g) {
            //((JLabel) selectBlockPanel.getComponent(0)).setText("ブロック: " + connectedBlocks.size());
            ((JLabel) selectBlockPanel.getComponent(1)).setText("スコア: " + score);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, BOARD_X + NUM_COLS * BLOCK_SIZE + 100, BOARD_Y + NUM_ROWS * BLOCK_SIZE + 100);

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
    }


    //チャット欄(重ねて表示)
    class ChatPanel extends JPanel{
        public ChatPanel(){
            setBounds((int)(WINDOW_X*0.6), (int)(WINDOW_Y*0.8), 300, 100);

            setBackground(new Color(.5f, .8f, .5f, .5f));
            add(new JButton(">>>"));
        }
    }

    //スコア・ランキング・選択中のブロックなど
    class StatusPanel extends JPanel{

    }

    //メニュー画面(おまけ)
    class MenuPanel extends JPanel{

    }


    //背景画像表示
    class BackImgPanel extends JPanel{
        
    }

    //ゲーム画面の外枠(おまけ)
    class GamePanelFrame extends JPanel{

    }

    public void showReStartMessage() {
        JOptionPane.showMessageDialog(this, "No more removable blocks. ReStart.");
    }


    public void connectToServer(String displayName, String roomName){
        
        try {
            client = new Client(displayName);
            client.connect(roomName);
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUI game = new GUI();
            game.setVisible(true);
        });
    }
}
