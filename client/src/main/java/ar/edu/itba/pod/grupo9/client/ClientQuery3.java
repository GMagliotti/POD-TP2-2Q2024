package ar.edu.itba.pod.grupo9.client;

import ar.edu.itba.pod.grupo9.client.util.parser.ArgParser;
import ar.edu.itba.pod.grupo9.client.util.City;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ClientQuery3 extends ClientQuery{
    private static final Logger logger = LoggerFactory.getLogger(ClientQuery3.class);

    public ClientQuery3() throws IOException, InterruptedException, ExecutionException {
    }

    public static void main(String[] args) {
        String n = System.getProperty("n");
        String fromDate = System.getProperty("from");
        String toDate = System.getProperty("to");

        ArgParser.validateQuery3Properties(n, fromDate, toDate);

        LocalDate from = LocalDate.parse(fromDate, ArgParser.DATE_FORMATTER);
        LocalDate to = LocalDate.parse(toDate, ArgParser.DATE_FORMATTER);

        try (ClientQuery3 client = new ClientQuery3()) {
            client.executeQuery("query3",
                    (hazelcastInstance) -> {
                        City city = City.getCity(client.cityStr);
                        return city.getQueryEngine().runQuery3(hazelcastInstance, Integer.parseInt(n), from, to);
                    },
                    (hazelcastInstance) -> {
                        City city = City.getCity(client.cityStr);
                        city.getQueryLoader().loadQuery3(hazelcastInstance, client.inputPath);
                    }
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected <K, V> void writeResults(List<Map.Entry<K, V>> resultList, String outputPath) {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"County", "Percentage"});

        for (Map.Entry<K, V> entry : resultList) {
            rows.add(new String[]{entry.getKey().toString(), entry.getValue().toString()});
        }

        writeToCSV(rows, outputPath);
    }
}
