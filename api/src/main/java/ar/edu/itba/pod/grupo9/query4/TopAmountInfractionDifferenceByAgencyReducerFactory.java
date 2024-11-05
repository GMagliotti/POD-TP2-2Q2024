package ar.edu.itba.pod.grupo9.query4;

import ar.edu.itba.pod.grupo9.model.InfractionSummary;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;


@SuppressWarnings("deprecation")
public class TopAmountInfractionDifferenceByAgencyReducerFactory implements ReducerFactory<String, InfractionSummary, InfractionSummary> {

    @Override
    public Reducer<InfractionSummary, InfractionSummary> newReducer(String infractionId) {
        return new TopAmountInfractionDifferenceByAgencyReducer();
    }

    private static class TopAmountInfractionDifferenceByAgencyReducer extends Reducer<InfractionSummary, InfractionSummary> {
        private double minAmount = Double.MAX_VALUE;
        private double maxAmount = Double.MIN_VALUE;

        @Override
        public void reduce(InfractionSummary summary) {
            if (summary.getMinAmount() < minAmount) {
                minAmount = summary.getMinAmount();
            }
            if (summary.getMaxAmount() > maxAmount) {
                maxAmount = summary.getMaxAmount();
            }
        }

        @Override
        public InfractionSummary finalizeReduce() {
            double difference = maxAmount - minAmount;
            return new InfractionSummary(minAmount, maxAmount, difference);
        }
    }
}


