package samepuzzle;

import java.util.Map;
import java.util.HashMap;

public class Rooms {
    //<部屋名, Roomクラス>
    private Map<String, Room> roomMap = new HashMap<>(); 

    public Rooms(){
        
    }


    //passwd無し
    public boolean createRoom(String name){
        Room r = new Room(name, "");
        if(roomMap.get(name) == null){
            roomMap.put(name, r);
            return true;     
        }else{
            return false;
        }
    }

    //passwdあり
    public boolean createRoom(String name, String passwd){
        Room r = new Room(name, passwd);
        if(roomMap.get(name) == null){
            roomMap.put(name, r);
            return true;     
        }else{
            return false;
        }
    }

    public void deleteRoom(String name){
        roomMap.remove(name);
    }

    
    public boolean joinRoom(Player p, String name, String passwd){
        Room r = roomMap.get(name);

        if(r == null){
            return false;
        }else{

            if(r.getPasswd() == passwd){
                r.addPlayer(p);

                return true;
            }else{
                return false;
            }
        }

    }
}
