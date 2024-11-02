package ar.edu.itba.pod.grupo9.query1;

import ar.edu.itba.pod.grupo9.model.Infraction;
import ar.edu.itba.pod.grupo9.model.Pair;
import com.hazelcast.mapreduce.Collator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SuppressWarnings("deprecation")
public class InfractionAgencyCountCollator implements Collator<Map.Entry<Pair<String, String>, Integer>, List<Map.Entry<Pair<String, String>, Integer>>> {

    private final Map<String, Infraction> codeToDescription;

    public InfractionAgencyCountCollator(Map<String, Infraction> codeToDescription) {
        this.codeToDescription = codeToDescription;
    }

    @Override
    public List<Map.Entry<Pair<String, String>, Integer>> collate(Iterable<Map.Entry<Pair<String, String>, Integer>> values) {
        return StreamSupport.stream(values.spliterator(), false)
                .map(entry -> {
                    Pair<String, String> key = entry.getKey();
                    String code = key.getFirst();
                    String agency = key.getSecond();
                    String description = codeToDescription.get(code).getDescription();
                    if (description == null) {
                        description = "";
                    }
                    return Map.entry(new Pair<>(description, agency), entry.getValue());
                })
                // sort descending by Integer value, then alphabetically by pair first element of the key, and then alphabetically by pair second element of the key
                .sorted(Map.Entry.<Pair<String, String>, Integer>comparingByValue().reversed()
                        .thenComparing(e -> e.getKey().getFirst())
                        .thenComparing(e -> e.getKey().getSecond())
                )
                .collect(Collectors.toList());
    }

}
