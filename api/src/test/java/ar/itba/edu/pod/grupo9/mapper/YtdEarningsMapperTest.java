//package ar.itba.edu.pod.grupo9.mapper;
//
//import ar.edu.itba.pod.grupo9.model.Infraction;
//import ar.edu.itba.pod.grupo9.model.Pair;
//import ar.edu.itba.pod.grupo9.model.Ticket;
//import ar.edu.itba.pod.grupo9.query1.InfractionAgencyCountMapper;
//import ar.edu.itba.pod.grupo9.query2.YtdEarningsMapper;
//import com.hazelcast.core.ReplicatedMap;
//import com.hazelcast.mapreduce.Context;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.time.LocalDate;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.mockito.Mockito.never;
//
//public class YtdEarningsMapperTest {
//
//    @Mock
//    private ReplicatedMap<String, Integer> agencies;
//
//    @Mock
//    @SuppressWarnings("deprecation")
//    private Context<Pair<String, Pair<Integer, Integer>>, Double> context;
//
//    private YtdEarningsMapper mapper;
//    private AutoCloseable closeable;
//
//    private final String LICENSE_PLATE = "LCPLATE";
//    private final String INFRACTION_CODE = "INF001";
//    private final String NX_INFRACTION_CODE = "INF002";
//
//    private final Double FINE_AMOUNT = 100.0;
//    private final String AGENCY = "AGENCY1";
//    private final String NX_AGENCY = "AGENCY2";
//    private final LocalDate ISSUE_DATE = LocalDate.of(2007, 7, 11);
//    private final String COUNTY_NAME = "SAN PEDRO"; // cheeky
//
//    @BeforeEach
//    void setUp() {
//        closeable = MockitoAnnotations.openMocks(this);
//        mapper = new YtdEarningsMapper(agencies);
//    }
//
//    @Test
//    void testMap_RelevantEntry() {
//        // Arrange
//        Ticket ticket = new Ticket(LICENSE_PLATE, INFRACTION_CODE, FINE_AMOUNT, AGENCY, ISSUE_DATE, COUNTY_NAME); // Example values
//        when(agencies.containsKey(AGENCY)).thenReturn(true);
//
//
//        // Act
//        mapper.map("key", ticket, context);
//
//        // Assert
//        final Pair<String, Pair<Integer, Integer>> EXPECTED_KEY = new Pair<>(AGENCY,
//                new Pair<>(ISSUE_DATE.getYear(), ISSUE_DATE.getMonthValue()));
//        verify(context).emit(EXPECTED_KEY, FINE_AMOUNT);
//    }
//
//    @Test
//    void testMap_IrrelevantEntry_NxAgency_NoEmit() {
//        // Arrange
//        Ticket ticket = new Ticket(LICENSE_PLATE, INFRACTION_CODE, FINE_AMOUNT, NX_AGENCY, ISSUE_DATE, COUNTY_NAME); // Example values
//        when(agencies.containsKey(NX_AGENCY)).thenReturn(false);
//
//        // Act
//        mapper.map("key", ticket, context);
//
//        // Assert
//        verify(context, never()).emit(any(), any());
//    }
//
//    @AfterEach
//    void tearDown() throws Exception {
//        if (closeable != null) {
//            closeable.close();
//        }
//    }
//}
