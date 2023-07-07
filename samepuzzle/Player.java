package samepuzzle;

import java.util.UUID;

public class Player {
    private String name;
    private String id;
    private int rank;
    private int score = 0;


    public Player(String name){
        this.name = name;
        id = UUID.randomUUID().toString();
    }
}
