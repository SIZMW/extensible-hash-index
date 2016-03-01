package ehindex.main;

/**
 * This class is used to demonstrate the extensible hash index.
 *
 * @author Aditya Nivarthi
 */
public class Value implements Comparable<Value> {
    public final int val;

    /**
     * Creates a Value instance with the specified value.
     *
     * @param val
     *            The integer value to set.
     */
    public Value(int val) {
        this.val = val;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Value o) {
        return (val < o.val) ? -1 : (val == o.val) ? 0 : 1;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof Value) {
            Value p = (Value) obj;
            return val == p.val;
        }

        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return val;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Value: " + val;
    }
}
