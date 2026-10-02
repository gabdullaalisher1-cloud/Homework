public class Task5 {
    public static void main(String[] args) {
        int[][] array = {
                {1, 2, 3},
                {10, 20, 30},
                {7, 8, 9}
        };

        System.out.println(sumOfSecondRow(array));
    }

    public static int sumOfSecondRow(int[][] array) {
        if (array == null || array.length < 2 || array[1] == null) {
            return -1;
        }

        int sum = 0;
        for (int val : array[1]) {
            sum += val;
        }
        return sum;
    }
}