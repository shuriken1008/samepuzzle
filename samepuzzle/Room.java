package samepuzzle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Room {
    private String name;
    private String passwd;
    private String uuid;
    private int stageSize;
    private Stage stage;

    private Player[] ranking = new Player[6];

    private boolean is_start = false;


    //<UUID, Playerクラス>
    private ArrayList<Player> playerList = new ArrayList<>();

    public Room(String name, String passwd){
        this.name = name;
        this.passwd = passwd;
        uuid = UUID.randomUUID().toString();
    }
    
    public void startGame(){
        stage = new Stage(stageSize);
    }


    public void addPlayer(Player p){
        playerList.add(p);
    }

    public String getUUID(){
        return uuid;
    }

    public String getName(){
       return name; 
    }

    public String getPasswd(){
        return passwd;
    }


}
