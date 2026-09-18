public class Task2 {
    public static void sumElementsGreaterThanFive(int[] array) {
        int sum = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > 5) {
                sum = sum + array[i];
            }
        }
        System.out.println("Сумма элементов больше 5: " + sum);
    }
}