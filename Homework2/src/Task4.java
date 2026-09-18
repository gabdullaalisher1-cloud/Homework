public class Task4 {
    public static void increaseElements(int number, int[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = array[i] + number;
        }
    }
}