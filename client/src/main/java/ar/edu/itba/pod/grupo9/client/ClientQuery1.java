package ar.edu.itba.pod.grupo9.client;

import ar.edu.itba.pod.grupo9.client.util.City;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class ClientQuery1 extends ClientQuery{
    private static final Logger logger = LoggerFactory.getLogger(ClientQuery1.class);

    public ClientQuery1() throws IOException, InterruptedException, ExecutionException {
     }

    public static void main(String[] args) {
        try (ClientQuery1 client = new ClientQuery1()) {
            client.executeQuery("query1",
                    (hazelcastInstance) -> {
                        City city = City.getCity(client.cityStr);
                        return city.getQueryEngine().runQuery1(hazelcastInstance);
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
        rows.add(new String[]{"Infraction", "Agency", "Tickets"});

        for (Map.Entry<K, V> entry : resultList) {
            rows.add(new String[]{entry.getKey().toString(), entry.getValue().toString()});
        }

        writeToCSV(rows, outputPath);
    }

}
