public class Task4 {
    public static void main(String[] args) {
        int[][] array = {
                {-10, 15, 3},
                {42, -5, 0},
                {7, 18, -9}
        };

        System.out.println(findMax(array));
    }

    public static int findMax(int[][] array) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException();
        }

        int max = Integer.MIN_VALUE;

        for (int[] row : array) {
            if (row == null) {
                continue;
            }
            for (int val : row) {
                if (val > max) {
                    max = val;
                }
            }
        }
        return max;
    }
}