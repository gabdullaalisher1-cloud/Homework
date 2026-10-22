package Kaznu.Alisher.Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.DoubleUnaryOperator;

public class Sro8NumericalPhysics {

    public record IterationStep(int k, double xk, double delta, double fxk) {}

    public static class NonlinearSolver {

        public static List<IterationStep> bisection(DoubleUnaryOperator f, double a, double b, double eps, int maxIter) {
            if (f.applyAsDouble(a) * f.applyAsDouble(b) >= 0) {
                throw new IllegalArgumentException("Root is not bracketed on [a, b]");
            }
            List<IterationStep> history = new ArrayList<>();
            double prev = a;
            for (int k = 1; k <= maxIter; k++) {
                double mid = 0.5 * (a + b);
                double fMid = f.applyAsDouble(mid);
                double delta = (k == 1) ? Math.abs(b - a) * 0.5 : Math.abs(mid - prev);
                history.add(new IterationStep(k, mid, delta, fMid));
                if (delta < eps || Math.abs(fMid) < eps) {
                    break;
                }
                if (f.applyAsDouble(a) * fMid < 0) {
                    b = mid;
                } else {
                    a = mid;
                }
                prev = mid;
            }
            return history;
        }

        public static List<IterationStep> fixedPoint(DoubleUnaryOperator phi, DoubleUnaryOperator f, double x0, double eps, int maxIter) {
            List<IterationStep> history = new ArrayList<>();
            double prev = x0;
            for (int k = 1; k <= maxIter; k++) {
                double next = phi.applyAsDouble(prev);
                double delta = Math.abs(next - prev);
                double fVal = f.applyAsDouble(next);
                history.add(new IterationStep(k, next, delta, fVal));
                if (delta < eps && Math.abs(fVal) < eps) {
                    break;
                }
                prev = next;
            }
            return history;
        }

        public static List<IterationStep> secant(DoubleUnaryOperator f, double x0, double x1, double eps, int maxIter) {
            List<IterationStep> history = new ArrayList<>();
            double prev0 = x0;
            double prev1 = x1;
            for (int k = 1; k <= maxIter; k++) {
                double f0 = f.applyAsDouble(prev0);
                double f1 = f.applyAsDouble(prev1);
                if (Math.abs(f1 - f0) < 1e-16) {
                    break;
                }
                double next = prev1 - f1 * (prev1 - prev0) / (f1 - f0);
                double delta = Math.abs(next - prev1);
                double fNext = f.applyAsDouble(next);
                history.add(new IterationStep(k, next, delta, fNext));
                if (delta < eps && Math.abs(fNext) < eps) {
                    break;
                }
                prev0 = prev1;
                prev1 = next;
            }
            return history;
        }

        public static List<IterationStep> newton(DoubleUnaryOperator f, DoubleUnaryOperator df, double x0, double eps, int maxIter) {
            List<IterationStep> history = new ArrayList<>();
            double prev = x0;
            for (int k = 1; k <= maxIter; k++) {
                double fVal = f.applyAsDouble(prev);
                double dfVal = df.applyAsDouble(prev);
                if (Math.abs(dfVal) < 1e-16) {
                    throw new ArithmeticException("Derivative is zero");
                }
                double next = prev - fVal / dfVal;
                double delta = Math.abs(next - prev);
                double fNext = f.applyAsDouble(next);
                history.add(new IterationStep(k, next, delta, fNext));
                if (delta < eps && Math.abs(fNext) < eps) {
                    break;
                }
                prev = next;
            }
            return history;
        }
    }

    public static class LinearSystemSolver {

        public static boolean isStrictlyDiagonallyDominant(double[][] A) {
            int n = A.length;
            for (int i = 0; i < n; i++) {
                double diag = Math.abs(A[i][i]);
                double sumOffDiag = 0.0;
                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        sumOffDiag += Math.abs(A[i][j]);
                    }
                }
                if (diag <= sumOffDiag) {
                    return false;
                }
            }
            return true;
        }

        public static double[] gaussPartialPivoting(double[][] A, double[] b) {
            int n = b.length;
            double[][] M = new double[n][n];
            double[] rhs = Arrays.copyOf(b, n);
            for (int i = 0; i < n; i++) {
                M[i] = Arrays.copyOf(A[i], n);
            }

            for (int k = 0; k < n; k++) {
                int maxRow = k;
                for (int i = k + 1; i < n; i++) {
                    if (Math.abs(M[i][k]) > Math.abs(M[maxRow][k])) {
                        maxRow = i;
                    }
                }
                double[] tempRow = M[k];
                M[k] = M[maxRow];
                M[maxRow] = tempRow;

                double tempVal = rhs[k];
                rhs[k] = rhs[maxRow];
                rhs[maxRow] = tempVal;

                for (int i = k + 1; i < n; i++) {
                    double factor = M[i][k] / M[k][k];
                    rhs[i] -= factor * rhs[k];
                    for (int j = k; j < n; j++) {
                        M[i][j] -= factor * M[k][j];
                    }
                }
            }

            double[] x = new double[n];
            for (int i = n - 1; i >= 0; i--) {
                double sum = 0.0;
                for (int j = i + 1; j < n; j++) {
                    sum += M[i][j] * x[j];
                }
                x[i] = (rhs[i] - sum) / M[i][i];
            }
            return x;
        }

        public static double[] jacobi(double[][] A, double[] b, double eps, int maxIter) {
            int n = b.length;
            double[] x = new double[n];
            double[] xNew = new double[n];
            for (int iter = 1; iter <= maxIter; iter++) {
                double maxDiff = 0.0;
                for (int i = 0; i < n; i++) {
                    double sum = 0.0;
                    for (int j = 0; j < n; j++) {
                        if (j != i) {
                            sum += A[i][j] * x[j];
                        }
                    }
                    xNew[i] = (b[i] - sum) / A[i][i];
                    maxDiff = Math.max(maxDiff, Math.abs(xNew[i] - x[i]));
                }
                System.arraycopy(xNew, 0, x, 0, n);
                if (maxDiff < eps) {
                    break;
                }
            }
            return x;
        }

        public static double[] gaussSeidel(double[][] A, double[] b, double eps, int maxIter) {
            int n = b.length;
            double[] x = new double[n];
            for (int iter = 1; iter <= maxIter; iter++) {
                double maxDiff = 0.0;
                for (int i = 0; i < n; i++) {
                    double sum = 0.0;
                    for (int j = 0; j < n; j++) {
                        if (j != i) {
                            sum += A[i][j] * x[j];
                        }
                    }
                    double updated = (b[i] - sum) / A[i][i];
                    maxDiff = Math.max(maxDiff, Math.abs(updated - x[i]));
                    x[i] = updated;
                }
                if (maxDiff < eps) {
                    break;
                }
            }
            return x;
        }

        public static double[] thomasAlgorithm(double[] subDiag, double[] mainDiag, double[] supDiag, double[] d) {
            int n = d.length;
            double[] alpha = new double[n];
            double[] beta = new double[n];

            alpha[0] = -supDiag[0] / mainDiag[0];
            beta[0] = d[0] / mainDiag[0];

            for (int i = 1; i < n; i++) {
                double denom = mainDiag[i] + subDiag[i] * alpha[i - 1];
                if (i < n - 1) {
                    alpha[i] = -supDiag[i] / denom;
                }
                beta[i] = (d[i] - subDiag[i] * beta[i - 1]) / denom;
            }

            double[] x = new double[n];
            x[n - 1] = beta[n - 1];
            for (int i = n - 2; i >= 0; i--) {
                x[i] = alpha[i] * x[i + 1] + beta[i];
            }
            return x;
        }

        public static double residualMaxNorm(double[][] A, double[] x, double[] b) {
            int n = b.length;
            double maxRes = 0.0;
            for (int i = 0; i < n; i++) {
                double ax = 0.0;
                for (int j = 0; j < n; j++) {
                    ax += A[i][j] * x[j];
                }
                maxRes = Math.max(maxRes, Math.abs(ax - b[i]));
            }
            return maxRes;
        }
    }

    private static void printTable(String methodName, List<IterationStep> steps, double exactRoot) {
        System.out.println("=== " + methodName + " ===");
        System.out.printf("%-4s | %-18s | %-14s | %-14s | %-14s%n",
                "k", "E_k (rad)", "|dE_k|", "|f(E_k)|", "lg|E_k - E*|");
        System.out.println("-".repeat(76));
        for (IterationStep s : steps) {
            double err = Math.max(Math.abs(s.xk() - exactRoot), 1e-16);
            System.out.printf(Locale.US, "%-4d | %-18.14f | %-14.4e | %-14.4e | %-14.4f%n",
                    s.k(), s.xk(), s.delta(), Math.abs(s.fxk()), Math.log10(err));
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        double e = 0.0934;
        double M = 1.0471975511965976;
        double eps = 1e-12;

        DoubleUnaryOperator fKepler = E -> E - e * Math.sin(E) - M;
        DoubleUnaryOperator dfKepler = E -> 1.0 - e * Math.cos(E);
        DoubleUnaryOperator phiKepler = E -> M + e * Math.sin(E);

        List<IterationStep> newtonSteps = NonlinearSolver.newton(fKepler, dfKepler, M, eps, 100);
        double exactRoot = newtonSteps.get(newtonSteps.size() - 1).xk();

        List<IterationStep> bisectionSteps = NonlinearSolver.bisection(fKepler, 1.0, 1.2, eps, 100);
        List<IterationStep> fixedPointSteps = NonlinearSolver.fixedPoint(phiKepler, fKepler, M, eps, 100);
        List<IterationStep> secantSteps = NonlinearSolver.secant(fKepler, 1.0, 1.2, eps, 100);

        printTable("Bisection Method", bisectionSteps, exactRoot);
        printTable("Fixed-Point Iteration", fixedPointSteps, exactRoot);
        printTable("Secant Method", secantSteps, exactRoot);
        printTable("Newton's Method", newtonSteps, exactRoot);

        double[][] A = {
                { 35.0, -10.0, -15.0 },
                {-10.0,  55.0, -20.0 },
                {-15.0, -20.0,  65.0 }
        };
        double[] b = { 12.0, 0.0, 0.0 };

        boolean diagDom = LinearSystemSolver.isStrictlyDiagonallyDominant(A);
        double[] J = LinearSystemSolver.gaussPartialPivoting(A, b);
        double[] JJacobi = LinearSystemSolver.jacobi(A, b, 1e-12, 500);
        double[] JSeidel = LinearSystemSolver.gaussSeidel(A, b, 1e-12, 500);

        double I1 = J[0];
        double I2 = J[0] - J[1];
        double I3 = J[0] - J[2];
        double I4 = J[1];
        double I5 = J[2] - J[1];
        double I6 = J[2];

        System.out.println("=== Kirchhoff Circuit Analysis (SLAE) ===");
        System.out.println("Strict Diagonal Dominance: " + diagDom);
        System.out.printf("Mesh Currents (Gauss):  J1 = %.8f A, J2 = %.8f A, J3 = %.8f A%n", J[0], J[1], J[2]);
        System.out.printf("Mesh Currents (Jacobi): J1 = %.8f A, J2 = %.8f A, J3 = %.8f A%n", JJacobi[0], JJacobi[1], JJacobi[2]);
        System.out.printf("Mesh Currents (Seidel): J1 = %.8f A, J2 = %.8f A, J3 = %.8f A%n", JSeidel[0], JSeidel[1], JSeidel[2]);
        System.out.printf("Branch Currents: I1=%.6f A, I2=%.6f A, I3=%.6f A, I4=%.6f A, I5=%.6f A, I6=%.6f A%n",
                I1, I2, I3, I4, I5, I6);
        System.out.printf("Max Residual ||Ax - b||_inf = %.4e V%n%n", LinearSystemSolver.residualMaxNorm(A, J, b));

        int nNodes = 6;
        double[] sub = { 0.0, -1.0, -1.0, -1.0, -1.0, -1.0 };
        double[] main = { 3.0,  3.0,  3.0,  3.0,  3.0,  2.0 };
        double[] sup = {-1.0, -1.0, -1.0, -1.0, -1.0,  0.0 };
        double[] rhs = {10.0,  0.0,  0.0,  0.0,  0.0,  0.0 };

        double[] phi = LinearSystemSolver.thomasAlgorithm(sub, main, sup, rhs);
        System.out.println("=== Ladder Resistor Network Potentials (Thomas Algorithm) ===");
        for (int i = 0; i < nNodes; i++) {
            System.out.printf("Node %d: phi_%d = %.8f V%n", i + 1, i + 1, phi[i]);
        }
    }
}