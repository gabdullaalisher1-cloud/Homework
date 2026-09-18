public class Task5 {
    public static void compareArrayHalves(int[] array) {
        int leftSum = 0;
        int rightSum = 0;
        int middle = array.length / 2;

        for (int i = 0; i < middle; i++) {
            leftSum = leftSum + array[i];
        }

        for (int i = middle; i < array.length; i++) {
            rightSum = rightSum + array[i];
        }

        if (leftSum > rightSum) {
            System.out.println("Левая половина больше");
        } else if (rightSum > leftSum) {
            System.out.println("Правая половина больше");
        } else {
            System.out.println("Половины равны");
        }
    }
}