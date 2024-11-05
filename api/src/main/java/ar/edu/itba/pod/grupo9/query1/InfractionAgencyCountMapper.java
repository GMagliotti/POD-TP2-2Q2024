package ar.edu.itba.pod.grupo9.query1;

import ar.edu.itba.pod.grupo9.model.Infraction;
import ar.edu.itba.pod.grupo9.model.Pair;
import ar.edu.itba.pod.grupo9.model.Ticket;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

@SuppressWarnings("deprecation")
public class InfractionAgencyCountMapper implements Mapper<String, Ticket, Pair<String, String>, Integer> {

    private transient ReplicatedMap<String, Infraction> validInfractions;

    public InfractionAgencyCountMapper() {
        // required by hazelcast
    }

    public InfractionAgencyCountMapper(ReplicatedMap<String, Infraction> infractions) {
        this.validInfractions = infractions;
    }

    @Override
    public void map(String s, Ticket ticket, Context<Pair<String, String>, Integer> context) {
        if(isRelevantEntry(ticket)) {
            context.emit(new Pair<>(ticket.getCode(), ticket.getIssuingAgency()), 1);
        }
    }

    private boolean isRelevantEntry(Ticket ticket) {
        return validInfractions == null || validInfractions.containsKey(ticket.getCode());
    }

}
