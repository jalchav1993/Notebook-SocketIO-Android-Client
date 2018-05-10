package edu.utep.cs.cs4330.notebookio.utility;

import java.util.concurrent.atomic.AtomicReference;

/**
 * How to use atomic reference
 * @author: Jesus Chavez
 * @macuser: aex on 4/27/18.
 */
public class Tuple<K , L> {
    /*package*/
    private static boolean equals(Object x, Object y) {
        return x == y || (x != null && x.equals(y));
    }
    public final K k;
    public final L l;

    public Tuple( K k, L l) {
        this.k = k;
        this.l = l;

    }
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        else if (!(obj instanceof Tuple<?, ?>)) {
            return false;
        }

        Tuple<?, ?> other = (Tuple<?, ?>)obj;
        return Tuple.equals(this.k, other.k)
                && Tuple.equals(this.l, other.l);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.k != null ? this.k.hashCode() : 0);
        hash = 79 * hash + (this.l != null ? this.l.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", k, l);
    }


}