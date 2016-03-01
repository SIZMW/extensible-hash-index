package ehindex.bucket;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import ehindex.index.ExtensibleHashIndex;

/**
 * This abstract class is used to define bucket methods and attributes for other
 * implementations of buckets for the extensible hash index.
 *
 * @author Aditya Nivarthi
 */
public abstract class Bucket {
    /**
     * The maximum number of objects to hold within a bucket of this type.
     */
    public final int MAX_SIZE;

    /**
     * The unmasked bucket hash value for this bucket.
     */
    public final int bucketNumber;

    protected int bits = 1;
    protected int currentSize = 0;

    /**
     * Creates a Bucket instance with the specified maximum size, bucket number
     * and masking bit count.
     *
     * @param maxSize
     *            The maximum number of objects to hold within a bucket of this
     *            type.
     * @param bucketNumber
     *            The unmasked bucket hash value for this bucket.
     * @param bitCount
     *            The number of bits to use for the mask for hash comparisons.
     */
    public Bucket(int maxSize, int bucketNumber, int bitCount) {
        MAX_SIZE = maxSize;
        bits = bitCount;
        this.bucketNumber = bucketNumber;
    }

    /**
     * Compares the specified number with this bucket's mask to the bucket's
     * masked number.
     *
     * @param number
     *            The number of the hash to compare.
     * @return true if masked numbers are equal, false otherwise
     */
    public boolean compareNumber(int number) {
        int mask = ExtensibleHashIndex.getBucketMask(bits);
        int currentVal = number & mask;
        int bucketVal = bucketNumber & mask;
        return currentVal == bucketVal;
    }

    /**
     * Returns whether the current bucket has the specified object.
     *
     * @param obj
     *            The object to find in this bucket.
     * @return true if the bucket has the object, false otherwise
     */
    public abstract boolean contains(Object obj);

    /**
     * Decreases the bit count on this bucket's comparison mask.
     *
     * @return true if bits are decreased by one, false otherwise
     */
    public boolean decreaseBits() {
        bits--;
        return true;
    }

    /**
     * Returns the number of bits used in this bucket's mask.
     *
     * @return an integer
     */
    public int getBits() {
        return bits;
    }

    /**
     * Returns the unmasked bucket hash value for this bucket.
     *
     * @return an integer
     */
    public int getBucketNumber() {
        return bucketNumber;
    }

    /**
     * Returns the number of objects in this bucket.
     *
     * @return an integer
     */
    public int getCurrentSize() {
        return currentSize;
    }

    /**
     * Returns a set of objects that do not match the current bucket's masked
     * hash value. This function should be used after calling increaseBits()
     * when splitting two buckets is needed to fit more values. All mismatched
     * objects based on hash code are returned in the output of this function
     * but not removed.
     *
     * @return a Set<Object>
     */
    public abstract Set<Object> getMismatchedItems();

    /**
     * Increases the bit count on this bucket's comparison mask.
     *
     * @return true if bits are increased by one, false otherwise
     */
    public boolean increaseBits() {
        bits++;
        return true;
    }

    /**
     * Inserts the specified object into this bucket.
     *
     * @param obj
     *            The object to insert into this bucket.
     * @return true if the object is inserted, false otherwise
     */
    public abstract boolean insert(Object obj);

    /**
     * Returns whether the bucket has reached maximum size.
     *
     * @return true if the bucket has reached maximum size, false otherwise
     */
    public boolean isFull() {
        return currentSize >= MAX_SIZE;
    }

    /**
     * Returns a list iterator over the elements in this list in proper
     * sequence.
     *
     * @return an Iterator<Object>
     */
    public abstract Iterator<Object> iterator();

    /**
     * Returns an Optional with the removed object if the object was removed, or
     * empty if the object was not found.
     *
     * @param obj
     *            The object to remove.
     * @return an Optional<Object>
     */
    public abstract Optional<Object> remove(Object obj);
}
