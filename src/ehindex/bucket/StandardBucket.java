package ehindex.bucket;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * This class is used to model the basic bucket structure for the extensible
 * hash index. This bucket holds objects based on the hash code value of the
 * objects being inserted.
 *
 * @author Aditya Nivarthi
 */
public class StandardBucket extends Bucket {
    protected List<Object> list;

    /**
     * Creates a StandardBucket instance with the specified maximum size, bucket
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
    public StandardBucket(int maxSize, int bucketNumber, int bitCount) {
        super(maxSize, bucketNumber, bitCount);
        list = new ArrayList<Object>();
    }

    /*
     * (non-Javadoc)
     *
     * @see ehindex.index.Bucket#contains(java.lang.Object)
     */
    @Override
    public synchronized boolean contains(Object obj) {
        return list.contains(obj);
    }

    /*
     * (non-Javadoc)
     *
     * @see ehindex.index.Bucket#getMismatchedItems()
     */
    @Override
    public synchronized Set<Object> getMismatchedItems() {
        Set<Object> mismatches = new HashSet<Object>();
        for (Object obj : list) {
            if (!compareNumber(obj.hashCode())) {
                mismatches.add(obj);
            }
        }

        return mismatches;
    }

    /*
     * Returns true if the object was inserted, false if the object was not
     * inserted for some internal reason.
     *
     * @see ehindex.index.Bucket#insert(java.lang.Object)
     */
    @Override
    public synchronized boolean insert(Object obj) {
        if (list.add(obj)) {
            currentSize++;
            return true;
        }

        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see ehindex.index.Bucket#iterator()
     */
    @Override
    public Iterator<Object> iterator() {
        return list.iterator();
    }

    /*
     * (non-Javadoc)
     *
     * @see ehindex.index.Bucket#remove(java.lang.Object)
     */
    @Override
    public synchronized Optional<Object> remove(Object obj) {
        if (list.remove(obj)) {
            currentSize--;
            return Optional.of(obj);
        }

        return Optional.empty();
    }
}
