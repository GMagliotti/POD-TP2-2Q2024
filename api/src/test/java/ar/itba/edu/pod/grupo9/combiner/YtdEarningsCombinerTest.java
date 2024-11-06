package ar.itba.edu.pod.grupo9.combiner;

import ar.edu.itba.pod.grupo9.model.Pair;
import ar.edu.itba.pod.grupo9.query1.InfractionAgencyCountCombinerFactory;
import ar.edu.itba.pod.grupo9.query2.YtdEarningsCombinerFactory;
import com.hazelcast.mapreduce.Combiner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("deprecation")
public class YtdEarningsCombinerTest {
    private Combiner<Double, Double> combiner;

    @BeforeEach
    void setUp() {
        YtdEarningsCombinerFactory factory = new YtdEarningsCombinerFactory();
        combiner = factory.newCombiner(new Pair<>("AGENCY1", new Pair<>(7, 7)));  // Sample key pair
    }

    @Test
    void testCombine_SumsValuesCorrectly() {

        // Act
        combiner.combine(3.0);
        combiner.combine(4.0);
        combiner.combine(5.0);

        // Assert
        assertEquals(12.0, combiner.finalizeChunk(), 0.00001);
    }

    @Test
    void testReset_ClearsCurrentSum() {
        // Act
        combiner.combine(10.0);
        combiner.reset();
        combiner.combine(15.0);

        // Assert
        assertEquals(15.0, combiner.finalizeChunk());
    }
}

