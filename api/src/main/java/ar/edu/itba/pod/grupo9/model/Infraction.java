package ar.edu.itba.pod.grupo9.model;

import com.hazelcast.nio.serialization.DataSerializable;

/**
 * {@link Infraction} is a simple class that represents an infraction. It has a code and a description.
 * <p>
 * {@code code} is the code of the infraction.
 * {@code description} is the description of the infraction.
 * </p>
 * <p>
 * This class is {@link DataSerializable} to allow Hazelcast to serialize and deserialize it.
 * <p>
 * This class is immutable.
 */
public class Infraction implements DataSerializable {
    private String code;
    private String description;

    public Infraction() {
        // for Hazelcast
    }

    public Infraction(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static Infraction fromInfractionCsv(String[] line) {
        return new Infraction(
                line[0],
                line[1]
        );
    }

    @Override
    public void writeData(com.hazelcast.nio.ObjectDataOutput out) throws java.io.IOException {
        out.writeUTF(code);
        out.writeUTF(description);
    }

    @Override
    public void readData(com.hazelcast.nio.ObjectDataInput in) throws java.io.IOException {
        code = in.readUTF();
        description = in.readUTF();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Infraction infraction = (Infraction) obj;
        return code.equals(infraction.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode() + description.hashCode();
    }

    @Override
    public String toString() {
        return "Infraction{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
