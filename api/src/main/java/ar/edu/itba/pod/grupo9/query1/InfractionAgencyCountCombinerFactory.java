package ar.edu.itba.pod.grupo9.query1;

import ar.edu.itba.pod.grupo9.model.Pair;
import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

@SuppressWarnings("deprecation")
public class InfractionAgencyCountCombinerFactory implements CombinerFactory<Pair<String, String>, Integer, Integer> {

    @Override
    public Combiner<Integer, Integer> newCombiner(Pair<String, String> key) {
        return new InfractionAgencyCountCombiner();
    }

    private static class InfractionAgencyCountCombiner extends Combiner<Integer, Integer> {
        private int amount = 0;

        @Override
        public void combine(Integer integer) {
            amount += integer;
        }

        @Override
        public Integer finalizeChunk() {
            return amount;
        }

        @Override
        public void reset() {
            amount = 0;
        }
    }

}
