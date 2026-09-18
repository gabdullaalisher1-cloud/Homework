import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        int[] myTask2Array = {2, 6, 8, 1, 10};
        int[] myTask3Array = new int[5];
        int[] myTask4Array = {1, 2, 3, 4, 5};
        int[] myTask5Array = {10, 5, 2, 3};

        System.out.println("--- Задание 1 ---");
        Task1.printTextMultipleTimes(3, "Привет, Java!");

        System.out.println("\n--- Задание 2 ---");
        Task2.sumElementsGreaterThanFive(myTask2Array);

        System.out.println("\n--- Задание 3 ---");
        Task3.fillArrayWithNumber(7, myTask3Array);
        System.out.println(Arrays.toString(myTask3Array));

        System.out.println("\n--- Задание 4 ---");
        Task4.increaseElements(10, myTask4Array);
        System.out.println(Arrays.toString(myTask4Array));

        System.out.println("\n--- Задание 5 ---");
        Task5.compareArrayHalves(myTask5Array);
    }
}