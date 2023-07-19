package samepuzzle;

import java.util.Map;
import java.util.HashMap;

public class Rooms {
    //<部屋名, Roomクラス>
    private Map<String, Room> roomMap = new HashMap<>(); 

    public Rooms(){
        
    }


    //passwd無し
    public Room getRoom(String name){
        
        Room r = roomMap.get(name);
        if(r == null){
            r = new Room(name, "");
            
        }else{
            roomMap.put(name, r);
        }
        return r;
    }

    public Room getRoomFromPlayer(String uuid){
        for(Room r: roomMap.values()){
            if(r.getPlayer(uuid) != null){
                return r;
            }
        }
        return null;
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
