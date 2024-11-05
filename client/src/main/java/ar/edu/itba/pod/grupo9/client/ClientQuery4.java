package ar.edu.itba.pod.grupo9.client;

import ar.edu.itba.pod.grupo9.client.util.City;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ClientQuery4 extends ClientQuery {

    public ClientQuery4() throws IOException, InterruptedException, ExecutionException {
    }

    public static void main(String[] args) {
        String n = System.getProperty("n");
        String agency = System.getProperty("agency");

        try (ClientQuery4 client = new ClientQuery4()) {
            client.executeQuery("query4",
                    (hazelcastInstance) -> {
                        City city = City.getCity(client.cityStr);
                        return city.getQueryEngine().runQuery4(hazelcastInstance, Integer.parseInt(n), agency);
                    },
                    (hazelcastInstance) -> {
                        City city = City.getCity(client.cityStr);
                        city.getQueryLoader().loadQuery4(hazelcastInstance, client.inputPath);
                    }
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected <K, V> void writeResults(List<Map.Entry<K, V>> resultList, String outputPath) {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"Infraction", "Max", "Min", "Diff"});

        for (Map.Entry<K, V> entry : resultList) {
            rows.add(new String[]{entry.getKey().toString(), entry.getValue().toString()});
        }

        writeToCSV(rows, outputPath);
    }
}
