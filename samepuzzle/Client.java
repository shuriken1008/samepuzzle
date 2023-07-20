package samepuzzle;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

/*
サーバーの起動
cd ./src
javac --enable-preview --source 20 Server.java
java --enable-preview Server
クライアントの起動
cd ./src
javac --enable-preview --source 20 TCPClient.java
java --enable-preview TCPClient
 */
public class Client {
    final int portNumber = 12344;
    
    //String hostName = "localhost";
    String hostName = "gesi.f5.si";
    final InetSocketAddress addr = new InetSocketAddress(hostName, portNumber);
    final Socket socket;
    final ObjectInputStream serverToClientStream;
    static ObjectOutputStream clientToServerStream;

    static Room myRoom ;
    static Player myData; 


    
    public Client(String displayName , String roomName) throws IOException {
        myData = new Player(displayName);
        myData.setRoomName(roomName);

        socket = new Socket(addr.getAddress(), addr.getPort());
        serverToClientStream = new ObjectInputStream(socket.getInputStream());
        clientToServerStream = new ObjectOutputStream(socket.getOutputStream());
        new Thread(
                () -> {
                    listener();
                })
                .start();
        
    }

    public void listener(){
        try {
            while (true) {
                String payload = serverToClientStream.readUTF();
                HashMap<String, Object> map = Json.toHashMap(payload);
                if(map == null){
                    continue;
                }
                System.out.println(payload);
                
                switch((String)map.get("type")){
                    case("playerData")->{
                        onPlayerData(map);
                    }
                    case("chat")->{
                        onChat(map);
                    }
                    case("blockData")->{
                        onBlockData(map);
                    }
                    case("gameStart")->{
                        onGameStart(map);
                    }
                    case("gameEnd")->{
                        onGameEnd(map);
                    }
                    case("disconnect")->{
                        onDisconnectPlayer(map);
                    }
                }

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onPlayerData(HashMap<String, Object> map){
        //読み込み
        Player newP = new Player();
        newP.setFromMap(map);

        Player p = myRoom.getPlayer(newP.getUUID());

        if(p == null){
            //部屋にいなかったらプレイヤーデータ作成
            myRoom.addPlayer(newP);
        }else{
            p.setFromMap(map);
        }

        //自分だったらmyData更新
        //ただしゲーム中は更新しない バグ防止
        if(newP.getUUID() == myData.getUUID() && !myData.isPlaying()){
            myData = newP;
        }
        
    }

    public void onChat(HashMap<String, Object> map){
        String msg = (String)map.get("content");
        String uuid = (String)map.get("uuid");

        Player p = myRoom.getPlayer(uuid);

        String str = "<" + p.getDisplayName() + " さん> " + msg; 
        
        System.out.println(str);
    }

    public void onBlockData(HashMap<String, Object> map){
        //データ読み込み
        int stageLevel = (int)map.get("stageLevel");
        int stageSize = (int)map.get("stageSize");
        String _stageData = (String)map.get("data");
        int[][] stageData = new int[stageSize][stageSize];

        for(int x=0; x<stageSize; x++){
            for(int y=0; y<stageSize; y++){
                stageData[x][y] = _stageData.charAt(x*stageSize + y);
            }
        }
    }

    public void onGameStart(HashMap<String, Object> map){
        int targetScore = Integer.valueOf((String)map.get("targetScore"));
        long epochSec = Long.valueOf((String)map.get("startAt"));
        
        myData.setGameStartTime(epochSec);
        myData.setIsPlaying(true);
        myData.setIsGameEnded(false);
        myData.setTargetScore(targetScore);

        //ターゲットスコア表示セット

    }

    public void onGameEnd(HashMap<String, Object> map){
        String winnerUUID = (String)map.get("winnerUUID");
        int hiscore = Integer.parseInt((String)map.get("hiScore"));

        myData.setIsGameEnded(true);
        myRoom.setHiscore(hiscore);
        myRoom.setWinner(winnerUUID);
        //ゲーム中止 -> guiで処理

        //リザルト表示　->同じ

        //

        //リザルト表示してからisPlaying = false;
    }

    public void onDisconnectPlayer(HashMap<String, Object> map){
        String uuid = (String)map.get("uuid");
        Player p = myRoom.getPlayer(uuid);

        myRoom.removePlayer(p);
        String str = "<" + myRoom.getName() + "> " +p.getDisplayName() + " さんが退出しました。";
        System.out.println(str);
    }

    public void connect() throws IOException{

        myRoom = new Room(myData.getRoomName(), "");

        String jStr = Json.toJson(new HashMap<String, Object>(){{
            put("type", "connect");
            put("displayName", myData.getDisplayName());
            put("uuid", myData.getUUID());
            put("roomName", myData.getRoomName());

        }   
        });

        clientToServerStream.writeUTF(jStr);
        clientToServerStream.flush();
        
        


    }


    public void disconnect() throws IOException{
        String jStr = Json.toJson(new HashMap<String, Object>(){{
            put("type", "disconnect");
            put("uuid", myData.getUUID());
            

        }   
        });

        clientToServerStream.writeUTF(jStr);
        clientToServerStream.flush();
    }


    public void sendChat(String msg) throws IOException{
        String jStr = Json.toJson(new HashMap<String, Object>(){{
            put("type", "chat");
            put("uuid", myData.getUUID());
            put("content", msg);
            

        }   
        });

        clientToServerStream.writeUTF(jStr);
        clientToServerStream.flush();
    }

    public static void sendMyData() throws IOException{
        clientToServerStream.writeUTF(myData.toJson());
        clientToServerStream.flush();

    }  

    public void sendBreakPos(int x, int y, int score) throws IOException{
        String jStr = Json.toJson(new HashMap<String, Object>(){{
            put("type", "breakData");
            put("roomName", myData.getRoomName());
            put("uuid", myData.getUUID());
            put("x", x);
            put("y", y);
            put("score", score);

        }   
        });

        clientToServerStream.writeUTF(jStr);
        clientToServerStream.flush();

    }
    


    public static void main(String[] args) throws IOException {

        Scanner consoleInputScanner = new Scanner(System.in);
        System.out.print("名前 > ");
        String name = consoleInputScanner.nextLine();

        System.out.print("部屋名 > ");
        String roomeName = consoleInputScanner.nextLine();

        Client client = new Client(name, roomeName);
        myData.setRoomName(roomeName);
        client.connect();
        

        //準備完了を送信
        myData.setIsReady(true);
        sendMyData();


        
        
    }

    public boolean checkGameFlag() {
        if(myData.isPlaying() && !myData.isGameEnded()){
            return true;
        }
        return false;
    }
}