package ar.edu.itba.pod.grupo9.model;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;

public class InfractionSummary implements DataSerializable {
    private double minAmount;
    private double maxAmount;
    private double difference;

    public InfractionSummary() {}

    public InfractionSummary(double minAmount, double maxAmount, double difference) {
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.difference = difference;
    }

    public double getMinAmount() { return minAmount; }
    public double getMaxAmount() { return maxAmount; }
    public double getDifference() { return difference; }

    @Override
    public String toString() {
        return String.format("Min: %.2f, Max: %.2f, Diff: %.2f", minAmount, maxAmount, difference);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeDouble(minAmount);
        out.writeDouble(maxAmount);
        out.writeDouble(difference);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        minAmount = in.readDouble();
        maxAmount = in.readDouble();
        difference = in.readDouble();
    }
}

