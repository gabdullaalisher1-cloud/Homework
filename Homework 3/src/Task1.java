public class Task1 {
    public static void main(String[] args) {
        int[][] array = {
                {1, -2, 3},
                {4, 5, -6},
                {7, 8, 9}
        };
        System.out.println(sumOfPositiveElements(array));
    }

    public static int sumOfPositiveElements(int[][] array) {
        int sum = 0;
        if (array == null) {
            return sum;
        }
        for (int[] row : array) {
            if (row != null) {
                for (int val : row) {
                    if (val > 0) {
                        sum += val;
                    }
                }
            }
        }
        return sum;
    }
}