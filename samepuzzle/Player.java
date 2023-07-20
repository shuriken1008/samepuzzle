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
        isOwner =  (String)map.get("isOwner") == "true"?true :false;
        isPlaying = (String)map.get("isPlaying") == "true" ? true: false;
        isReady = (String)map.get("isReady") == "true" ? true: false;
        score = Integer.parseInt((String)map.get("score"));
        rank = Integer.parseInt((String)map.get("rank"));

        //System.out.println(toJson());
    }

    public String toJson(){
        HashMap<String, Object> map = new HashMap<>(){{
            put("type", "playerData");
            put("uuid", uuid);
            put("displayName", displayName);
            put("roomName", roomName);
            put("isOwner", isOwner);
            put("isPlaying", isPlaying);
            put("isReady", isReady);
            put("score", score);
            put("rank", rank);
        }};

        return Json.toJson(map);
    }
}
