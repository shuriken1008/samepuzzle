package samepuzzle;

import java.util.HashMap;
import java.util.UUID;

public class Player {
    private String displayName;
    private String uuid;
    private int rank = 0;
    private int score = 0;
    private int stageLevel = 0;

    private boolean isOwner = false;
    private boolean isPlaying = false;
    private boolean isReady = false;
    


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
        isOwner = (Boolean)map.get("isOwner");
        isPlaying = (Boolean)map.get("isPlaying");
        isReady = (Boolean)map.get("isReady");
        score = (int)map.get("score");
        rank = (int)map.get("rank");
    }

    public String toJson(){
        HashMap<String, Object> map = new HashMap<>(){{
            put("uuid", uuid);
            put("displayName", displayName);
            put("isOwner", isOwner);
            put("isPlaying", isPlaying);
            put("isReady", isReady);
            put("score", score);
            put("rank", rank);
        }};

        return Json.toJson(map);
    }
}
