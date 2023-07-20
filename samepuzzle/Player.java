package samepuzzle;

import java.util.HashMap;
import java.util.UUID;

public class Player {
    private String displayName;
    private String uuid;
    private String roomName;
    private int rank = 0;
    private int score = 0;
    private int stageLevel = 0;

    private boolean isOwner = false;
    private boolean isPlaying = false;
    private boolean isReady = false;
    
    private Stage myStage;


    public Player(String displayName){
        this.displayName = displayName;
        uuid = UUID.randomUUID().toString();
    }

    public Player(){
    }


    public String getUUID(){   
        return uuid;
    }
    public void setUUID(String uuid){
        this.uuid = uuid;
    }

    public Boolean getIsReady(){
        return isReady;
    }
    public void setReady(Boolean f){
        isReady = f;
    }

    public String getDisplayName(){
        return displayName;
    }

    public void setRoomName(String roomName){
        this.roomName = roomName;
    }
    public String getRoomName(){
        return roomName;
    }

    public int getRank(){
        return rank;
    }

    public int getScore(){
        return score;
    }

    public void setFromMap(HashMap<String, Object> map){

        if(map == null){
            return;
        }

        uuid = (String)map.get("uuid");
        displayName = (String)map.get("displayName");
        roomName = (String)map.get("roomName");
        isOwner = Integer.parseInt((String)map.get("isOwner")) == 1 ? true:false;
        isReady = Integer.parseInt((String)map.get("isReady")) == 1 ? true:false;
        isPlaying = Integer.parseInt((String)map.get("isPlaying")) == 1 ? true:false;
        score = Integer.parseInt((String)map.get("score"));
        rank = Integer.parseInt((String)map.get("rank"));
    }

    public String toJson(){
        HashMap<String, Object> map = new HashMap<>(){{
            put("type", "playerData");
            put("uuid", uuid);
            put("displayName", displayName);
            put("roomName", roomName);
            put("isOwner", isOwner ? 1:0);
            put("isPlaying", isPlaying ? 1:0);
            put("isReady", isReady ? 1:0);
            put("score", score);
            put("rank", rank);
        }};

        return Json.toJson(map);
    }
}
