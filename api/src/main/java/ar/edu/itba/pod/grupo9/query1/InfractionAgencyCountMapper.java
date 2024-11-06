package ar.edu.itba.pod.grupo9.query1;

import ar.edu.itba.pod.grupo9.model.Pair;
import ar.edu.itba.pod.grupo9.model.Ticket;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

@SuppressWarnings("deprecation")
public class InfractionAgencyCountMapper implements Mapper<String, Ticket, Pair<String, String>, Integer> {

    @Override
    public void map(String s, Ticket ticket, Context<Pair<String, String>, Integer> context) {
        context.emit(new Pair<>(ticket.getCode(), ticket.getIssuingAgency()), 1);
    }
}
