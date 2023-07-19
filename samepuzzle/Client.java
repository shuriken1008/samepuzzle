package samepuzzle;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
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
    final InetAddress localhost;
    final Socket socket;
    final ObjectInputStream serverToClientStream;
    final ObjectOutputStream clientToServerStream;

    private Room myRoom ;
    private Player myData; 
    
    public Client(String displayName) throws IOException {
        myData = new Player(displayName);

        localhost = InetAddress.getLocalHost();
        socket = new Socket(localhost, Server.portNumber);
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
        
    }

    public void onChat(HashMap<String, Object> map){
        String msg = (String)map.get("content");
        String uuid = (String)map.get("uuid");
        
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
        int targetScore = (int)map.get("targetScore");
        long epochSec = (long)map.get("startAt");
        
        Timer timer = new Timer(false);
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				//ゲームスタート

				timer.cancel();
			}
		};

        timer.schedule(task, epochSec);

        //ターゲットスコア表示セット

    }

    public void onGameEnd(HashMap<String, Object> map){
        //ゲーム中止

        //リザルト表示

        //
    }

    public void onDisconnectPlayer(HashMap<String, Object> map){
        String uuid = (String)map.get("uuid");

        Player p = myRoom.getPlayer(uuid);
        myRoom.removePlayer(p);
    }

    public void connect(String roomName) throws IOException{

        myRoom = new Room(roomName, "");

        String jStr = Json.toJson(new HashMap<String, Object>(){{
            put("type", "connect");
            put("displayName", myData.getDisplayName());
            put("uuid", myData.getUUID());
            put("roomName", roomName);

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



    public void sendPlayerData() throws IOException{
        String jStr = Json.toJson(new HashMap<String, Object>(){{
            put("type", "playerData");
            put("uuid", myData.getUUID());
            

        }   
        });

        clientToServerStream.writeUTF(jStr);
        clientToServerStream.flush();

    }  

    public void sendBreakPos(int x, int y){
        
    }
    


    public static void main(String[] args) throws IOException {

        Scanner consoleInputScanner = new Scanner(System.in);
        System.out.print("名前 > ");
        String name = consoleInputScanner.nextLine();

        System.out.print("部屋名 > ");
        String roomeName = consoleInputScanner.nextLine();

        Client client = new Client(name);
        client.connect(roomeName);

        
        
    }
}