package ar.edu.itba.pod.grupo9.client;

import ar.edu.itba.pod.grupo9.client.util.City;
import ar.edu.itba.pod.grupo9.model.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ClientQuery2 extends ClientQuery {
    private static final Logger logger = LoggerFactory.getLogger(ClientQuery3.class);

    public ClientQuery2() throws IOException, InterruptedException, ExecutionException {
    }

    public static void main(String[] args) {
        try (ClientQuery2 client = new ClientQuery2()) {
            client.executeQuery("query2",
                    (hazelcastInstance) -> {
                        City city = City.getCity(client.cityStr);
                        return city.getQueryEngine().runQuery2(hazelcastInstance);
                    },
                    (hazelcastInstance) -> {
                        City city = City.getCity(client.cityStr);
                        city.getQueryLoader().loadQuery1(hazelcastInstance, client.inputPath);
                    }
            );
            logger.info("Client execution completed.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    // Write results
    @Override
    protected <K, V> void writeResults(List<Map.Entry<K, V>> resultList, String outputPath) {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"Agency", "Year", "Month", "Total earnings"});

        for (Map.Entry<K, V> entry : resultList) {
            @SuppressWarnings("unchecked")
            Map.Entry<Pair<String, Pair<Integer, Integer>>, Double> e = (Map.Entry<Pair<String, Pair<Integer, Integer>>, Double>) entry;
            rows.add(new String[]{e.getKey().getFirst(),
                    e.getKey().getSecond().getFirst().toString(),
                    e.getKey().getSecond().getSecond().toString(),
                    e.getValue().toString()});
        }

        writeToCSV(rows, outputPath);
    }

}
