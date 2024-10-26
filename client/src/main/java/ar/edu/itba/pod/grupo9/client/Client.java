package ar.edu.itba.pod.grupo9.client;

import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MultiMap;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;

public class Client {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws InterruptedException, IOException, ExecutionException {
        logger.info("hz-bike-rentals Client Starting ...");

        try {
            // Group Config
            GroupConfig groupConfig = new GroupConfig().setName("l12345").setPassword("l12345-pass");

            // Client Network Config
            ClientNetworkConfig clientNetworkConfig = new ClientNetworkConfig();
            clientNetworkConfig.addAddress("127.0.0.1");

            // Client Config
            ClientConfig clientConfig = new ClientConfig().setGroupConfig(groupConfig).setNetworkConfig(clientNetworkConfig);

            // Node Client
            HazelcastInstance hazelcastInstance = HazelcastClient.newHazelcastClient(clientConfig);

            // Key Value Source
            IMap<String, String> wordsIMap = hazelcastInstance.getMap("words");
            KeyValueSource<String, String> wordsKeyValueSource = KeyValueSource.fromMap(wordsIMap);

            // Job Tracker
            JobTracker jobTracker = hazelcastInstance.getJobTracker("word-count");

            // Text File Reading and Key Value Source Loading
            final AtomicInteger auxKey = new AtomicInteger();
            try (Stream<String> lines = Files.lines(Paths.get(args[0]), StandardCharsets.UTF_8)) {
                lines.forEach(line -> wordsIMap.put(String.valueOf(auxKey.getAndIncrement()), line));
            }

            // MapReduce Job
            Job<String, String> job = jobTracker.newJob(wordsKeyValueSource);
            ICompletableFuture<List<Map.Entry<String,Long>>> future = null;
                    //job
                    //.mapper(new TokenizerMapper())
                    //.keyPredicate(new First100LinesKeyPredicate())
                    //.combiner(new WordCountCombinerFactory())
                    //.reducer(new WordCountReducerFactory())
                    //.submit(new WordCountCollator());

            // Wait and retrieve the result
            Map<String, Long> result = future.get().stream().collect(
                    Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            // Sort entries ascending by count and print
            result.entrySet().stream()
                    //.sorted((Map.Entry.<String, Long>comparingByValue()))
                    .forEach(System.out::println);
        } finally {
            HazelcastClient.shutdownAll();
        }
    }
}
