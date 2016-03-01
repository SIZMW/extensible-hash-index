package ehindex.bucket;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class is used to model a bucket for the extensible hash index where
 * duplicate values based on hash code and equals() are not permitted. Duplicate
 * inserts are skipped to reduce bucket sizes.
 *
 * @author Aditya Nivarthi
 */
public class HashBucket extends Bucket {
    protected Set<Object> set;

    /**
     * Creates a HashBucket instance with the specified maximum size, bucket
     * number and masking bit count.
     *
     * @param maxSize
     *            The maximum number of objects to hold within a bucket of this
     *            type.
     * @param bucketNumber
     *            The unmasked bucket hash value for this bucket.
     * @param bitCount
     *            The number of bits to use for the mask for hash comparisons.
     */
    public HashBucket(int maxSize, int bucketNumber, int bitCount) {
        super(maxSize, bucketNumber, bitCount);
        set = new TreeSet<Object>();
    }

    /**
     * (non-Javadoc)
     *
     * @see ehindex.bucket.Bucket#contains(java.lang.Object)
     */
    @Override
    public synchronized boolean contains(Object obj) {
        return set.contains(obj);
    }

    /**
     * (non-Javadoc)
     *
     * @see ehindex.bucket.Bucket#getMismatchedItems()
     */
    @Override
    public synchronized Set<Object> getMismatchedItems() {
        Set<Object> mismatches = new HashSet<Object>();
        for (Object obj : set) {
            if (!compareNumber(obj.hashCode())) {
                mismatches.add(obj);
            }
        }

        return mismatches;
    }

    /**
     * Returns true if the object was inserted, false if a duplicate was found
     * and the object was not inserted.
     *
     * @see ehindex.bucket.Bucket#insert(java.lang.Object)
     */
    @Override
    public synchronized boolean insert(Object obj) {
        if (set.add(obj)) {
            currentSize++;
            return true;
        }

        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see ehindex.bucket.Bucket#iterator()
     */
    @Override
    public Iterator<Object> iterator() {
        return set.iterator();
    }

    /**
     * (non-Javadoc)
     *
     * @see ehindex.bucket.Bucket#remove(java.lang.Object)
     */
    @Override
    public synchronized Optional<Object> remove(Object obj) {
        if (set.remove(obj)) {
            currentSize--;
            return Optional.of(obj);
        }

        return Optional.empty();
    }
}
