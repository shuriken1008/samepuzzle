package samepuzzle;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

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
    private String uuid;
    private String displayName;
    private String roomName;
    private Player playerData; 
    final InetAddress localhost;
    final Socket socket;
    final ObjectInputStream serverToClientStream;
    final ObjectOutputStream clientToServerStream;
    final Scanner consoleInputScanner;
    
    public Client(String displayName) throws IOException {
        playerData = new Player(displayName);

        localhost = InetAddress.getLocalHost();
        socket = new Socket(localhost, Server.portNumber);
        serverToClientStream = new ObjectInputStream(socket.getInputStream());
        clientToServerStream = new ObjectOutputStream(socket.getOutputStream());
        new Thread(
                () -> {
                    listener();
                })
                .start();
        consoleInputScanner = new Scanner(System.in);
    }

    public void listener(){
        try {
            while (true) {
                String payload = serverToClientStream.readUTF();
                HashMap<String, Object> map = Json.toHashMap(payload);
                if(map == null){
                    continue;
                }

                if(map.get("type") == "playerData"){
                    this.uuid = (String)map.get("uuid");
                    
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
                }

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onPlayerData(HashMap<String, Object> map){

    }

    public void onChat(HashMap<String, Object> map){

    }

    public void onBlockData(HashMap<String, Object> map){

    }

    public void onGameStart(HashMap<String, Object> map){

    }

    public void onGameEnd(HashMap<String, Object> map){

    }

    public void connect(String roomName) throws IOException{
        this.roomName = roomName;
        String jStr = Json.toJson(new HashMap<String, Object>(){{
            put("type", "connect");
            put("displayName", displayName);
            put("uuid", playerData.getUUID());
            put("roomName", roomName);

        }   
        });

        clientToServerStream.writeUTF(jStr);
        clientToServerStream.flush();
        
        final String message = consoleInputScanner.nextLine();
        HashMap<String, Object> map = Json.toHashMap(message);
        if(map == null){
            return;
        }

        if(map.get("type") == "playerData"){
            this.uuid = (String)map.get("uuid");
            
        }    
        
        


    }


    public void disconnect() throws IOException{
        String jStr = Json.toJson(new HashMap<String, Object>(){{
            put("type", "disconnect");
            put("uuid", uuid);
            

        }   
        });

        clientToServerStream.writeUTF(jStr);
        clientToServerStream.flush();
    }


    public void changeStatus() throws IOException{
        String jStr = Json.toJson(new HashMap<String, Object>(){{
            put("type", "status");
            put("uuid", uuid);
            

        }   
        });

        clientToServerStream.writeUTF(jStr);
        clientToServerStream.flush();
    }

    public void waitStartGame(){


    }

    public void startGameHost(){

    }

    


    public static void main(String[] args) throws IOException {
        final InetAddress localhost = InetAddress.getLocalHost();
        System.out.println(
                "クライアント2を起動しました. これから " + localhost + " のポート番号 " + Server.portNumber + "に接続します");
        final Socket socket = new Socket(localhost, Server.portNumber);

        final ObjectInputStream serverToClientStream = new ObjectInputStream(socket.getInputStream());
        final ObjectOutputStream clientToServerStream = new ObjectOutputStream(socket.getOutputStream());

        new Thread(
                () -> {
                    try {
                        while (true) {
                            System.out.println(serverToClientStream.readUTF());
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .start();

        final Scanner consoleInputScanner = new Scanner(System.in);



        System.out.print("名前 > ");
        String name = consoleInputScanner.nextLine();

        System.out.print("部屋名 > ");
        String roomName = consoleInputScanner.nextLine();

        //jsonに変換
        //サーバーと接続
        String jStr = Json.toJson(new HashMap<String, Object>(){{
            put("type", "connect");
            put("player", name);
            put("roomName", roomName);

        }   
        });

        clientToServerStream.writeUTF(jStr);




        while (true) {
            
            // コンソールから入力を受け付ける
            final String message = consoleInputScanner.nextLine();

            
            // サーバーにメッセージを送る
            clientToServerStream.writeUTF(message);
            clientToServerStream.flush();
        }
    }
}