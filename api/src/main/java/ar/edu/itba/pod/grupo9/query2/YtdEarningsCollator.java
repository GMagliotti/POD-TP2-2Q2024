package ar.edu.itba.pod.grupo9.query2;

import ar.edu.itba.pod.grupo9.model.Pair;
import com.hazelcast.mapreduce.Collator;

import java.util.*;
import java.util.stream.StreamSupport;

@SuppressWarnings("deprecation")

public class YtdEarningsCollator implements Collator<
        Map.Entry<Pair<String, Pair<Integer, Integer>>, Double>,
        List<Map.Entry<Pair<String, Pair<Integer, Integer>>, Double>>
        > {
    @Override
    public List<Map.Entry<Pair<String, Pair<Integer, Integer>>, Double>> collate(Iterable<Map.Entry<Pair<String, Pair<Integer, Integer>>, Double>> values) {
        List<Map.Entry<Pair<String, Pair<Integer, Integer>>, Double>> orderedEarnings = StreamSupport.stream(values.spliterator(), false)
                .sorted(Comparator.comparing((Map.Entry<Pair<String, Pair<Integer, Integer>>, Double> e) -> e.getKey().getFirst())
                        .thenComparing(e -> e.getKey().getSecond().getFirst())
                        .thenComparing(e -> e.getKey().getSecond().getSecond()))
                .toList();

        List<Map.Entry<Pair<String, Pair<Integer, Integer>>, Double>> retList = new ArrayList<>(orderedEarnings.size());

        String previousAgency = null;
        int previousYear = 0;
        double ytdEarnings = 0;
        for (Map.Entry<Pair<String, Pair<Integer, Integer>>, Double> entry : orderedEarnings) {
            if (isNewYtd(entry.getKey(), previousAgency, previousYear)) {
                ytdEarnings = 0;
            }
            ytdEarnings += entry.getValue();
            retList.add(new AbstractMap.SimpleEntry<>(entry.getKey(), ytdEarnings));
            previousAgency = entry.getKey().getFirst();
            previousYear = entry.getKey().getSecond().getFirst();
        }

        return retList;
    }

    private boolean isNewYtd(Pair<String, Pair<Integer, Integer>> key, String agency, int year) {
        return !key.getFirst().equals(agency) || key.getSecond().getFirst() != year;
    }
}
