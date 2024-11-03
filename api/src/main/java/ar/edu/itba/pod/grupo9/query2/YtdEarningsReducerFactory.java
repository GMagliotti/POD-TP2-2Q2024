package ar.edu.itba.pod.grupo9.query2;

import ar.edu.itba.pod.grupo9.model.Pair;
import ar.edu.itba.pod.grupo9.query1.InfractionAgencyCountReducerFactory;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class YtdEarningsReducerFactory implements ReducerFactory<Pair<String, Pair<Integer, Integer>>, Double, Double> {
    @Override
    public Reducer<Double, Double> newReducer(Pair<String, Pair<Integer, Integer>> stringPairPair) {
        return new YtdEarningsReducer();
    }

    private static class YtdEarningsReducer extends Reducer<Double, Double> {
        private double amount = 0;

        @Override
        public void reduce(Double aDouble) {
            amount += aDouble;
        }

        @Override
        public Double finalizeReduce() {
            return amount;
        }
    }
}
