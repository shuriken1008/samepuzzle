import java.io.*;
import java.net.Socket;

public class SameGameClient {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 8888;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("Connected to server.");

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            // ゲームのメインループ
            while (true) {
                // サーバーから現在のゲーム状態を受信
                String gameState = reader.readLine();

                // ゲーム状態を描画
                System.out.println(gameState);

                // プレイヤーの入力を受け付け、サーバーに送信
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Enter your move (row col): ");
                String move = consoleReader.readLine();
                writer.println(move);

                // ゲーム終了条件が満たされた場合はループを終了
                // または、エラーが発生した場合はループを終了
                if (gameState.equals("Game Over") || gameState.startsWith("Error")) {
                    break;
                }
            }

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
