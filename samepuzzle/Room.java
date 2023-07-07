package samepuzzle;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Room {
    private String name;
    private String passwd;
    private String uuid;

    private Player[] ranking = new Player[6];

    private boolean is_start = false;


    private Map<Integer, Player> playerMap = new HashMap();

    public Room(String name, String passwd){
        this.name = name;
        this.passwd = passwd;
        uuid = UUID.randomUUID().toString();
    }

    public void addPlayer(Player p){
        
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
