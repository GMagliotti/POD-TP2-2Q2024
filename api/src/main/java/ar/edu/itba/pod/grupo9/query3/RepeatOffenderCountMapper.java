package ar.edu.itba.pod.grupo9.query3;

import ar.edu.itba.pod.grupo9.model.Pair;
import ar.edu.itba.pod.grupo9.model.Ticket;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.time.LocalDate;

/**
 * This class is responsible for mapping the input data to a key-value pair.
 * The logic goes as follows:
 * - Only consider tickets issued between the given dates.
 * - Emit a key-value pair with the ticket's license plate and the ticket's county name.
 */
@SuppressWarnings("deprecation")
public class RepeatOffenderCountMapper implements Mapper<String, Ticket, Pair<String, String>, Integer> {

    private final LocalDate startDate;
    private final LocalDate endDate;

    public RepeatOffenderCountMapper(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public void map(String s, Ticket ticket, Context<Pair<String, String>, Integer> context) {
        // only consider tickets issued between the given dates, inclusive
        if (ticket.getIssueDate().compareTo(startDate) >= 0 && ticket.getIssueDate().compareTo(endDate) <= 0) {
            context.emit(new Pair<>(ticket.getPlate(), ticket.getCountyName()), 1);
        }
    }

}
