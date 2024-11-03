package ar.edu.itba.pod.grupo9.query2;

import ar.edu.itba.pod.grupo9.model.Pair;
import ar.edu.itba.pod.grupo9.model.Ticket;
import ar.edu.itba.pod.grupo9.model.YtdEarningsEntry;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class YtdEarningsMapper implements Mapper<String, Ticket, Pair<String, Pair<Integer, Integer>>, Double> {
    @Override
    public void map(String s, Ticket ticket, Context<Pair<String, Pair<Integer, Integer>>, Double> context) {
        LocalDate date = ticket.getIssueDate();
        String agency = ticket.getIssuingAgency();
        context.emit(new Pair<>(agency, new Pair<>(date.getYear(), date.getMonthValue())), ticket.getFineAmount());
    }
}
