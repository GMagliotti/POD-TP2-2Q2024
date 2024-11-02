package ar.edu.itba.pod.grupo9.model;

import com.hazelcast.nio.serialization.DataSerializable;

/**
 * A simple pair of objects.
 * @param <A> the type of the first object
 * @param <B> the type of the second object
 */
public class Pair<A, B> implements DataSerializable {
    private A first;
    private B second;

    public Pair() {
        // for Hazelcast
    }

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public void writeData(com.hazelcast.nio.ObjectDataOutput out) throws java.io.IOException {
        out.writeObject(first);
        out.writeObject(second);
    }

    @Override
    public void readData(com.hazelcast.nio.ObjectDataInput in) throws java.io.IOException {
        first = in.readObject();
        second = in.readObject();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Pair<?, ?> pair = (Pair<?, ?>) obj;
        return first.equals(pair.first) && second.equals(pair.second);
    }

    @Override
    public int hashCode() {
        return first.hashCode() + second.hashCode();
    }

    @Override
    public String toString() {
        return first + ";" + second;
    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }

}
