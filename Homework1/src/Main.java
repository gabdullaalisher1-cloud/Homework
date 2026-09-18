import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- Базовая часть (последовательный вызов) ---");
        Task1.greetings();
        Task2.checkSign(5, -10, 2);
        Task3.selectColor();
        Task4.compareNumbers();
        Task5.addOrSubtractAndPrint(10, 5, true);

        System.out.println("\n--- Задание (*) ---");
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        System.out.print("Введите номер задания от 1 до 5: ");

        if (scanner.hasNextInt()) {
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    Task1.greetings();
                    break;
                case 2:
                    Task2.checkSign(random.nextInt(100) - 50, random.nextInt(100) - 50, random.nextInt(100) - 50);
                    break;
                case 3:
                    Task3.selectColor();
                    break;
                case 4:
                    Task4.compareNumbers();
                    break;
                case 5:
                    Task5.addOrSubtractAndPrint(random.nextInt(100), random.nextInt(50), random.nextBoolean());
                    break;
                default:
                    System.out.println("Неверный номер. Ожидалось число от 1 до 5.");
            }
        } else {
            System.out.println("Ошибка: введено не число.");
        }

        scanner.close();
    }
}