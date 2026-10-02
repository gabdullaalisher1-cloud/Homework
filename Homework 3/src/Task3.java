public class Task3 {
    public static void main(String[] args) {
        int[][] array = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        };
        zeroDiagonals(array);
        for (int[] row : array) {
            for (int val : row) {
                System.out.print(val + " ");
            }
            System.out.println();
        }
    }

    public static void zeroDiagonals(int[][] matrix) {
        if (matrix == null) {
            return;
        }
        int size = matrix.length;
        for (int i = 0; i < size; i++) {
            if (matrix[i] != null && matrix[i].length == size) {
                matrix[i][i] = 0;
                matrix[i][size - 1 - i] = 0;
            }
        }
    }
}