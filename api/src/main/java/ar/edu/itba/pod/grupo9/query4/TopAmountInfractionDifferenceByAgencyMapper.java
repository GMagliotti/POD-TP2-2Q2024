package ar.edu.itba.pod.grupo9.query4;

import ar.edu.itba.pod.grupo9.model.Infraction;
import ar.edu.itba.pod.grupo9.model.InfractionSummary;
import ar.edu.itba.pod.grupo9.model.Ticket;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;


@SuppressWarnings("deprecation")
public class TopAmountInfractionDifferenceByAgencyMapper implements Mapper<String, Ticket, String, InfractionSummary> {

    private final String agency;

    public TopAmountInfractionDifferenceByAgencyMapper(final String agency) {
        this.agency = agency.replace("_", " ");
    }

    @Override
    public void map(String s, Ticket ticket, Context<String, InfractionSummary> context) {
        if (ticket.getIssuingAgency().equals(agency)) {
            double amount = ticket.getFineAmount();
            context.emit(ticket.getCode(), new InfractionSummary(amount, amount, 0.0));
        }
    }
}

