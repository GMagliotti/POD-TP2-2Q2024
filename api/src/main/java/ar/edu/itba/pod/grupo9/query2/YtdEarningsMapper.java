package ar.edu.itba.pod.grupo9.query2;

import ar.edu.itba.pod.grupo9.model.Pair;
import ar.edu.itba.pod.grupo9.model.Ticket;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.time.LocalDate;

@SuppressWarnings("deprecation")
public class YtdEarningsMapper implements Mapper<String, Ticket, Pair<String, Pair<Integer, Integer>>, Double>,
        HazelcastInstanceAware {
    private String agenciesMapName;

    private transient HazelcastInstance hz;

    public YtdEarningsMapper(String agenciesMapName) {
        this.agenciesMapName = agenciesMapName;
    }

    public YtdEarningsMapper(String agenciesMapName, HazelcastInstance hz) {
        this.agenciesMapName = agenciesMapName;
        this.hz = hz;
    }

    @Override
    public void map(String s, Ticket ticket, Context<Pair<String, Pair<Integer, Integer>>, Double> context) {
        LocalDate date = ticket.getIssueDate();
        String agency = ticket.getIssuingAgency();
        if (isRelevantEntry(ticket)) {
            context.emit(new Pair<>(agency, new Pair<>(date.getYear(), date.getMonthValue())), ticket.getFineAmount());
        }

    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hz = hazelcastInstance;
    }

    private boolean isRelevantEntry(Ticket ticket) {
        return ticket.getFineAmount() != 0 &&
                hz.<String, Integer>getReplicatedMap(agenciesMapName).containsKey(ticket.getIssuingAgency());
    }
}
