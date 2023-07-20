package samepuzzle;

import java.util.HashMap;
import java.util.UUID;

public class Player {
    private String displayName;
    private String uuid;
    private String roomName;
    private int rank = 0;
    private int score = 0;
    private int targetScore;
    private int stageLevel = 0;

    private boolean isOwner = false;
    private boolean isPlaying = false;
    private boolean isReady = false;
    
    private Long gameStartAt;
    private boolean isGameEnded = true;
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

    public Boolean isReady(){
        return isReady;
    }
    public void setIsReady(boolean f){
        isReady = f;
    }
    public boolean isPlaying(){
        return isPlaying;
    }
    public void setIsPlaying(Boolean f){
        isPlaying = f;
    }
    public void setIsGameEnded(boolean f){
        isGameEnded = f;
    }
    public boolean isGameEnded(){
        return isGameEnded;
    }

    public String getDisplayName(){
        return displayName;
    }

    public void setGameStartTime(Long s){
        gameStartAt = s;
    }
    public Long getGameStartTime(){
        return gameStartAt;
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
    public void setRank(int n){
        rank = n;
    }

    public void resetScore(){
        score = 0;
    }
    public void setScore(int score){
        this.score = score;
    }
    public void addScore(int score){
        this.score += score;
    }
    public int getScore(){
        return score;
    }
    public void setTargetScore(int score){
        targetScore = score;
    }
    public int getTargetScore(){
        return targetScore;
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
