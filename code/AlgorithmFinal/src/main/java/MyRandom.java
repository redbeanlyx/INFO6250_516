public class MyRandom {
    public static int randomInt(int start, int end) {
        return (int) (Math.random() * (end - start + 1) + start);
    }

    public static boolean randomBoolean() {
        if (Math.random() < 0.5) {
            return true;
        }
        return false;
    }

    public static int[] randomIntNums(int start, int end, int num) {
        int[] nums = new int[num];
        for (int i = 0; i < nums.length; i++) {
            nums[i] = randomInt(start, end);
            for (int j = 0; j < i; j++) {
                if (nums[j] == nums[i]) {
                    nums[i] = randomInt(start, end);
                    j = -1;
                }
            }
        }
        return nums;
    }
}
