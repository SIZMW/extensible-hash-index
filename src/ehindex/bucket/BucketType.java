package ehindex.bucket;

/**
 * This enumerated type defines the types of buckets that can be instantiated
 * within the extensible hash index class. Hash buckets can be used to reduced
 * duplicate objects based on the hash value and the equals() method on the
 * stored objects. Standard does not affect duplication.
 * 
 * @author Aditya Nivarthi
 */
public enum BucketType {
    HASH, STANDARD
}
