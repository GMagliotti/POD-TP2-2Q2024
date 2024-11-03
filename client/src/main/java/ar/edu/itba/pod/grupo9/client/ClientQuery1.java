package ar.edu.itba.pod.grupo9.client;

import ar.edu.itba.pod.grupo9.client.util.City;
import ar.edu.itba.pod.grupo9.model.Pair;
import com.hazelcast.client.HazelcastClient;
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
            try {
                client.logTimestamp("Inicio de la lectura de los archivos de entrada");

                logger.info("Loading data...");
                City city = City.getCity(client.cityStr);
                city.getQueryLoader().loadQuery1(client.hazelcastInstance, client.inputPath);

                client.logTimestamp("Fin de lectura de los archivos de entrada");

                logger.info("Data loaded");
                logger.info("Running query...");
                client.logTimestamp("Inicio de un trabajo MapReduce");
                List<Map.Entry<Pair<String, String>, Integer>> resultList = city.getQueryEngine().runQuery1(client.hazelcastInstance);
                client.logTimestamp("Fin de un trabajo MapReduce");

                logger.info("Query executed");

                logger.info("Writing results...");
                client.writeResults(resultList, client.outputPath + "/query1.csv");

                logger.info("Client execution completed.");
            } finally {
                HazelcastClient.shutdownAll();
            }
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
