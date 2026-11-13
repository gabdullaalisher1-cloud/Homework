package Kaznu.Alisher;

import java.util.function.DoubleUnaryOperator;

public class Main {
    public static double chord(DoubleUnaryOperator f, double a, double b, double eps, int nmax) {
        if (f.applyAsDouble(a) * f.applyAsDouble(b) > 0) {
            throw new IllegalArgumentException("Ошибка: на концах отрезка функция должна иметь разные знаки.");
        }
        double c = a;
        for (int n = 1; n <= nmax; n++) {
            double fa = f.applyAsDouble(a);
            double fb = f.applyAsDouble(b);
            double cn = a - (b - a) * fa / (fb - fa);
            if (n > 1 && Math.abs(cn - c) < eps) {
                System.out.println("Корень найден за " + n + " итераций.");
                return cn;
            }
            if (fa * f.applyAsDouble(cn) < 0) {
                b = cn;
            } else {
                a = cn;
            }
            c = cn;
        }
        throw new RuntimeException("Превышено максимальное число итераций.");
    }

    public static void main(String[] args) {
        DoubleUnaryOperator f = x -> Math.pow(x, 3) - x - 1;
        double a = 1.0;
        double b = 2.0;
        double eps = 1e-6;
        int nmax = 100;
        try {
            double root = chord(f, a, b, eps, nmax);
            System.out.printf("x* = %.6f\n", root);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}