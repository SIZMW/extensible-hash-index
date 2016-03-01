package ehindex.index;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import ehindex.bucket.Bucket;
import ehindex.bucket.BucketType;
import ehindex.bucket.HashBucket;
import ehindex.bucket.StandardBucket;

/**
 * This class is the main index class that can be instantiated to create the
 * extensible hash index.
 *
 * @author Aditya Nivarthi
 */
public class ExtensibleHashIndex {

    private static final Logger logger = Logger.getGlobal();

    /**
     * Prime number hash value to use for modulo.
     */
    public static final int HASH_VALUE = 1610612741;

    /**
     * The maximum number of objects to hold within a bucket.
     */
    public final int MAX_SIZE;

    protected int globalBitSize = 1;
    protected List<Bucket> directory;
    protected BucketType type;

    /**
     * Creates an ExtensibleHashIndex instance with the specified maximum bucket
     * size and bucket type.
     *
     * @param maxSize
     *            The maximum number of objects to hold within a bucket.
     * @param type
     *            The type of buckets to use for storing objects.
     */
    public ExtensibleHashIndex(int maxSize, BucketType type) {
        logger.setLevel(Level.OFF);
        MAX_SIZE = maxSize;
        this.type = type;
        directory = new ArrayList<Bucket>();
    }

    /**
     * Creates and adds a new bucket to the directory of the index.
     *
     * @param hashCode
     *            The hash code value to use for the new bucket.
     * @param bitSize
     *            The number of bits to use for the mask of the new bucket.
     * @return a Bucket
     */
    protected Bucket addNewBucket(int hashCode, int bitSize) {
        Bucket bucket;

        // Check type of bucket to add
        if (type.equals(BucketType.HASH)) {
            bucket = new HashBucket(MAX_SIZE, hashCode, bitSize);
        } else {
            bucket = new StandardBucket(MAX_SIZE, hashCode, bitSize);
        }

        logger.log(Level.INFO, type + " bucket added");

        directory.add(bucket);
        return bucket;
    }

    /**
     * Cleans and removes empty buckets from the directory.
     *
     * @return true if at least one bucket was removed, false otherwise
     */
    protected synchronized boolean cleanBuckets() {
        logger.log(Level.INFO, "Bucket cleaning");
        boolean ret = false;
        for (Bucket e : directory) {
            // Remove bucket if it is empty
            if (e.getCurrentSize() <= 0) {
                directory.remove(e);
                logger.log(Level.INFO, "Directory removed");
                ret = true;
            }
        }
        return ret;
    }

    /**
     * Inserts the specified object into the index based on the hash code.
     *
     * @param obj
     *            The object to insert.
     * @return true on successful insert, false otherwise
     */
    public synchronized boolean insert(Object obj) {
        int hashCode = ExtensibleHashIndex.getHashNumber(obj.hashCode());
        logger.log(Level.INFO, "Inserting object: " + obj);

        for (Bucket e : directory) {
            // Find the right bucket based on hash code
            if (e.compareNumber(hashCode)) {
                // If the bucket is full, handle splitting the bucket
                if (e.isFull()) {
                    logger.log(Level.INFO, "Bucket is full");
                    // Check for duplication on hash buckets
                    if (type.equals(BucketType.HASH)) {
                        if (e.contains(obj)) {
                            logger.log(Level.INFO, "Duplicate object found");
                            return true;
                        }
                    }
                    return splitBuckets(e, obj);
                } else {
                    logger.log(Level.INFO, "Insert object into directory: " + e.bucketNumber);
                    return e.insert(obj);
                }
            }
        }

        Bucket b = addNewBucket(hashCode & ExtensibleHashIndex.getBucketMask(globalBitSize), globalBitSize);
        b.insert(obj);
        return true;
    }

    /**
     * Prints the entire index and bucket information.
     */
    public void printIndex() {
        String tab = "    ";
        System.out.println("\n-----------------------\n");
        System.out.println("EHINDEX:");
        System.out.println("Global bits: " + globalBitSize);
        for (Bucket e : directory) {
            System.out.println("BUCKET:");
            System.out.println(tab + "NUM: " + e.bucketNumber);
            System.out.println(tab + "BITS: " + e.getBits());
            System.out.println(tab + "SIZE: " + e.getCurrentSize());
            System.out.println("OBJECTS: ");

            Iterator<Object> iterator = e.iterator();
            while (iterator.hasNext()) {
                System.out.println(tab + tab + iterator.next().toString());
            }
        }
    }

    /**
     * Removes the specified object from the index.
     *
     * @param obj
     *            The object to be removed.
     * @return Optional with the removed object if it was removed, empty if
     *         object was not found
     */
    public synchronized Optional<Object> remove(Object obj) {
        int hashCode = ExtensibleHashIndex.getHashNumber(obj.hashCode());

        // Remove the object from the appropriate bucket
        for (Bucket e : directory) {
            if (e.compareNumber(hashCode)) {
                Optional<Object> ret = e.remove(obj);

                if (e.getCurrentSize() <= 0) {
                    directory.remove(e);
                }
                logger.log(Level.INFO, "Object for removal is present: " + ret.isPresent());
                return ret;
            }
        }

        logger.log(Level.INFO, "Object for removal is not present");
        return Optional.empty();
    }

    /**
     * Splits a bucket into two buckets with higher bit masks when a bucket
     * becomes full.
     *
     * @param record
     *            The bucket to be split.
     * @param obj
     *            The object to insert after buckets are split.
     * @return true on successful split and insert of mismatched and new values,
     *         false otherwise
     */
    protected synchronized boolean splitBuckets(Bucket record, Object obj) {
        boolean returnStatus = true;

        // Get the previous number of bits on the bucket
        int bitCount = record.getBits();

        // Increase the number of bits
        record.increaseBits();

        // Check if global bits need to be incremented
        if (record.getBits() > globalBitSize) {
            logger.log(Level.INFO, "Increase global bit count");
            globalBitSize++;
        }

        // Get the mismatches on the new bit mask
        Set<Object> mismatches = record.getMismatchedItems();

        // Decrease the bits before removal of mismatches
        record.decreaseBits();

        logger.log(Level.INFO, "Removing mismatched objects");
        // Remove the mismatches with lower mask so we can find their old hashes
        for (Object o : mismatches) {
            remove(o);
        }

        // Increase the bits for the mask
        record.increaseBits();

        // Get the new bucket to create
        // Formula is <new number> = <old number> + 2^(<old bit count>)
        int hashB = bitCount;
        int hashN = record.bucketNumber;
        int hashNewN = (int) (hashN + Math.pow(2, hashB));

        addNewBucket(hashNewN, record.getBits());

        // Reinsert the mismatch objects into their new buckets
        for (Object o : mismatches) {
            returnStatus = returnStatus && insert(o);
        }

        returnStatus = returnStatus && insert(obj);

        // Clean buckets if more splits happened and empty buckets were left
        // behind
        cleanBuckets();
        return returnStatus;
    }

    /**
     * Returns the mask for the specified number of bits.
     *
     * @param bits
     *            The number of bits to turn on the for mask.
     * @return an integer
     */
    public static int getBucketMask(int bits) {
        int sum = 0;
        for (int i = bits - 1; i >= 0; i--) {
            sum = sum | (int) Math.pow(2, i);
        }

        logger.log(Level.INFO, "Returns bit mask: " + sum);
        return sum;
    }

    /**
     * Returns the hash number based on the hash code. Hash code is modded (%)
     * by the constant ExtensibleHashIndex.HASH_VALUE for hash number.
     *
     * @param hashCode
     *            The hash code to use for generating a hash number.
     * @return an integer
     */
    public static int getHashNumber(int hashCode) {
        return hashCode % HASH_VALUE;
    }
}
