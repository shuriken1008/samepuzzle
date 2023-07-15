
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SameGameServer {
    private static final int PORT = 8888;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started. Waiting for connections...");

            Socket client1 = serverSocket.accept();
            System.out.println("Client 1 connected.");
            Socket client2 = serverSocket.accept();
            System.out.println("Client 2 connected.");

            startGame(client1, client2);

            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startGame(Socket client1, Socket client2) {
        try {
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(client1.getInputStream()));
            PrintWriter writer1 = new PrintWriter(client1.getOutputStream(), true);
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(client2.getInputStream()));
            PrintWriter writer2 = new PrintWriter(client2.getOutputStream(), true);

            // ゲームの初期化などを実装

            while (true) {
                // クライアント1に現在のゲーム状態を送信
                // クライアント1からの操作を受け取る
                String move1 = reader1.readLine();
                // move1を処理してゲーム状態を更新

                // クライアント2に現在のゲーム状態を送信
                // クライアント2からの操作を受け取る
                String move2 = reader2.readLine();
                // move2を処理してゲーム状態を更新

                // ゲーム状態をクライアントに送信
                writer1.println(getGameState());
                writer2.println(getGameState());

                // ゲーム終了条件が満たされた場合はループを終了
                // または、エラーが発生した場合はループを終了
                if (isGameOver() || isError()) {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client1.close();
                client2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getGameState() {
        // 現在のゲーム状態を文字列で返す
        // 例えば、ブロックの配置やスコアなどを表す
        return "";
    }

    private static boolean isGameOver() {
        // ゲーム終了条件を判定し、終了している場合はtrueを返す
        return false;
    }

    private static boolean isError() {
        // エラーが発生したかどうかを判定し、エラーがある場合はtrueを返す
        return false;
    }
}
