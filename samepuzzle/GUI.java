package samepuzzle;

import javax.swing.*;

import samepuzzle.GUI.GamePanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.TimerTask;
import java.util.Timer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Random;



public class GUI extends JFrame {
    private static final int WINDOW_X = 800;
    private static final int WINDOW_Y = 600;
    private static final int NUM_ROWS = 20;
    private static final int NUM_COLS = 20;
    private static final int BLOCK_SIZE = 20;
    private static final int BOARD_X = 10;
    private static final int BOARD_Y = 70;

    private static final int OFFSET = 2;

    private int[][] board;
    private boolean[][] visited;
    private int score;

    private HashSet<Point> lastConnectedBlocks = new HashSet<>();

    private JLabel scoreLabel;
    private JPanel selectBlockPanel;
    private JPanel boardPanel;

    private Score s = new Score();

    private Sound SESelect = new Sound("./media/select.wav");
    private Sound SEBrake = new Sound("./media/b2.wav");


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
                //setContentPane(new GamePanel());
                revalidate();
            }
        });
    }

    public static String dataToString(int[][] data) {
        String str = "";
        for (int[] x : data) {
            for (int i : x) {
                str += i;
            }
        }

        return str;
    }

    // Title
    class TitleScreenPanel extends JPanel {
        private JTextField playerTextField;
        private JTextField exroomTextField;

        public TitleScreenPanel() {
            setLayout(new BorderLayout());

            JPanel inputPanel = new JPanel(new GridLayout(3, 2));

            JLabel titleLabel = new JLabel("SameGame");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

            JLabel playerLabel = new JLabel("プレイヤー名：");
            playerTextField = new JTextField();

            JLabel roomLabel = new JLabel("部屋名：");
            exroomTextField = new JTextField();

            JButton startButton = new JButton("ゲームを開始");
            startButton.addActionListener(e -> startGame());

            inputPanel.add(titleLabel);
            inputPanel.add(new JLabel());
            inputPanel.add(playerLabel);
            inputPanel.add(playerTextField);
            inputPanel.add(roomLabel);
            inputPanel.add(exroomTextField);

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(startButton);

            add(inputPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        private void startGame() {
            String playerName = playerTextField.getText();
            String exroomName = exroomTextField.getText();

            if (!playerName.isEmpty() && !exroomName.isEmpty()) {
                displayName = playerName;
                roomName = exroomName;

                setContentPane(new GamePanel());
                setContentPane(new WaitingPanel(playerName, exroomName));
                revalidate();
            } else {
                JOptionPane.showMessageDialog(this, "プレイヤー名と部屋名を入力してください。");
            }
        }
    }


    class WaitingPanel extends JPanel {
        private String playerName;
        private String exroomName;
        private volatile boolean gameStarted = false;

        public WaitingPanel(String playerName, String exroomName) {
            this.playerName = playerName;
            this.exroomName = roomName;
            setLayout(new BorderLayout());

            JLabel waitingLabel = new JLabel("マッチング中...");
            
            //font arialだと文字化け->メイリオで解決※windows以外だとどうなるか不明
            waitingLabel.setFont(new Font("メイリオ", Font.BOLD, 36));
            
            waitingLabel.setHorizontalAlignment(SwingConstants.CENTER);

            JLabel playerLabel = new JLabel("プレイヤー名：" + playerName);
            JLabel roomLabel = new JLabel("部屋名：" + exroomName);

            JButton readyButton = new JButton("準備完了！");    
            readyButton.addActionListener(new ActionListener(){
                private boolean isReady = false;
                public void actionPerformed(ActionEvent e) {
                    readyButton.setText(isReady? "取り消す": "準備完了！");
                    JOptionPane.showMessageDialog(null, "Hello, Event Test!");
                    try {
                        client.myData.setIsReady(true);
                        client.sendMyData();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            });

            JPanel contentPanel = new JPanel(new GridLayout(3, 1));
            contentPanel.add(waitingLabel);
            contentPanel.add(playerLabel);
            contentPanel.add(roomLabel);
            contentPanel.add(readyButton);

            add(contentPanel, BorderLayout.CENTER);

            new Thread(() -> {
                while (!gameStarted) {
                    try {
                        gameStarted = client.checkGameFlag();
                        System.out.println(gameStarted);
                        Thread.sleep(1000); //1 secoundごとにチェック
                        //
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                SwingUtilities.invokeLater(() -> {
                    Timer timer = new Timer(false);
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            //ゲームスタート
                            System.out.println("game start!");
                            setContentPane(new GamePanel());
                            revalidate();
                            timer.cancel();
                        }
                    };
                    Long epoch = client.myData.getGameStartTime();
                    Date d = new Date(epoch*1000);
                    timer.schedule(task,d);
                    System.out.println("game start at " + d.toString());
                });
            }).start();
        }
    }


    // Game Panel
    class GamePanel extends JPanel {
        private boolean gameFinish = false;


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

                                //ブロックデータ送信(最初にクリックした座標)
                                
                                try {
                                    client.sendBreakPos(row, col);
                                } catch (IOException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
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

        private void showResultPanel() {
            ResultPanel resultPanel = new ResultPanel(score);
            remove(boardPanel);
            add(resultPanel, BorderLayout.CENTER);
            revalidate();
        }


        //result
        class ResultPanel extends JPanel {
            private int finalScore;

            public ResultPanel(int score) {
                this.finalScore = score;

                setLayout(new BorderLayout());

                JLabel resultLabel = new JLabel("Finish!");
                resultLabel.setFont(new Font("Arial", Font.BOLD, 36));
                resultLabel.setHorizontalAlignment(SwingConstants.CENTER);

                JLabel scoreLabel = new JLabel("Final Score: " + score);
                scoreLabel.setFont(new Font("Arial", Font.PLAIN, 24));
                scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);

                JButton restartButton = new JButton("Restart Game");
                restartButton.addActionListener(e -> restartGame());

                JPanel contentPanel = new JPanel(new GridLayout(3, 1));
                contentPanel.add(resultLabel);
                contentPanel.add(scoreLabel);
                contentPanel.add(restartButton);

                add(contentPanel, BorderLayout.CENTER);
            }

            private void restartGame() {
                initializeBoard();
                setContentPane(new TitleScreenPanel());
                revalidate();
            }
        }


        public void receiveBlockData(int[][] data) {
            for (int row = 0; row < NUM_ROWS; row++) {
                System.arraycopy(data[row], 0, board[row], 0, NUM_COLS);
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
            connectToServer(displayName, roomName);

            JPanel scorePanel = new JPanel();

            JLabel nameLabel = new JLabel("Player: " + displayName);
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

            ((JLabel) selectBlockPanel.getComponent(0)).setFont(new Font("メイリオ", Font.PLAIN, 24));
            ((JLabel) selectBlockPanel.getComponent(1)).setFont(new Font("メイリオ", Font.PLAIN, 24));

            add(selectBlockPanel);
        }

        @Override
        public void paint(Graphics g) {
            //((JLabel) selectBlockPanel.getComponent(0)).setText("ブロック: " + connectedBlocks.size());
            ((JLabel) selectBlockPanel.getComponent(1)).setText("スコア: " + score);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, WINDOW_X, WINDOW_Y);

            //gameFinishになったらresult表示
            if(gameFinish){
                showResultPanel();
                return;
            }

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
    class ChatPanel extends JPanel {
        public ChatPanel() {
            setBounds((int) (WINDOW_X * 0.6), (int) (WINDOW_Y * 0.8), 300, 100);

            setBackground(new Color(.5f, .8f, .5f, .5f));
            add(new JButton(">>>"));
        }
    }

    //スコア・ランキング・選択中のブロックなど
    class StatusPanel extends JPanel {

    }

    //メニュー画面(おまけ)
    class MenuPanel extends JPanel {

    }


    //背景画像表示
    //コードは書いたがどこにも実装はしていない
    //image.pngはファイル名/URLに変更
    //memo↓
    //BackImgPanel backImgPanel = new BackImgPanel();
    //            add(backImgPanel);
    class BackImgPanel extends JPanel {
        private Image backgroundImage;

        public void BackImPanel() {
            try {
                backgroundImage = ImageIO.read(new File("./media/back.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    //ゲーム画面の外枠(おまけ)
    class GamePanelFrame extends JPanel {

    }

    public void showReStartMessage() {
        JOptionPane.showMessageDialog(this, "No more removable blocks. ReStart.");
    }


    public void connectToServer(String displayName, String roomName) {

        try {
            client = new Client(displayName,roomName);
            client.connect();

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
