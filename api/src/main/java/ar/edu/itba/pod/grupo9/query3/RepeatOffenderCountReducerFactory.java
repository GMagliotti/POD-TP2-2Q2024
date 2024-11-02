package ar.edu.itba.pod.grupo9.query3;

import ar.edu.itba.pod.grupo9.model.Pair;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

/**
 * Factory for the Reducer
 * Counts the amount of repeat offenders pairs of plate and county name
 */
@SuppressWarnings("deprecation")
public class RepeatOffenderCountReducerFactory implements ReducerFactory<Pair<String,String>, Integer, Integer> {

    @Override
    public Reducer<Integer, Integer> newReducer(Pair<String,String> key) {
        return new RepeatOffenderCountReducer();
    }

    private static class RepeatOffenderCountReducer extends Reducer<Integer, Integer> {
        private int amount = 0;

        @Override
        public void reduce(Integer integer) {
            amount += integer;
        }

        @Override
        public Integer finalizeReduce() {
            return amount;
        }
    }
}
