package samepuzzle;

import java.util.UUID;

public class Player {
    private String displayName;
    private String uuid;
    private int rank;
    private int score = 0;
    private Stage mystage;

    private boolean isOwner = false;
    private boolean isPlaying = false;
    private boolean isWaiting = false;

    private String ipaddr;
    private int port;
    


    public Player(String name){
        this.displayName = name;
        uuid = UUID.randomUUID().toString();
    }

    public String getUUID(){   
        return uuid;
    }
    public void setUUID(String uuid){
        this.uuid = uuid;
    }

    public String getDisplayName(){
        return displayName;
    }
}
