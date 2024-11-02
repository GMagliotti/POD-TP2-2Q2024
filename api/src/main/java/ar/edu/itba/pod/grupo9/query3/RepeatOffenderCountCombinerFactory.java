package ar.edu.itba.pod.grupo9.query3;

import ar.edu.itba.pod.grupo9.model.Pair;
import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

/**
 * CombinerFactory for the RepeatOffenderCount query.
 * Captures the emitted <plate, county name> and counts the number of times they appear.
 */
@SuppressWarnings("deprecation")
public class RepeatOffenderCountCombinerFactory implements CombinerFactory<Pair<String,String>, Integer, Integer> {

        @Override
        public Combiner<Integer, Integer> newCombiner(Pair<String,String> key) {
            return new RepeatOffenderCountCombiner();
        }

        private static class RepeatOffenderCountCombiner extends Combiner<Integer, Integer> {
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
