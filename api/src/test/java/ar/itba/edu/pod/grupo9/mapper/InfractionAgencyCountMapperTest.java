package ar.itba.edu.pod.grupo9.mapper;

import ar.edu.itba.pod.grupo9.model.Infraction;
import ar.edu.itba.pod.grupo9.model.Pair;
import ar.edu.itba.pod.grupo9.model.Ticket;
import ar.edu.itba.pod.grupo9.query1.InfractionAgencyCountMapper;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.mapreduce.Context;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Mockito;

import java.io.Closeable;
import java.time.LocalDate;
import java.util.HashMap;
import static org.mockito.Mockito.*;

class InfractionAgencyCountMapperTest {

    @Mock
    private ReplicatedMap<String, Infraction> infractions;

    @Mock
    private ReplicatedMap<String, Integer> agencies;

    @Mock
    @SuppressWarnings("deprecation")
    private Context<Pair<String, String>, Integer> context;

    private InfractionAgencyCountMapper mapper;
    private AutoCloseable closeable;

    private final String LICENSE_PLATE = "LCPLATE";
    private final String INFRACTION_CODE = "INF001";
    private final String NX_INFRACTION_CODE = "INF002";

    private final Double FINE_AMOUNT = 100.0;
    private final String AGENCY = "AGENCY1";
    private final String NX_AGENCY = "AGENCY2";
    private final LocalDate ISSUE_DATE = LocalDate.of(2007, 7, 11);
    private final String COUNTY_NAME = "SAN PEDRO"; // cheeky

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        mapper = new InfractionAgencyCountMapper(infractions, agencies);
    }

    @Test
    void testMap_RelevantEntry() {
        // Arrange
        Ticket ticket = new Ticket(LICENSE_PLATE, INFRACTION_CODE, FINE_AMOUNT, AGENCY, ISSUE_DATE, COUNTY_NAME); // Example values
        when(infractions.containsKey(INFRACTION_CODE)).thenReturn(true);
        when(agencies.containsKey(AGENCY)).thenReturn(true);

        // Act
        mapper.map("key", ticket, context);

        // Assert
        verify(context).emit(new Pair<>(INFRACTION_CODE, AGENCY), 1);
    }

    @Test
    void testMap_IrrelevantEntry_NxInfraction_NoEmit() {
        // Arrange
        Ticket ticket = new Ticket(LICENSE_PLATE, NX_INFRACTION_CODE, FINE_AMOUNT, AGENCY, ISSUE_DATE, COUNTY_NAME); // Example values
        when(infractions.containsKey(NX_INFRACTION_CODE)).thenReturn(false);
        when(agencies.containsKey(AGENCY)).thenReturn(true);

        // Act
        mapper.map("key", ticket, context);

        // Assert
        verify(context, never()).emit(any(), any());
    }

    @Test
    void testMap_IrrelevantEntry_NxAgency_NoEmit() {
        // Arrange
        Ticket ticket = new Ticket(LICENSE_PLATE, INFRACTION_CODE, FINE_AMOUNT, NX_AGENCY, ISSUE_DATE, COUNTY_NAME); // Example values
        when(infractions.containsKey(INFRACTION_CODE)).thenReturn(true);
        when(agencies.containsKey(NX_AGENCY)).thenReturn(false);

        // Act
        mapper.map("key", ticket, context);

        // Assert
        verify(context, never()).emit(any(), any());
    }

    @Test
    void testMap_IrrelevantEntry_NxBoth_NoEmit() {
        // Arrange
        Ticket ticket = new Ticket(LICENSE_PLATE, NX_INFRACTION_CODE, FINE_AMOUNT, NX_AGENCY, ISSUE_DATE, COUNTY_NAME); // Example values
        when(infractions.containsKey(NX_INFRACTION_CODE)).thenReturn(false);
        when(agencies.containsKey(NX_AGENCY)).thenReturn(false);

        // Act
        mapper.map("key", ticket, context);

        // Assert
        verify(context, never()).emit(any(), any());
    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }
}
