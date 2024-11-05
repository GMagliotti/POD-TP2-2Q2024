package ar.edu.itba.pod.grupo9.query4;

import ar.edu.itba.pod.grupo9.model.InfractionSummary;
import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

@SuppressWarnings("deprecation")
public class TopAmountInfractionDifferenceByAgencyCombinerFactory implements CombinerFactory<String, InfractionSummary, InfractionSummary> {
    @Override
    public Combiner<InfractionSummary, InfractionSummary> newCombiner(String infractionId) {
        return new TopAmountInfractionDifferenceByAgencyCombiner();
    }

    private static class TopAmountInfractionDifferenceByAgencyCombiner extends Combiner<InfractionSummary, InfractionSummary> {
        private double minAmount = Double.MAX_VALUE;
        private double maxAmount = Double.MIN_VALUE;

        @Override
        public void combine(InfractionSummary summary) {
            if (summary.getMinAmount() < minAmount) {
                minAmount = summary.getMinAmount();
            }
            if (summary.getMaxAmount() > maxAmount) {
                maxAmount = summary.getMaxAmount();
            }
        }

        @Override
        public InfractionSummary finalizeChunk() {
            double difference = maxAmount - minAmount;
            InfractionSummary summary = new InfractionSummary(minAmount, maxAmount, difference);
            minAmount = Double.MAX_VALUE;
            maxAmount = Double.MIN_VALUE;
            return summary;
        }
    }
}

