package ar.itba.edu.pod.grupo9.reducer;

import ar.edu.itba.pod.grupo9.model.Pair;
import ar.edu.itba.pod.grupo9.query1.InfractionAgencyCountCombinerFactory;
import ar.edu.itba.pod.grupo9.query1.InfractionAgencyCountReducerFactory;
import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.Reducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("deprecation")
public class InfractionAgencyReducerTest {
    private Reducer<Integer, Integer> reducer;

    @BeforeEach
    void setUp() {
        InfractionAgencyCountReducerFactory factory = new InfractionAgencyCountReducerFactory();
        reducer = factory.newReducer(new Pair<>("INF001", "AGENCY1"));  // Sample key pair
    }

    @Test
    void testReduce_SumsValuesCorrectly() {
        // Act
        reducer.reduce(3);
        reducer.reduce(4);
        reducer.reduce(5);

        // Assert
        assertEquals(12, reducer.finalizeReduce());
    }
}
