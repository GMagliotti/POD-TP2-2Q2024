package ar.edu.itba.pod.grupo9.query1;

import ar.edu.itba.pod.grupo9.model.Infraction;
import ar.edu.itba.pod.grupo9.model.Pair;
import ar.edu.itba.pod.grupo9.model.Ticket;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

@SuppressWarnings("deprecation")
public class InfractionAgencyCountMapper implements Mapper<String, Ticket, Pair<String, String>, Integer>,
        HazelcastInstanceAware {

    private transient HazelcastInstance hz;
    private final String infractionsMapName;
    private final String agenciesMapName;

    public InfractionAgencyCountMapper(String infractionsMapName, String agenciesMapName) {
        this.infractionsMapName = infractionsMapName;
        this.agenciesMapName = agenciesMapName;
    }

    @Override
    public void map(String s, Ticket ticket, Context<Pair<String, String>, Integer> context) {
        if(isRelevantEntry(ticket)) {
            context.emit(new Pair<>(ticket.getCode(), ticket.getIssuingAgency()), 1);
        }
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hz = hazelcastInstance;
    }

    private boolean isRelevantEntry(Ticket ticket) {
        return hz.<String, Infraction>getReplicatedMap(infractionsMapName).containsKey(ticket.getCode()) &&
                hz.<String, Integer>getReplicatedMap(agenciesMapName).containsKey(ticket.getIssuingAgency());
    }

}
