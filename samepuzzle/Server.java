package samepuzzle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.Queue;
/*
サーバーの起動
cd ./src
javac --enable-preview --source 20 TCPServer.java
java --enable-preview TCPServer
クライアントの起動
cd ./src
javac --enable-preview --source 20 TcpClient.java
java --enable-preview TCPClient
 */

/**
 * チャット コンソールアプリ の サーバー
 */

public class Server {
    public static int portNumber = 1234;
    private Rooms rooms = new Rooms();

    //<threadid, thread>
    private static HashMap<Long, Server1ClientThread> serverThreadMap = new HashMap<>();

    private static HashMap<String, String> uuidToThreadIdMap = new HashMap<>();
    
    private static ArrayList<Server1ClientThread> serverThreadArrayList = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        
        final ServerSocket serverSocket = new ServerSocket(portNumber);
        

        try {
            while (true) {
                System.out.println("新たなクライアントとの接続を待機しています");
                final Socket socket = serverSocket.accept();
                System.out.println("新たにクライアントと接続しました!");
                // クライアントからメッセージを受け取るスレッド
                final Server1ClientThread lastServerThread = new Server1ClientThread(socket,
                        // 1つのクライアントからメッセージが来た
                        (message, clientId) -> {
                            listener(clientId, message);
                        },
                        // 1つのクライアントとの接続が切れた
                        (disconnected) -> {
                            try {
                                sendMessageToAllClient(disconnected + "さんが退出しました", serverThreadArrayList);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });

                serverThreadMap.put(lastServerThread.getId(), lastServerThread);
                lastServerThread.start();
                
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void listener(long clientId, String message){
        try {
            HashMap<String, Object> map = Json.toHashMap(message);
            if(map == null){
                return;
            }

            
            switch((String)map.get("type")){
                case("playerData")->{
                    onPlayerData(map);
                }
                case("chat")->{
                    sendMessageToAllClient(message, serverThreadArrayList);
                }
                case("connect")->{
                    onPlayerConnect(map);
                }
                case("disconnect")->{
                    onPlayerDisconnect(map);
                }
                case("status")->{
                    onStatus(map);
                }
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void onPlayerData(HashMap<String, Object> map){

    }

    public static void onPlayerConnect(HashMap<String, Object> map){

    }

    public static void onPlayerDisconnect(HashMap<String, Object> map){

    }

    public static void onStatus(HashMap<String, Object> map){

    }



    /**
     * サーバーに接続しているすべてのクライアントにメッセージを送信する
     */
    static public void sendMessageToAllClient(String message, ArrayList<Server1ClientThread> serverThreadArrayList)
            throws IOException {
        for (final Server1ClientThread serverThread : serverThreadArrayList) {
            if (!serverThread.isDisconnected) {
                serverThread.sendDataToClient(message);
            }
        }
    }
}

/**
 * 1つのクライアントからメッセージを受け取り, 送信するためのスレッド
 */
class Server1ClientThread extends Thread {
    final Socket socket;

    //ラムダ式で２つの入力を受け付けるクラス
    final BiConsumer<String, Long> handler;

    final Consumer<Long> onDisconnect;
    boolean isDisconnected = false;

    ObjectOutputStream serverToClientStream = null;

    Server1ClientThread(
            final Socket socket,
            final BiConsumer<String, Long> handler,
            final Consumer<Long> onDisconnect) {
        System.out.println("Server1ClientThreadを起動します");
        this.socket = socket;
        this.handler = handler;
        this.onDisconnect = onDisconnect;
    }

    @Override
    public void run() {
        try {
            serverToClientStream = new ObjectOutputStream(socket.getOutputStream());

            final ObjectInputStream clientToServerStream = new ObjectInputStream(socket.getInputStream());
            while (true) {
                final String clientToServerData = clientToServerStream.readUTF();
                logWithId("クライアントから " + clientToServerData + "を受け取りました");
                handler.accept(clientToServerData, getId());
            }
        } catch (IOException e) {
            isDisconnected = true;
            onDisconnect.accept(getId());
        }
    }

    /**
     * クライアントにデータを送信する
     */
    public void sendDataToClient(final String message) throws IOException {
        // まだ接続していないときは, 送信しない
        if (serverToClientStream == null) {
            return;
        }
        serverToClientStream.writeUTF(message);
        serverToClientStream.flush();
    }

    private void logWithId(final String message) {
        System.out.println("[Server1ClientThread id: " + getId() + "] " + message);
    }
}