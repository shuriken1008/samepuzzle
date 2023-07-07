package samepuzzle;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        int[] array = {0, 2, 0, 4, 0, 6, 0, 8, 0};
        System.out.println("Before: " + Arrays.toString(array));
        
        int[] result = moveZerosToEnd(array);
        System.out.println("After: " + Arrays.toString(result));
    }
    
    public static int[] moveZerosToEnd(int[] array) {
        int[] result = new int[array.length];
        int index = 0;
        
        // 0以外の要素を新しい配列に追加
        for (int i = 0; i < array.length; i++) {
            if (array[i] != 0) {
                result[index] = array[i];
                index++;
            }
        }
        
        // 0の要素を新しい配列の末尾に追加
        while (index < array.length) {
            result[index] = 0;
            index++;
        }
        
        return result;
    }
}
