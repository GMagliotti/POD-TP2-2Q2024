package ar.edu.itba.pod.grupo9.client.util.query;

import ar.edu.itba.pod.grupo9.client.util.City;
import ar.edu.itba.pod.grupo9.model.Infraction;
import ar.edu.itba.pod.grupo9.model.Pair;
import ar.edu.itba.pod.grupo9.model.Ticket;
import ar.edu.itba.pod.grupo9.query1.InfractionAgencyCountCollator;
import ar.edu.itba.pod.grupo9.query1.InfractionAgencyCountCombinerFactory;
import ar.edu.itba.pod.grupo9.query1.InfractionAgencyCountMapper;
import ar.edu.itba.pod.grupo9.query1.InfractionAgencyCountReducerFactory;
import ar.edu.itba.pod.grupo9.query3.RepeatOffenderCountCollator;
import ar.edu.itba.pod.grupo9.query3.RepeatOffenderCountCombinerFactory;
import ar.edu.itba.pod.grupo9.query3.RepeatOffenderCountMapper;
import ar.edu.itba.pod.grupo9.query3.RepeatOffenderCountReducerFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public enum QueryEngine {
    NYC {
        @Override
        public List<Map.Entry<Pair<String, String>, Integer>> runQuery1(HazelcastInstance hazelcastInstance) {
            return execQuery1(hazelcastInstance, City.NYC);
        }

        @Override
        public List<Map.Entry<String, Double>> runQuery3(HazelcastInstance hazelcastInstance, int n, LocalDate from, LocalDate to) {
            return execQuery3(hazelcastInstance, City.NYC, n, from, to);
        }

    },
    CHI {
        @Override
        public List<Map.Entry<Pair<String, String>, Integer>> runQuery1(HazelcastInstance hazelcastInstance) {
            return execQuery1(hazelcastInstance, City.CHI);
        }

        @Override
        public List<Map.Entry<String, Double>> runQuery3(HazelcastInstance hazelcastInstance, int n, LocalDate from, LocalDate to) {
            return execQuery3(hazelcastInstance, City.CHI, n, from, to);
        }
    };

    private static final Logger logger = LoggerFactory.getLogger(QueryEngine.class);

    private static List<Map.Entry<String, Double>> execQuery3(HazelcastInstance hazelcastInstance, City city, int n, LocalDate from, LocalDate to) {
        Properties prop = loadProperties();

        MultiMap<String, Ticket> tickets = hazelcastInstance.getMultiMap(prop.getProperty("hz.collection.tickets." + city.name().toLowerCase()));

        JobTracker jobTracker = hazelcastInstance.getJobTracker(prop.getProperty("hz.cluster.name"));
        KeyValueSource<String, Ticket> source = KeyValueSource.fromMultiMap(tickets);
        Job<String, Ticket> job = jobTracker.newJob(source);

        ICompletableFuture<List<Map.Entry<String, Double>>> future = job
                .mapper(new RepeatOffenderCountMapper(from, to))
                .combiner(new RepeatOffenderCountCombinerFactory())
                .reducer(new RepeatOffenderCountReducerFactory())
                .submit(new RepeatOffenderCountCollator(n));

        try {
            return future.get();
        } catch (Exception e) {
            logger.error("Error getting future result", e);
            System.exit(1);
        }
        return null;
    }

    private static List<Map.Entry<Pair<String, String>, Integer>> execQuery1(HazelcastInstance hazelcastInstance, City city) {
        Properties prop = loadProperties();

        MultiMap<String, Ticket> tickets = hazelcastInstance.getMultiMap(prop.getProperty("hz.collection.tickets." + city.name().toLowerCase()));
        IMap<String, Infraction> infractions = hazelcastInstance.getMap(prop.getProperty("hz.collection.infractions." + city.name().toLowerCase()));
        JobTracker jobTracker = hazelcastInstance.getJobTracker(prop.getProperty("hz.cluster.name"));
        KeyValueSource<String, Ticket> source = KeyValueSource.fromMultiMap(tickets);
        Job<String, Ticket> job = jobTracker.newJob(source);

        ICompletableFuture<List<Map.Entry<Pair<String, String>, Integer>>> future = job
                .mapper(new InfractionAgencyCountMapper())
                .combiner(new InfractionAgencyCountCombinerFactory())
                .reducer(new InfractionAgencyCountReducerFactory())
                .submit(new InfractionAgencyCountCollator(infractions));

        try {
            return future.get();
        } catch (Exception e) {
            logger.error("Error getting future result", e);
            System.exit(1);
        }
        return null;
    }

    private static Properties loadProperties() {
        Properties prop = new Properties();
        try (InputStream inputStream = QueryEngine.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new RuntimeException("Property file not found in the classpath");
            }
        } catch (IOException e) {
            logger.error("Error loading properties file", e);
            System.exit(1);
        }
        return prop;
    }

    public abstract List<Map.Entry<Pair<String, String>, Integer>> runQuery1(HazelcastInstance hazelcastInstance);

    public abstract List<Map.Entry<String, Double>> runQuery3(HazelcastInstance hazelcastInstance, int n, LocalDate from, LocalDate to);
}
