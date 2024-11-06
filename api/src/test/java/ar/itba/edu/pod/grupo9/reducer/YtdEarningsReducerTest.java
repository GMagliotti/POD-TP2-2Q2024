package ar.itba.edu.pod.grupo9.reducer;

import ar.edu.itba.pod.grupo9.model.Pair;
import ar.edu.itba.pod.grupo9.query2.YtdEarningsReducerFactory;
import com.hazelcast.mapreduce.Reducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("deprecation")
public class YtdEarningsReducerTest {
    private Reducer<Double, Double> reducer;

    @BeforeEach
    void setUp() {
        YtdEarningsReducerFactory factory = new YtdEarningsReducerFactory();
        reducer = factory.newReducer(new Pair<>("AGENCY1", new Pair<>(7, 7)));  // Sample key pair
    }

    @Test
    void testCombine_SumsValuesCorrectly() {

        // Act
        reducer.reduce(3.0);
        reducer.reduce(4.0);
        reducer.reduce(5.0);

        // Assert
        assertEquals(12.0, reducer.finalizeReduce(), 0.00001);
    }
}

