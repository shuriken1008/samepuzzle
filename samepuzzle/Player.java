package samepuzzle;

import java.util.UUID;

public class Player {
    private String name;
    private String uuid;
    private int rank;
    private int score = 0;
    private Stage mystage;

    private String ipaddr;
    private int port;


    public Player(String name){
        this.name = name;
        uuid = UUID.randomUUID().toString();
    }

    public String getUUID(){   
        return uuid;
    }
}
