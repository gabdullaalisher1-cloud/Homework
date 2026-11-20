package Kaznu.Alisher;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

public class Main {

    public static class SplineCoefficients {
        public final double[] a, b, c, d, x;

        public SplineCoefficients(double[] x, double[] a, double[] b, double[] c, double[] d) {
            this.x = x;
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }
    }

    public static class OlsResult {
        public final double a0, sigmaA0;
        public final double lambda, sigmaLambda;
        public final double qFactor, sigmaQ;
        public final double rSquared;
        public final double slope, intercept;

        public OlsResult(double a0, double sigmaA0, double lambda, double sigmaLambda,
                         double qFactor, double sigmaQ, double rSquared,
                         double slope, double intercept) {
            this.a0 = a0;
            this.sigmaA0 = sigmaA0;
            this.lambda = lambda;
            this.sigmaLambda = sigmaLambda;
            this.qFactor = qFactor;
            this.sigmaQ = sigmaQ;
            this.rSquared = rSquared;
            this.slope = slope;
            this.intercept = intercept;
        }
    }

    public static double evaluateLagrange(double[] xNodes, double[] yNodes, double x) {
        int n = xNodes.length;
        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            double term = yNodes[i];
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    term *= (x - xNodes[j]) / (xNodes[i] - xNodes[j]);
                }
            }
            sum += term;
        }
        return sum;
    }

    public static double[] computeNewtonCoefficients(double[] xNodes, double[] yNodes) {
        int n = xNodes.length;
        double[] coef = yNodes.clone();
        for (int j = 1; j < n; j++) {
            for (int i = n - 1; i >= j; i--) {
                coef[i] = (coef[i] - coef[i - 1]) / (xNodes[i] - xNodes[i - j]);
            }
        }
        return coef;
    }

    public static double evaluateNewton(double[] xNodes, double[] coef, double x) {
        int n = coef.length - 1;
        double result = coef[n];
        for (int k = n - 1; k >= 0; k--) {
            result = coef[k] + (x - xNodes[k]) * result;
        }
        return result;
    }

    public static SplineCoefficients buildNaturalCubicSpline(double[] x, double[] y) {
        int n = x.length - 1;
        double[] h = new double[n];
        for (int i = 0; i < n; i++) {
            h[i] = x[i + 1] - x[i];
        }

        double[] alpha = new double[n + 1];
        for (int i = 1; i < n; i++) {
            alpha[i] = (3.0 / h[i]) * (y[i + 1] - y[i]) - (3.0 / h[i - 1]) * (y[i] - y[i - 1]);
        }

        double[] l = new double[n + 1];
        double[] mu = new double[n + 1];
        double[] z = new double[n + 1];
        l[0] = 1.0;

        for (int i = 1; i < n; i++) {
            l[i] = 2.0 * (x[i + 1] - x[i - 1]) - h[i - 1] * mu[i - 1];
            mu[i] = h[i] / l[i];
            z[i] = (alpha[i] - h[i - 1] * z[i - 1]) / l[i];
        }

        l[n] = 1.0;
        double[] a = new double[n];
        double[] b = new double[n];
        double[] c = new double[n + 1];
        double[] d = new double[n];

        for (int j = n - 1; j >= 0; j--) {
            a[j] = y[j];
            c[j] = z[j] - mu[j] * c[j + 1];
            b[j] = (y[j + 1] - y[j]) / h[j] - h[j] * (c[j + 1] + 2.0 * c[j]) / 3.0;
            d[j] = (c[j + 1] - c[j]) / (3.0 * h[j]);
        }

        double[] cTrim = new double[n];
        System.arraycopy(c, 0, cTrim, 0, n);
        return new SplineCoefficients(x.clone(), a, b, cTrim, d);
    }

    public static double evaluateSpline(SplineCoefficients sp, double val) {
        int n = sp.a.length;
        int idx = n - 1;
        for (int i = 0; i < n; i++) {
            if (val <= sp.x[i + 1]) {
                idx = i;
                break;
            }
        }
        double dx = val - sp.x[idx];
        return sp.a[idx] + sp.b[idx] * dx + sp.c[idx] * dx * dx + sp.d[idx] * dx * dx * dx;
    }

    public static double rungeFunction(double x) {
        return 1.0 / (1.0 + 25.0 * x * x);
    }

    public static double[] computeBarycentricWeights(double[] xNodes) {
        int n = xNodes.length;
        double[] w = new double[n];
        for (int j = 0; j < n; j++) {
            w[j] = 1.0;
            for (int k = 0; k < n; k++) {
                if (j != k) {
                    w[j] /= (xNodes[j] - xNodes[k]);
                }
            }
        }
        return w;
    }

    public static double evaluateBarycentric(double[] xNodes, double[] yNodes, double[] w, double x) {
        for (int j = 0; j < xNodes.length; j++) {
            if (Math.abs(x - xNodes[j]) < 1e-14) {
                return yNodes[j];
            }
        }
        double num = 0.0;
        double den = 0.0;
        for (int j = 0; j < xNodes.length; j++) {
            double term = w[j] / (x - xNodes[j]);
            num += term * yNodes[j];
            den += term;
        }
        return num / den;
    }

    public static OlsResult fitDampedOscillator(double[] t, double[] amplitude, double omega0) {
        int n = t.length;
        double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            y[i] = Math.log(amplitude[i]);
        }

        double sumT = 0.0, sumY = 0.0, sumT2 = 0.0, sumTY = 0.0;
        for (int i = 0; i < n; i++) {
            sumT += t[i];
            sumY += y[i];
            sumT2 += t[i] * t[i];
            sumTY += t[i] * y[i];
        }

        double denom = n * sumT2 - sumT * sumT;
        double slope = (n * sumTY - sumT * sumY) / denom;
        double intercept = (sumY - slope * sumT) / n;

        double lambda = -slope;
        double a0 = Math.exp(intercept);
        double qFactor = omega0 / (2.0 * lambda);

        double ssRes = 0.0;
        double meanY = sumY / n;
        double ssTot = 0.0;
        for (int i = 0; i < n; i++) {
            double yPred = intercept + slope * t[i];
            double res = y[i] - yPred;
            ssRes += res * res;
            ssTot += (y[i] - meanY) * (y[i] - meanY);
        }

        double s2 = ssRes / (n - 2);
        double sigmaSlope = Math.sqrt(s2 * n / denom);
        double sigmaIntercept = Math.sqrt(s2 * sumT2 / denom);
        double sigmaA0 = a0 * sigmaIntercept;
        double sigmaQ = qFactor * (sigmaSlope / lambda);
        double rSquared = 1.0 - ssRes / ssTot;

        return new OlsResult(a0, sigmaA0, lambda, sigmaSlope, qFactor, sigmaQ, rSquared, slope, intercept);
    }

    public static void exportRungeCsv(int[] nValues, double[] errUniform, double[] errCheb) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("runge_error.csv"))) {
            writer.println("n,error_uniform,error_chebyshev");
            for (int i = 0; i < nValues.length; i++) {
                writer.printf(Locale.US, "%d,%.8e,%.8e%n", nValues[i], errUniform[i], errCheb[i]);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    static class PlotPanel extends JPanel {
        private final String title, xLabel, yLabel;
        private final double[][] curvesX, curvesY;
        private final Color[] colors;
        private final String[] legends;
        private final boolean[] isScatter;
        private final boolean logY;

        public PlotPanel(String title, String xLabel, String yLabel,
                         double[][] curvesX, double[][] curvesY,
                         Color[] colors, String[] legends, boolean[] isScatter, boolean logY) {
            this.title = title;
            this.xLabel = xLabel;
            this.yLabel = yLabel;
            this.curvesX = curvesX;
            this.curvesY = curvesY;
            this.colors = colors;
            this.legends = legends;
            this.isScatter = isScatter;
            this.logY = logY;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int padL = 85, padR = 35, padT = 55, padB = 65;
            int plotW = w - padL - padR;
            int plotH = h - padT - padB;

            double minX = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
            double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;

            for (int c = 0; c < curvesX.length; c++) {
                for (int i = 0; i < curvesX[c].length; i++) {
                    double xv = curvesX[c][i];
                    double yv = logY ? Math.log10(curvesY[c][i]) : curvesY[c][i];
                    if (xv < minX) minX = xv;
                    if (xv > maxX) maxX = xv;
                    if (yv < minY) minY = yv;
                    if (yv > maxY) maxY = yv;
                }
            }

            double dxRange = (maxX - minX) * 0.06;
            double dyRange = (maxY - minY) * 0.08;
            if (dxRange == 0) dxRange = 1.0;
            if (dyRange == 0) dyRange = 1.0;
            minX -= dxRange;
            maxX += dxRange;
            minY -= dyRange;
            maxY += dyRange;

            g2.setColor(new Color(245, 247, 250));
            g2.fillRect(padL, padT, plotW, plotH);

            g2.setColor(new Color(220, 224, 230));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            int gridLines = 6;
            for (int i = 0; i <= gridLines; i++) {
                int gx = padL + (i * plotW) / gridLines;
                int gy = padT + (i * plotH) / gridLines;
                g2.drawLine(gx, padT, gx, padT + plotH);
                g2.drawLine(padL, gy, padL + plotW, gy);

                double xVal = minX + i * (maxX - minX) / gridLines;
                double yVal = maxY - i * (maxY - minY) / gridLines;
                g2.setColor(new Color(60, 65, 75));
                g2.drawString(String.format(Locale.US, "%.2f", xVal), gx - 14, padT + plotH + 20);
                String yStr = logY ? String.format(Locale.US, "1e%.1f", yVal) : String.format(Locale.US, "%.2f", yVal);
                g2.drawString(yStr, 15, gy + 4);
                g2.setColor(new Color(220, 224, 230));
            }

            g2.setColor(new Color(40, 45, 55));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRect(padL, padT, plotW, plotH);

            g2.setFont(new Font("SansSerif", Font.BOLD, 15));
            g2.drawString(title, padL, 30);

            g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
            g2.drawString(xLabel, padL + plotW / 2 - 40, h - 18);

            AffineTransform orig = g2.getTransform();
            g2.rotate(-Math.PI / 2);
            g2.drawString(yLabel, -(padT + plotH / 2 + 40), 22);
            g2.setTransform(orig);

            for (int c = 0; c < curvesX.length; c++) {
                g2.setColor(colors[c]);
                if (isScatter[c]) {
                    for (int i = 0; i < curvesX[c].length; i++) {
                        double yv = logY ? Math.log10(curvesY[c][i]) : curvesY[c][i];
                        int px = padL + (int) ((curvesX[c][i] - minX) / (maxX - minX) * plotW);
                        int py = padT + plotH - (int) ((yv - minY) / (maxY - minY) * plotH);
                        g2.fill(new Ellipse2D.Double(px - 4.5, py - 4.5, 9, 9));
                    }
                } else {
                    g2.setStroke(new BasicStroke(2.3f));
                    Path2D path = new Path2D.Double();
                    for (int i = 0; i < curvesX[c].length; i++) {
                        double yv = logY ? Math.log10(curvesY[c][i]) : curvesY[c][i];
                        int px = padL + (int) ((curvesX[c][i] - minX) / (maxX - minX) * plotW);
                        int py = padT + plotH - (int) ((yv - minY) / (maxY - minY) * plotH);
                        if (i == 0) path.moveTo(px, py);
                        else path.lineTo(px, py);
                    }
                    g2.draw(path);
                }
            }

            int legX = padL + 18;
            int legY = padT + 18;
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            for (int c = 0; c < legends.length; c++) {
                g2.setColor(colors[c]);
                if (isScatter[c]) {
                    g2.fillOval(legX, legY + c * 20 - 8, 8, 8);
                } else {
                    g2.setStroke(new BasicStroke(2.5f));
                    g2.drawLine(legX - 2, legY + c * 20 - 4, legX + 14, legY + c * 20 - 4);
                }
                g2.setColor(new Color(30, 35, 45));
                g2.drawString(legends[c], legX + 22, legY + c * 20);
            }
        }
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        double[] xNodes = {0.0, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0};
        double[] yNodes = new double[xNodes.length];
        for (int i = 0; i < xNodes.length; i++) {
            yNodes[i] = Math.sin(xNodes[i]) + Math.cos(0.5 * xNodes[i]);
        }

        double[] newtonCoef = computeNewtonCoefficients(xNodes, yNodes);
        SplineCoefficients spline = buildNaturalCubicSpline(xNodes, yNodes);

        System.out.println("=== ЗАДАНИЕ 2: ИНТЕРПОЛЯЦИЯ ТАБЛИЧНОЙ ФУНКЦИИ ===");
        System.out.printf("%-8s %-14s %-14s %-14s %-14s%n", "x", "Точное f(x)", "Лагранж P6", "Ньютон N6", "Сплайн S3");
        double[] testPoints = {0.25, 0.75, 1.25, 1.75, 2.25, 2.75};
        for (double pt : testPoints) {
            double exact = Math.sin(pt) + Math.cos(0.5 * pt);
            double lag = evaluateLagrange(xNodes, yNodes, pt);
            double newt = evaluateNewton(xNodes, newtonCoef, pt);
            double spl = evaluateSpline(spline, pt);
            System.out.printf("%-8.2f %-14.6f %-14.6f %-14.6f %-14.6f%n", pt, exact, lag, newt, spl);
        }

        System.out.println("\n=== ЗАДАНИЕ 3: ИССЛЕДОВАНИЕ ФЕНОМЕНА РУНГЕ ===");
        System.out.printf("%-6s %-22s %-22s%n", "n", "Макс. ошибка (равн.)", "Макс. ошибка (Чебышёв)");
        int[] nValues = {4, 6, 8, 10, 12, 14, 16, 18, 20};
        double[] nArr = new double[nValues.length];
        double[] errUniformArr = new double[nValues.length];
        double[] errChebArr = new double[nValues.length];

        int denseCount = 2001;
        double[] xDense = new double[denseCount];
        double[] yDense = new double[denseCount];
        for (int i = 0; i < denseCount; i++) {
            xDense[i] = -1.0 + 2.0 * i / (denseCount - 1);
            yDense[i] = rungeFunction(xDense[i]);
        }

        for (int idx = 0; idx < nValues.length; idx++) {
            int n = nValues[idx];
            nArr[idx] = n;

            double[] xu = new double[n + 1];
            double[] yu = new double[n + 1];
            for (int k = 0; k <= n; k++) {
                xu[k] = -1.0 + 2.0 * k / n;
                yu[k] = rungeFunction(xu[k]);
            }
            double[] wu = computeBarycentricWeights(xu);
            double maxErrU = 0.0;
            for (int i = 0; i < denseCount; i++) {
                double pVal = evaluateBarycentric(xu, yu, wu, xDense[i]);
                maxErrU = Math.max(maxErrU, Math.abs(yDense[i] - pVal));
            }
            errUniformArr[idx] = maxErrU;

            double[] xc = new double[n + 1];
            double[] yc = new double[n + 1];
            for (int k = 0; k <= n; k++) {
                xc[k] = Math.cos((2.0 * k + 1.0) * Math.PI / (2.0 * (n + 1)));
                yc[k] = rungeFunction(xc[k]);
            }
            double[] wc = computeBarycentricWeights(xc);
            double maxErrC = 0.0;
            for (int i = 0; i < denseCount; i++) {
                double pVal = evaluateBarycentric(xc, yc, wc, xDense[i]);
                maxErrC = Math.max(maxErrC, Math.abs(yDense[i] - pVal));
            }
            errChebArr[idx] = maxErrC;

            System.out.printf("%-6d %-22.6e %-22.6e%n", n, maxErrU, maxErrC);
        }

        exportRungeCsv(nValues, errUniformArr, errChebArr);

        System.out.println("\n=== ЗАДАНИЕ 4: МНК (ВАРИАНТ 1 — ЗАТУХАНИЕ КОЛЕБАНИЙ) ===");
        double[] tExp = {0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0};
        double[] aExp = {10.12, 8.54, 7.49, 6.31, 5.56, 4.68, 4.12, 3.45, 3.04, 2.57, 2.26, 1.90, 1.68};
        double omega0 = 2.0;
        OlsResult ols = fitDampedOscillator(tExp, aExp, omega0);

        System.out.printf("Начальная амплитуда A0 = %.4f +/- %.4f см%n", ols.a0, ols.sigmaA0);
        System.out.printf("Коэффициент затухания lambda = %.5f +/- %.5f с^-1%n", ols.lambda, ols.sigmaLambda);
        System.out.printf("Добротность Q = %.3f +/- %.3f%n", ols.qFactor, ols.sigmaQ);
        System.out.printf("Коэффициент детерминации R^2 = %.6f%n", ols.rSquared);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("СРО 12 — Интерполяция и МНК (Габдулла Алишер, КазНУ)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(960, 640);

            int m = 300;
            double[] xCurve = new double[m];
            double[] yLagCurve = new double[m];
            double[] ySplCurve = new double[m];
            for (int i = 0; i < m; i++) {
                xCurve[i] = 3.0 * i / (m - 1);
                yLagCurve[i] = evaluateLagrange(xNodes, yNodes, xCurve[i]);
                ySplCurve[i] = evaluateSpline(spline, xCurve[i]);
            }

            PlotPanel panel1 = new PlotPanel(
                    "Задание 2: Интерполяция полиномом Лагранжа и кубическим сплайном",
                    "Координата x", "Значение f(x)",
                    new double[][]{xCurve, xCurve, xNodes},
                    new double[][]{yLagCurve, ySplCurve, yNodes},
                    new Color[]{new Color(27, 79, 114), new Color(211, 84, 0), new Color(192, 57, 43)},
                    new String[]{"Полином Лагранжа P6(x)", "Кубический сплайн S3(x)", "Узлы таблицы"},
                    new boolean[]{false, false, true},
                    false
            );

            PlotPanel panel2 = new PlotPanel(
                    "Задание 3: Феномен Рунге (максимальная погрешность на сетках)",
                    "Степень полинома n", "Max |f(x) - Pn(x)| (лог. шкала)",
                    new double[][]{nArr, nArr},
                    new double[][]{errUniformArr, errChebArr},
                    new Color[]{new Color(192, 57, 43), new Color(39, 174, 96)},
                    new String[]{"Равномерная сетка", "Узлы Чебышёва"},
                    new boolean[]{false, false},
                    true
            );

            double[] tFit = new double[200];
            double[] aFit = new double[200];
            for (int i = 0; i < 200; i++) {
                tFit[i] = 12.0 * i / 199.0;
                aFit[i] = ols.a0 * Math.exp(-ols.lambda * tFit[i]);
            }

            PlotPanel panel3 = new PlotPanel(
                    "Задание 4: Затухание амплитуды A(t) = A0 * exp(-lambda * t) (Вариант 1)",
                    "Время t, с", "Амплитуда A(t), см",
                    new double[][]{tFit, tExp},
                    new double[][]{aFit, aExp},
                    new Color[]{new Color(27, 79, 114), new Color(192, 57, 43)},
                    new String[]{"Кривая МНК: A(t) = 10.02 * exp(-0.150 * t)", "Экспериментальные данные"},
                    new boolean[]{false, true},
                    false
            );

            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("1. Интерполяция", panel1);
            tabs.addTab("2. Феномен Рунге", panel2);
            tabs.addTab("3. МНК (Вариант 1)", panel3);

            frame.add(tabs);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}