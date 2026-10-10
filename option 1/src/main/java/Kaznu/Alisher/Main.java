package Kaznu.Alisher;

/**
 * СРС 6: Машинная арифметика и анализ погрешностей вычислительного эксперимента
 * Выполнил: Габдулла Алишер Русланұлы
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("--- ЧИСЛЕННЫЙ ЭКСПЕРИМЕНТ 1 ---");

        // 1. Поиск машинного эпсилон для типа float
        float epsF = 1.0f;
        while (1.0f + (epsF / 2.0f) > 1.0f) {
            epsF = epsF / 2.0f;
        }
        System.out.println("Машинное эпсилон (float): " + epsF);

        // 2. Поиск машинного эпсилон для типа double
        double epsD = 1.0;
        while (1.0 + (epsD / 2.0) > 1.0) {
            epsD = epsD / 2.0;
        }
        System.out.println("Машинное эпсилон (double): " + epsD);

        // 3. Демонстрация потери точности при суммировании малых чисел
        float sumF = 0.0f;
        float prevSumF = -1.0f;
        int countF = 0;

        while (sumF != prevSumF) {
            prevSumF = sumF;
            sumF = sumF + 0.1f;
            countF = countF + 1;
        }
        System.out.println("Сумма 0.1 перестает расти на шаге: " + countF);
        System.out.println("Итоговая накопленная сумма: " + sumF);


        System.out.println("\n--- ЧИСЛЕННЫЙ ЭКСПЕРИМЕНТ 2 ---");
        System.out.println("Вычисление корня уравнения x^2 + bx + 1 = 0");

        // Цикл от b = 10^3 до b = 10^9
        for (double b = 1000; b <= 1000000000; b = b * 10) {
            System.out.println("\nКоэффициент b = " + b);

            double d = Math.sqrt(b * b - 4.0);

            // Наивный алгоритм
            double naive = (-b + d) / 2.0;

            // Устойчивый алгоритм
            double stable = -2.0 / (b + d);

            // Относительная погрешность
            double error = Math.abs((naive - stable) / stable);

            System.out.println("  Результат (наивный):    " + naive);
            System.out.println("  Результат (устойчивый): " + stable);
            System.out.println("  Погрешность метода:     " + error);
        }
    }
}