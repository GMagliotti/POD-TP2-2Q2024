package ar.edu.itba.pod.grupo9.query2;

import ar.edu.itba.pod.grupo9.model.Pair;
import ar.edu.itba.pod.grupo9.model.Ticket;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.time.LocalDate;

@SuppressWarnings("deprecation")
public class YtdEarningsMapper implements Mapper<String, Ticket, Pair<String, Pair<Integer, Integer>>, Double> {
    private transient ReplicatedMap<String, Integer> validAgencies;

    public YtdEarningsMapper() {
        // required by hazelcast
    }

    public YtdEarningsMapper(ReplicatedMap<String, Integer> agencies) {
        this.validAgencies = agencies;
    }

    @Override
    public void map(String s, Ticket ticket, Context<Pair<String, Pair<Integer, Integer>>, Double> context) {
        LocalDate date = ticket.getIssueDate();
        String agency = ticket.getIssuingAgency();
        if (isRelevantEntry(ticket)) {
            context.emit(new Pair<>(agency, new Pair<>(date.getYear(), date.getMonthValue())), ticket.getFineAmount());
        }

    }

    private boolean isRelevantEntry(Ticket ticket) {
        return ticket.getFineAmount() != 0 &&
                (validAgencies == null || validAgencies.containsKey(ticket.getIssuingAgency()));
    }
}
