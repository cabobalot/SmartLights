package smartLights;
import java.util.*;

public class SmartRandom {
    private Random rand = new Random();
    private final int range;
    private final int tailSize;
    private List<Integer> nums;
    private Deque<Integer> usedNums;

    private static SmartRandom instance = new SmartRandom(16, 8);

    public static void setRange(int r, int tail) {
        instance = new SmartRandom(r, tail);
    }

    /**
     * get the next random number from the universal static instance
     * @return
     */
    public static int getFromStatic() {
        return instance.getRandomInt();
    }

    /**
     * generate numbers from 0 inclusive to range exclusive
     * @param r the range
     * @param tail when resetting the used list pull this many into the pool
     */
    public SmartRandom(int r, int tail) {
        range = r;
        tailSize = tail;
        nums = new ArrayList<>(range);
        usedNums = new ArrayDeque<>(range);
        for (int i = 0; i < range; i++) {
            nums.add(i);
        }
    }

    public int getRandomInt() {
        // pick a random unused number
        int next = nums.remove(rand.nextInt(nums.size()));
        usedNums.add(next);

        if (nums.isEmpty()) {
            for (int i = 0; i < tailSize; i++) {
                nums.add(usedNums.pop());
            }
        }

        return next;
    }

    /**
     * test the random generator
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Random numbers:");
        setRange(10, 5);
        for (int i = 0; i < 100; i++) {
            System.out.print(getFromStatic());
            System.out.print(",");
        }
        System.out.println();
        System.out.println("Done.");
    }

}
