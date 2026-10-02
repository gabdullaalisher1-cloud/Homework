public class Task2 {
    public static void main(String[] args) {
        int size = 5;
        printSquare(size);
    }

    public static void printSquare(int size) {
        if (size <= 0) {
            System.out.println("Размер должен быть больше 0");
            return;
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print("* ");
            }
            System.out.println();
        }
    }
}