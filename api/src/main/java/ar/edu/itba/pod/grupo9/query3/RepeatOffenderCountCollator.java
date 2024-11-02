package ar.edu.itba.pod.grupo9.query3;

import ar.edu.itba.pod.grupo9.model.Pair;
import com.hazelcast.mapreduce.Collator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Collator for the query 3
 * The logic goes as follows:
 * - For the given pair of license plate and county name, we have the number of times the driver has been fined.
 * - We want to return the ratio of the number of repeat offenders for each county.
 * - We consider a driver to be a repeat offender if they have been fined more than n times.
 * - We calculate the total number of fines for each county.
 * - We calculate the ratio of repeat offenders for each county, as the number of repeat offenders divided by the total number offenders.
 * - We sort the list by the ratio in descending order. Then we sort alphabetically by the county name.
 */
@SuppressWarnings("deprecation")
public class RepeatOffenderCountCollator implements Collator<Map.Entry<Pair<String,String>, Integer>, List<Map.Entry<String,Double>>> {
    private static final Logger logger = LoggerFactory.getLogger(RepeatOffenderCountCollator.class);

    private final int n;

    public RepeatOffenderCountCollator(int n){
        logger.info("Creating RepeatOffenderCountCollator with n = {}", n);
        this.n = n;
    }

    @Override
    public List<Map.Entry<String, Double>> collate(Iterable<Map.Entry<Pair<String, String>, Integer>> values) {
        logger.info("Collating RepeatOffenderCountCollator");
        Map<String, Integer> countyFines = StreamSupport.stream(values.spliterator(), false)
                .collect(Collectors.groupingBy(e -> e.getKey().getSecond(), Collectors.summingInt(Map.Entry::getValue)));
        //logger.info("County fines: {}", countyFines);
        // create empty repeatOffenders map
        Map<String, Integer> repeatOffenders = new java.util.HashMap<>(Map.of());
        for (Map.Entry<String, Integer> entry : countyFines.entrySet()) {
            //logger.info("County: {}, fines: {}", entry.getKey(), entry.getValue());
            // create a Map<plate, count> for current county
            Map<String, Integer> plateCount = StreamSupport.stream(values.spliterator(), false)
                    .filter(e -> e.getKey().getSecond().equals(entry.getKey()))
                    .collect(Collectors.groupingBy(e -> e.getKey().getFirst(), Collectors.summingInt(Map.Entry::getValue)));
            //logger.info("Plate count: {}", plateCount);
            // we increase by 1 the repeatOffenders count for each plate that has been fined more than n times
            plateCount.forEach((plate, count) -> {
                if (count >= n) {
                    repeatOffenders.put(entry.getKey(), repeatOffenders.getOrDefault(entry.getKey(), 0) + 1);
                }
            });
            //logger.info("Repeat offenders: {}", repeatOffenders);
        }
        //logger.info("Repeat offenders: {}", repeatOffenders);
        return countyFines.entrySet().stream()
                .map(e -> {
                    String county = e.getKey();
                    double totalFines = e.getValue();
                    double repeatOffenderCount = repeatOffenders.getOrDefault(county, 0);
                    double ratio = repeatOffenderCount / totalFines;
                    // log with 10 decimal places
                    logger.info("County: {}, total fines: {}, repeat offenders: {}, ratio: {}", county, totalFines, repeatOffenderCount, ratio);
                    ratio = Math.floor(ratio * 100) / 100; // truncate to 2 decimal places
                    logger.info("Final ratio for county {}: {}", county, ratio);
                    return Map.entry(county, ratio);
                })
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed().thenComparing(Map.Entry::getKey))
                .collect(Collectors.toList());
    }
}
