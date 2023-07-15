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

        Json.toJson(new HashMap<String, Object>(){{
            put("name", name);
            put("roomName", roomName);
        }   
        });

        clientToServerStream.writeUTF("{\"type\": \"connect\"" + name + "\t" +  roomName);


        while (true) {
            
            // コンソールから入力を受け付ける
            final String message = consoleInputScanner.nextLine();

            
            // サーバーにメッセージを送る
            clientToServerStream.writeUTF(message);
            clientToServerStream.flush();
        }
    }
}