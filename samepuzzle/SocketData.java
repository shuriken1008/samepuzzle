package samepuzzle;

import java.io.Serializable;

public class SocketData implements Serializable{
    public static final int CONNECTION = 0;
    public static final int BREAKBLOCK = 1;
    public static final int STAGEDATA = 2;
    public static final int FINISHGAME = 3;

    

    public int dataType;
    public String sender;
    public String payload;


    public SocketData(int dataType, String sender, String payload){
        this.dataType = dataType;
        this.sender = sender;
        this.payload = payload;
    }
}