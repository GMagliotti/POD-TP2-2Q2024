package ar.edu.itba.pod.grupo9.query1;

import ar.edu.itba.pod.grupo9.model.Pair;
import com.hazelcast.mapreduce.ReducerFactory;
import com.hazelcast.mapreduce.Reducer;

@SuppressWarnings("deprecation")
public class InfractionAgencyCountReducerFactory implements ReducerFactory<Pair<String, String>, Integer, Integer> {
    @Override
    public com.hazelcast.mapreduce.Reducer<Integer, Integer> newReducer(Pair<String, String> key) {
        return new InfractionAgencyCountReducer();
    }

    private static class InfractionAgencyCountReducer extends Reducer<Integer, Integer> {
        private volatile int amount = 0;

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
