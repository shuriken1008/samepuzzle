package samepuzzle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class Room {
    private String name;
    private String passwd;
    private String uuid;
    private int targetScore;
    private int stageSize;
    private ArrayList<Stage> stageList = new ArrayList<>();

    private Player[] ranking = new Player[6];

    //<uuid, Player>
    private HashMap<String, Player> players = new HashMap<>();

    private boolean hasGameStarted = false;
    private boolean hasGameEnded = false;



    //<UUID, Playerクラス>
    private ArrayList<Player> playerList = new ArrayList<>();

    public Room(String name, String passwd){
        this.name = name;
        this.passwd = passwd;
        uuid = UUID.randomUUID().toString();
    }
    

    //server only
    public Stage genarateStages(){
        Stage s = new Stage(stageSize);
        stageList.add(s);

        return s;
    }
    public Stage getStage(int index){
        Stage s = stageList.get(index);
        if(s == null){
            s = genarateStages();
        }

        return s;
    }
    //

    //client only
    public void loadStage(int[][] stageData, int stageLevel){
        Stage s = new Stage(stageSize, stageLevel, stageData);
        stageList.add(stageLevel, s);
    }
    //


    public Player getPlayer(String uuid){
        return players.get(uuid);
    }
    public Collection<Player> getAllPlayers(){
        return players.values();
    }

    public void addPlayer(Player p){
        playerList.add(p);
        players.put(p.getUUID(), p);
    }

    public void removePlayer(Player p){
        playerList.remove(p);
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

    public void setTargetScore(int score){
        targetScore = score;
    }
    public int getTargetScore(){
        return targetScore;
    }

    public void makeRanking(){
        int[]
        for(Player p: getAllPlayers()){
            
        }
    }


}
