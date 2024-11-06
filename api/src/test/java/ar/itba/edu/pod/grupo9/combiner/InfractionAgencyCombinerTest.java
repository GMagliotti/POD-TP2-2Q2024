package ar.itba.edu.pod.grupo9.combiner;

import ar.edu.itba.pod.grupo9.model.Pair;
import ar.edu.itba.pod.grupo9.query1.InfractionAgencyCountCombinerFactory;
import com.hazelcast.mapreduce.Combiner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("deprecation")
public class InfractionAgencyCombinerTest {
    private Combiner<Integer, Integer> combiner;

    @BeforeEach
    void setUp() {
        InfractionAgencyCountCombinerFactory factory = new InfractionAgencyCountCombinerFactory();
        combiner = factory.newCombiner(new Pair<>("INF001", "AGENCY1"));  // Sample key pair
    }

    @Test
    void testCombine_SumsValuesCorrectly() {
        // Act
        combiner.combine(3);
        combiner.combine(4);
        combiner.combine(5);

        // Assert
        assertEquals(12, combiner.finalizeChunk());
    }

    @Test
    void testReset_ClearsCurrentSum() {
        // Act
        combiner.combine(10);
        combiner.reset();
        combiner.combine(15);

        // Assert
        assertEquals(15, combiner.finalizeChunk());
    }
}
