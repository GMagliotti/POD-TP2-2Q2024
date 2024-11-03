package ar.edu.itba.pod.grupo9.query2;

import ar.edu.itba.pod.grupo9.model.Pair;
import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

public class YtdEarningsCombinerFactory implements CombinerFactory<Pair<String, Pair<Integer,Integer>>, Double, Double> {
    @Override
    public Combiner<Double, Double> newCombiner(Pair<String, Pair<Integer, Integer>> stringPairPair) {
        return new YtdEarningsCombiner();
    }

    private static class YtdEarningsCombiner extends Combiner<Double, Double> {
        private double amount = 0;

        @Override
        public void combine(Double aDouble) {
            amount += aDouble;
        }

        @Override
        public Double finalizeChunk() {
            return amount;
        }

        @Override
        public void reset() {
            amount = 0;
        }
    }
}
