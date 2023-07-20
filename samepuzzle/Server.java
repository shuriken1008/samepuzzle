package samepuzzle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
    public static int portNumber = 12344;
    private static Rooms rooms = new Rooms();

    //<threadid, thread>
    private static HashMap<Long, Server1ClientThread> serverThreadMap = new HashMap<>();

    private static HashMap<String, Long> uuidToThreadIdMap = new HashMap<>();
    
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
                                //sendMessageToAllClient(disconnected + "さんが退出しました", serverThreadArrayList);
                                try {
                                    onDisconnect(disconnected);
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
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
                case("breakData")->{
                    onBreakData(map);
                }
                case("chat")->{
                    sendMessageToAllClient(message, serverThreadArrayList);
                }
                case("connect")->{
                    onPlayerConnect(clientId, map);
                }
                case("disconnect")->{
                    onPlayerDisconnect(map);
                }
                
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //準備OK/NGを受け取る
    public static void onPlayerData(HashMap<String, Object> map){
        Player newP = new Player();
        newP.setFromMap(map);
        Room r = rooms.getRoom(newP.getRoomName());
        //System.out.println(r.getName());
        
        Player p = r.getPlayer(newP.getUUID());
        //System.out.println("newP" + newP.getIsReady());
        p.setIsReady(newP.isReady());

        gameStart(r);

    }

    public static void onPlayerConnect(long threadId, HashMap<String, Object> map) throws IOException{
        String roomName = (String)map.get("roomName");
        String displayName = (String)map.get("displayName");
        String uuid = (String)map.get("uuid");
        uuidToThreadIdMap.put(uuid, threadId);
        //部屋を検索
        Room r = rooms.getRoom(roomName);
        Player p = new Player(displayName);
        p.setUUID(uuid);
        p.setRoomName(roomName);


        r.addPlayer(p);

        //PLデータを全員に送信
        for(Player _p: r.getAllPlayers()){
            sendDataToRoomMember(_p.toJson(), r);
        }
    }

    public static void onBreakData(HashMap<String, Object> map){
        String roomName = (String)map.get("roomName");
        Room r = rooms.getRoom(roomName);
        
        String uuid = (String)map.get("uuid");
        int score = Integer.parseInt((String)map.get("score"));
        int x = Integer.parseInt((String)map.get("x"));
        int y = Integer.parseInt((String)map.get("y"));

        //不正チェック(実装しない)

        //スコア代入
        Player p = r.getPlayer(uuid);
        p.setScore(score);
        
        
        //スコア判定
        if(isGameOver(r)){
            GameEnd(r);
        }

        for(Player _p : r.getAllPlayers()){
            try {
                sendDataToRoomMember(_p.toJson(), r);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public static void onPlayerDisconnect(HashMap<String, Object> map){
        String uuid = (String)map.get("uuid");
        Room r = rooms.getRoomFromPlayer(uuid);
        Player p = r.getPlayer(uuid);
        r.removePlayer(p);

        String json = Json.toJson(new HashMap<String, Object>(){{
            put("type", "disconnect");
            put("uuid", uuid);
        }});
        
        try {
            sendDataToRoomMember(json, r);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void onDisconnect (Long threadId)throws IOException{
        String uuid = threadIdToUUID(threadId, uuidToThreadIdMap);
        
        // System.out.println("uuid:" + uuid);
        
        Room r = rooms.getRoomFromPlayer(uuid);
        Player p = r.getPlayer(uuid);
        r.removePlayer(p);

        String json = Json.toJson(new HashMap<String, Object>(){{
            put("type", "disconnect");
            put("uuid", uuid);
        }});
        
        sendDataToRoomMember(json, r);
        

    }

    
    public static String threadIdToUUID(Long value, HashMap<String, Long> map) { 
        /** * valueからkeyを取得（逆引き） * * @param value 値 (String) * @return キー (String) */
        for (String key : map.keySet()) { 
            if (map.get(key).equals(value)) { 
                return key;
            }
        } 
        return null;
    }

    public boolean checkGameStart(Room r){
        for(Player p : r.getAllPlayers()){
            System.out.println(p.getDisplayName() + "," + p.isReady());
            if(!p.isReady()){
                return false;
            }
        }

        return true;
    }
    public static void gameStart(Room r){
        //System.out.println(r.getAllPlayers());



        LocalDateTime nowDate = LocalDateTime.now();
        Long startAt = nowDate.plusSeconds(10).toEpochSecond(ZoneOffset.ofHours(9));
        //開始命令送信
        HashMap<String, Object> map = new HashMap<>(){{
            put("type", "gameStart");
            put("startAt", startAt);
            put("targetScore", r.getTargetScore());
        }};

        try{
            sendDataToRoomMember(Json.toJson(map), r);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static boolean isGameOver(Room r){
        int targetScore = r.getTargetScore();
        for(Player p : r.getAllPlayers()){
            if (targetScore <= p.getScore()){
                return true;
            }else{
                continue;
            }
        }
        return false;
    }

    public static void GameEnd(Room r){
        int maxScore = 0;
        String winner = "";
        for(Player p : r.getAllPlayers()){
            int score = p.getScore();
            if(maxScore < score){
                maxScore = score;
                winner = p.getUUID();
            }
        }

        final int hiscore = maxScore;
        final String winnerUUID = winner;
        

        HashMap<String, Object> map = new HashMap<>(){{
            put("type", "gameEnd");
            put("winnerUUID", winnerUUID);
            put("hiScore", hiscore);
        }};

        try{
            sendDataToRoomMember(Json.toJson(map), r);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void sendBlockData(Room r, int stageLevel){
        Stage s = r.getStage(stageLevel);

        try{
            sendDataToRoomMember(s.toJson(), r);
        }catch(IOException e){
            e.printStackTrace();
        }
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

    static public void sendDataToRoomMember(String json, Room r)throws IOException{
        for(Player p: r.getAllPlayers()){
            String uuid = p.getUUID();
            Long threadId = uuidToThreadIdMap.get(uuid);
            if(threadId == null){
                continue;
            }
            Server1ClientThread thread = serverThreadMap.get(threadId);
            if (thread == null){
                continue;
            }

            if(!thread.isDisconnected){
                thread.sendDataToClient(json);
            }
        }
        System.out.println("<" + r.getName() + ">: " + json);
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