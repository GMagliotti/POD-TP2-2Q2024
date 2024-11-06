package ar.itba.edu.pod.grupo9.collator;

import ar.edu.itba.pod.grupo9.model.Infraction;
import ar.edu.itba.pod.grupo9.model.Pair;
import ar.edu.itba.pod.grupo9.query1.InfractionAgencyCountCollator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InfractionAgencyCountCollatorTest {

    private InfractionAgencyCountCollator collator;
    private Map<String, Infraction> codeToDescription;


    @BeforeEach
    void setUp() {
        // Set up a mapping for code-to-description
        codeToDescription = new HashMap<>();
        codeToDescription.put("INF001", new Infraction("INF001", "Speeding"));
        codeToDescription.put("INF002", new Infraction("INF002", "Parking Violation"));

        // Initialize the collator with the codeToDescription map
        collator = new InfractionAgencyCountCollator(codeToDescription);
    }

    @Test
    void testCollate_SortsAndMapsDescriptionsCorrectly() {
        // Arrange
        List<Map.Entry<Pair<String, String>, Integer>> input = Arrays.asList(
                Map.entry(new Pair<>("INF001", "AgencyB"), 10),
                Map.entry(new Pair<>("INF001", "AgencyA"), 10),
                Map.entry(new Pair<>("INF002", "AgencyB"), 15),
                Map.entry(new Pair<>("INF001", "AgencyC"), 15),
                Map.entry(new Pair<>("INF002", "AgencyA"), 5)
        );

        // Act
        List<Map.Entry<Pair<String, String>, Integer>> result = collator.collate(input);

        // Assert
        assertEquals(5, result.size());

        // Expected ordering:
        // 1. Parking Violation, AgencyB (15)
        // 2. Speeding, AgencyC (15)
        // 3. Speeding, AgencyA (10)
        // 4. Parking Violation, AgencyA (5)

        assertEquals("Parking Violation", result.get(0).getKey().getFirst());
        assertEquals("AgencyB", result.get(0).getKey().getSecond());
        assertEquals(15, result.get(0).getValue());

        assertEquals("Speeding", result.get(1).getKey().getFirst());
        assertEquals("AgencyC", result.get(1).getKey().getSecond());
        assertEquals(15, result.get(1).getValue());

        assertEquals("Speeding", result.get(2).getKey().getFirst());
        assertEquals("AgencyA", result.get(2).getKey().getSecond());
        assertEquals(10, result.get(2).getValue());

        assertEquals("Speeding", result.get(3).getKey().getFirst());
        assertEquals("AgencyB", result.get(3).getKey().getSecond());
        assertEquals(10, result.get(3).getValue());

        assertEquals("Parking Violation", result.get(4).getKey().getFirst());
        assertEquals("AgencyA", result.get(4).getKey().getSecond());
        assertEquals(5, result.get(4).getValue());
    }

    @Test
    void testCollate_DefaultsToEmptyDescriptionWhenNoMappingExists() {
        // Arrange
        List<Map.Entry<Pair<String, String>, Integer>> input = Collections.singletonList(
                Map.entry(new Pair<>("UNKNOWN_CODE", "AgencyX"), 20)
        );

        // Act
        List<Map.Entry<Pair<String, String>, Integer>> result = collator.collate(input);

        // Assert
        assertEquals(1, result.size());
        assertEquals("", result.get(0).getKey().getFirst()); // Should default to empty string
        assertEquals("AgencyX", result.get(0).getKey().getSecond());
        assertEquals(20, result.get(0).getValue());
    }
}

