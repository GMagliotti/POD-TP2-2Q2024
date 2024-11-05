package ar.edu.itba.pod.grupo9.query4;

import ar.edu.itba.pod.grupo9.model.Infraction;
import ar.edu.itba.pod.grupo9.model.InfractionSummary;
import com.hazelcast.mapreduce.Collator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SuppressWarnings("deprecation")
public class TopAmountInfractionDifferenceByAgencyCollator implements Collator<Map.Entry<String, InfractionSummary>, List<Map.Entry<String, InfractionSummary>>> {

    private final int n;
    private final Map<String, Infraction> codeToDescription;

    public TopAmountInfractionDifferenceByAgencyCollator(int n, Map<String, Infraction> codeToDescription) {
        this.n = n;
        this.codeToDescription = codeToDescription;
    }

    @Override
    public List<Map.Entry<String, InfractionSummary>> collate(Iterable<Map.Entry<String, InfractionSummary>> values) {
        return StreamSupport.stream(values.spliterator(), false)
                .map(entry -> {
                    String code = entry.getKey();
                    InfractionSummary summary = entry.getValue();
                    String description = codeToDescription.get(code).getDescription();
                    if (description == null) {
                        description = "";
                    }
                    return Map.entry(description, summary);
                })
                .sorted((e1, e2) -> {
                    int comparison = Double.compare(e2.getValue().getDifference(), e1.getValue().getDifference());
                    if (comparison == 0) {
                        return e1.getKey().compareTo(e2.getKey());
                    }
                    return comparison;
                })
                .limit(n)
                .collect(Collectors.toList());
    }

}
