package samepuzzle;

//チャットサーバープログラム
//チャットクライアントプログラムからの接続を待つ。
//接続後は1行の文字列読み取りを行い、接続終了する。
//java CharServer 99
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
	

    public static void main(String[] args)
    {
		Rooms rooms = new Rooms();
	try {
		int port=8888;
	    ServerSocket srvSock = new ServerSocket(port);
	    Socket socket = srvSock.accept();

		String addr;


		rooms.createRoom(null, null);

	    //　通信処理

      	//ソケットの入力ストリームから文字列を1行読み取る。
	    BufferedReader reader = new BufferedReader
	    	(new InputStreamReader(socket.getInputStream()));
	    String[] words = reader.readLine().split(" ");


		
		switch(words[0]){
			case("/makeroom")->{

			}
		}

		

		//終了処理　このプログラムは1行読み取ったら終了する。
		//通信を続けるのであれば、reader.readLine();を
		//ループするが、終了コマンドをチェックする等の処理を
		//記述する。
	    reader.close();
	    socket.close();
	    srvSock.close();
	}
	catch (IOException e) {
	    e.printStackTrace();
	}
    } 


}
