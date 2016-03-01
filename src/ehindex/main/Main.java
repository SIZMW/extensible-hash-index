package ehindex.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ehindex.bucket.BucketType;
import ehindex.index.ExtensibleHashIndex;

/**
 * Main method for testing extensible hash index.
 *
 * @author Aditya Nivarthi
 */
public class Main {

    /**
     * Main method.
     *
     * @param args
     *            Command line arguments.
     */
    public static void main(String[] args) {
        ExtensibleHashIndex index = new ExtensibleHashIndex(100, BucketType.STANDARD);
        Main.runInserts(index);
        Main.runRemoves(index);
    }

    /**
     * Runs 800 inserts on the specified index.
     *
     * @param index
     *            The extensible hash index to populate.
     */
    public static void runInserts(ExtensibleHashIndex index) {
        List<Long> times = new ArrayList<Long>();

        Random rand = new Random();
        rand.setSeed(1);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 800; i++) {
            long st = System.nanoTime();

            int val = rand.nextInt(1000);
            index.insert(new Value(val));
            long et = System.nanoTime();
            times.add(et - st);
        }

        long endTime = System.currentTimeMillis();

        System.out.println("Total Time: " + (endTime - startTime) + " ms");

        long sum = 0;
        for (Long l : times) {
            sum += l;
        }

        sum /= times.size();
        System.out.println("Average Insert Time: " + (sum) + " ns");
    }

    /**
     * Runs 800 removes on the specified index.
     * 
     * @param index
     *            The extensible hash index to remove values from.
     */
    public static void runRemoves(ExtensibleHashIndex index) {
        List<Long> times = new ArrayList<Long>();

        Random rand = new Random();
        rand.setSeed(1);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 800; i++) {
            long st = System.nanoTime();

            int val = rand.nextInt(1000);
            index.remove(new Value(val));
            long et = System.nanoTime();
            times.add(et - st);
        }

        long endTime = System.currentTimeMillis();

        System.out.println("Total Time: " + (endTime - startTime) + " ms");

        long sum = 0;
        for (Long l : times) {
            sum += l;
        }

        sum /= times.size();
        System.out.println("Average Remove Time: " + (sum) + " ns");
    }
}
