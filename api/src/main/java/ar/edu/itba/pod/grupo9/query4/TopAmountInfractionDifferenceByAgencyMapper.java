package ar.edu.itba.pod.grupo9.query4;

import ar.edu.itba.pod.grupo9.model.Infraction;
import ar.edu.itba.pod.grupo9.model.InfractionSummary;
import ar.edu.itba.pod.grupo9.model.Ticket;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;


@SuppressWarnings("deprecation")
public class TopAmountInfractionDifferenceByAgencyMapper implements Mapper<String, Ticket, String, InfractionSummary>,
        HazelcastInstanceAware {
    private final String agency;
    private final String infractionsMapName;
    private transient HazelcastInstance hz;

    public TopAmountInfractionDifferenceByAgencyMapper(final String agency, final String infractionsMapName) {
        this.infractionsMapName = infractionsMapName;
        this.agency = agency.replace("_", " ");
    }

    @Override
    public void map(String s, Ticket ticket, Context<String, InfractionSummary> context) {
        if (ticket.getIssuingAgency().equals(agency) && (isRelevantEntry(ticket))) {
            double amount = ticket.getFineAmount();
            context.emit(ticket.getCode(), new InfractionSummary(amount, amount, 0.0));
        }
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hz) {
        this.hz = hz;
    }

    private boolean isRelevantEntry(Ticket ticket) {
        return hz.<String, Infraction>getReplicatedMap(infractionsMapName).containsKey(ticket.getCode());
    }
}

