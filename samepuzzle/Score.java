package samepuzzle;


public class Score{

    //n→連鎖するブロックの数
    //return →　スコア

    private int defaultScore = 20;
    private int increase = 25;
    private int[] scoreArr;

    //初期化
    public Score(){
        
        scoreArr = new int[100];
        scoreArr[2] = defaultScore;
        for(int i=3; i<scoreArr.length; i++){
            scoreArr[i] = scoreArr[i-1] + increase; 
            increase += 10;


        }
    }

    public int calc(int n){
        return scoreArr[n];
    }

    public static void main(String[] args){
        Score s = new Score();
    }
}