package samepuzzle;

import javax.swing.*;

import samepuzzle.GUI.GamePanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
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

    private boolean hasConnected = false;

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
    BGImageLayeredPane layerPane;


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
        layerPane = new BGImageLayeredPane();
        //
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
        Image backgroundImage;

        public TitleScreenPanel() {
            setLayout(new GridBagLayout()); // Use GridBagLayout for more control over component sizes and positions
                        
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);

            JLabel titleLabel = new JLabel("SamePuzzle");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

            JLabel playerLabel = new JLabel("プレイヤー名：");
            playerLabel.setFont(new Font("メイリオ", Font.PLAIN, 24)); // Update font size here
            playerTextField = new JTextField();
            playerTextField.setPreferredSize(new Dimension(200, 40)); // Set the desired size here

            JLabel roomLabel = new JLabel("部屋名：");
            roomLabel.setFont(new Font("メイリオ", Font.PLAIN, 24)); // Update font size here
            exroomTextField = new JTextField();
            exroomTextField.setPreferredSize(new Dimension(200, 40)); // Set the desired size here


            JButton startButton = new JButton("ゲームを開始");
            startButton.setPreferredSize(new Dimension(200, 60));
            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // 画面を切り替える

                    new Thread(()->{
                        //add(new ChatPanel());
                        startButton.setEnabled(false);
                        startGame();
                        startButton.setEnabled(false);

                    }).start();
                }
            });

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            add(titleLabel, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 1;
            gbc.fill = GridBagConstraints.NONE;
            add(playerLabel, gbc);

            gbc.gridx = 1;
            gbc.gridy = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            add(playerTextField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.fill = GridBagConstraints.NONE;
            add(roomLabel, gbc);

            gbc.gridx = 1;
            gbc.gridy = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            add(exroomTextField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            add(startButton, gbc);
        }

        private void startGame() {
            String playerName = playerTextField.getText();
            String exroomName = exroomTextField.getText();

            if (!playerName.isEmpty() && !exroomName.isEmpty()) {
                displayName = playerName;
                roomName = exroomName;

                connectToServer(playerName, exroomName);

                Timer timer = new Timer(false);
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        //部屋に移動


                        //c.add(new ChatPanel());

                        setContentPane(new WaitingPanel(playerName, exroomName));
                        repaint();
                        revalidate();
                        timer.cancel();

                    }
                };
                timer.schedule(task,10);

            } else {
                JOptionPane.showMessageDialog(this, "プレイヤー名と部屋名を入力してください。");
            }
        }
    }


    class WaitingPanel extends JPanel {
        private String playerName;
        private String exroomName;
        private volatile boolean gameStarted = false;
        private volatile boolean gameEnd = false;

        public WaitingPanel(String playerName, String exroomName) {
            this.playerName = playerName;
            this.exroomName = roomName;
            setLayout(new BorderLayout());
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));


            JLabel waitingLabel = new JLabel("<html>マッチング中...<br><h3>プレイヤー</h3></html>");

            //font arialだと文字化け->メイリオで解決※windows以外だとどうなるか不明
            waitingLabel.setFont(new Font("メイリオ", Font.BOLD, 36));

            waitingLabel.setHorizontalAlignment(SwingConstants.CENTER);

            JLabel playerLabel = new JLabel("プレイヤー名：" + playerName + "/" + "部屋名：" + exroomName);
            playerLabel.setFont(new Font("メイリオ", Font.BOLD, 28));
            playerLabel.setHorizontalAlignment(SwingConstants.CENTER);


            JButton readyButton = new JButton("準備完了！");
            readyButton.setPreferredSize(new Dimension(20, 10));
            readyButton.setAlignmentX(SwingConstants.CENTER);

            JPanel contentPanel = new JPanel(new GridLayout(3, 1));
            contentPanel.add(waitingLabel);
            contentPanel.add(playerLabel);
            contentPanel.add(readyButton);

            add(contentPanel, BorderLayout.CENTER);



            readyButton.addActionListener(new ActionListener(){
                private boolean isReady = client.myData.isReady();
                public void actionPerformed(ActionEvent e) {
                    readyButton.setText(isReady? "取り消す": "準備完了！");
                    //JOptionPane.showMessageDialog(null, "マッチング中");
                    try {

                        client.myData.setIsReady(!isReady);
                        client.sendMyData();
                        readyButton.setText(!isReady? "取り消す": "準備完了！");
                        if(client.myData.isGameEnded()){
                        }else{

                            readyButton.setEnabled(false);
                        }

                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            });

            contentPanel.add(waitingLabel);
            contentPanel.add(playerLabel);
            contentPanel.add(readyButton);

            add(contentPanel, BorderLayout.CENTER);

            new Thread(() -> {
                while (!gameStarted) {
                    try {
                        //プレイヤーの表示
                        String t = "<html>";
                        for(Player p : client.myRoom.getAllPlayers()){
                            t += p.getDisplayName() + "    "
                                    + (p.isReady() ? "準備ok!" : "") +  "<br>";
                        }
                        t += "</html>";
                        playerLabel.setText(t);
                        gameStarted = client.checkGameFlag();
                        gameEnd = client.checkGameFlag();
                        System.out.println(gameStarted);
                        Thread.sleep(1000); //1 secoundごとにチェック
                        //
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                SwingUtilities.invokeLater(() -> {
                    readyButton.setEnabled(false);
                    Timer timer = new Timer(false);
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {

                            //ゲームスタート
                            waitingLabel.setText("右クリックでスタート！！");
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
                    //まもなく開始します
                    waitingLabel.setText("まもなく開始します…");
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
                    //GameOverチェック
                    if(!client.checkGameFlag()){
                        System.out.println("gameover");
                        showResultPanel();

                    }
                    boolean[][] _visited = copyVisited(visited);
                    if (row >= 0 && row < NUM_ROWS && col >= 0 && col < NUM_COLS && board[row][col] != 0) {
                        HashSet<Point> connectedBlocks = findConnectedBlocks(row, col);
                        ((JLabel) selectBlockPanel.getComponent(0)).setText(
                                "<html>ブロック: " + connectedBlocks.size() + "　　　　" + "スコア" + client.myData.getScore() + "　　　　"
                                        + client.myData.getRank() + "位</html>"
                        );
                        //((JLabel) selectBlockPanel.getComponent(1)).setText("スコア: " + client.myData.getScore());


                        boolean f = connectedBlocks.equals(lastConnectedBlocks);

                        if (f) {
                            if (connectedBlocks.size() >= 2) {
                                SEBrake.playWav();
                                removeBlocks(connectedBlocks);
                                compressBoard();
                                repaint();
                                //ブロックデータ送信(最初にクリックした座標)
                                try {
                                    client.sendBreakPos(row, col, client.myData.getScore());

                                } catch (IOException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }

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

        private void showResultPanel() {
            gameFinish = true;
            remove(boardPanel);
            //add(new ResultPanel(score), BorderLayout.CENTER);
            new Thread(
                    () -> {

                        setContentPane(new ResultPanel(score));
                        revalidate();
                    })
                    .start();

        }

        //result
        class ResultPanel extends JPanel {
            private int finalScore;
            private JButton restartButton;

            public ResultPanel(int score) {
                //自分のスコア
                //順位
                int rank = client.myData.getRank();
                rank = rank == 0 ? rank: 1;
                //ハイスコア
                int hiScore = client.myRoom.getHiscore();
                //ハイスコアプレイヤー
                String hiScorePlayer = client.myRoom.getWinner();
                Player _p = client.myRoom.getPlayer(hiScorePlayer);
                if(_p == null){ _p = client.myRoom.getPlayer(client.myData.getUUID());}
                String winnerName = _p.getDisplayName();

                this.finalScore = score;

                setLayout(new BorderLayout());

                JLabel resultLabel = new JLabel("GameOver!");
                resultLabel.setFont(new Font("Arial", Font.BOLD, 36));
                resultLabel.setHorizontalAlignment(SwingConstants.CENTER);

                JLabel scoreLabel = new JLabel("Final Score: " + score);
                scoreLabel.setFont(new Font("Arial", Font.PLAIN, 24));
                scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);

                JLabel rankLabel = new JLabel("Rank : " + rank + "位");
                rankLabel.setHorizontalAlignment(SwingConstants.CENTER);
                rankLabel.setFont(new Font("メイリオ", Font.PLAIN, 24));

                JLabel winnerLabel = new JLabel("Winner : " + winnerName + "  (" + hiScore + "点)");
                winnerLabel.setFont(new Font("メイリオ", Font.PLAIN, 24));
                winnerLabel.setHorizontalAlignment(SwingConstants.CENTER);


                restartButton = new JButton("Restart Game");
                restartButton.addActionListener(e -> restartGame());



                JPanel contentPanel = new JPanel(new GridLayout(3, 1));
                contentPanel.add(resultLabel);
                contentPanel.add(scoreLabel);
                contentPanel.add(rankLabel);
                contentPanel.add(winnerLabel);
                contentPanel.add(restartButton);

                add(contentPanel, BorderLayout.CENTER);
            }

            private void restartGame() {
                restartButton.setEnabled(false);
                new Thread(
                        () -> {

                            setContentPane(new TitleScreenPanel());
                            revalidate();
                            initializeBoard();
                        })
                        .start();

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
        public int[][] copyBoard(int[][] arr2D) {
            int[][] _arr = new int[NUM_ROWS][NUM_COLS];
            for (int x = 0; x < NUM_ROWS; x++) {
                for (int y = 0; y < NUM_COLS; y++) {
                    _arr[x][y] = arr2D[x][y];
                }
            }
            return _arr;
        }
        public boolean compareBoard(int[][] a, int[][] b){
            for (int x = 0; x < NUM_ROWS; x++) {
                for (int y = 0; y < NUM_COLS; y++) {
                    if(a[x][y] != b[x][y]){
                        return false;
                    }
                }
            }
            return true;
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

            client.myData.addScore(s.calc(blocks.size()));
        }

        private void compressBoard() {
            int[][] _before = copyBoard(board);
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

            if(!compareBoard(_before, board)){
                compressBoard();
            }
        }

        private void createScoreLabel() {
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
    class ChatPanel extends JLayeredPane {
        public ChatPanel() {

            setBounds((int) (WINDOW_X * 0.6), (int) (WINDOW_Y * 0.8), 300, 100);

            JTextArea textarea = new JTextArea(10, 40);
            JScrollPane scrollpane = new JScrollPane(textarea);
            textarea.setEditable(false);
            add(textarea);
            add(new JButton(">>>"));
            setBackground(new Color(.5f, .8f, .5f, .5f));

        }
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
            if (client == null){
                client = new Client(displayName,roomName);
                client.connect();
            }else{
                client.disconnect();
                client.myData.setRoomName(roomName);
                client.myData.setDisplayName(displayName);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    // 背景画像を描画する JLayeredPane
    class BGImageLayeredPane extends JLayeredPane {
        BGImageLayeredPane() {}

        void setImage(Image img) {
            bgImage = img;
        }
        Image bgImage;

        @Override public void paint(Graphics g) {
            if (bgImage != null) {
                int imageh = bgImage.getHeight(null);
                int imagew = bgImage.getWidth(null);

                Dimension d = getSize();
                for (int h = 0; h < d.getHeight(); h += imageh) {
                    for (int w = 0; w < d.getWidth(); w += imagew) {
                        g.drawImage(bgImage, w, h, this);
                    }
                }
            }
            super.paint(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUI game = new GUI();
            game.setVisible(true);
        });
    }
}
