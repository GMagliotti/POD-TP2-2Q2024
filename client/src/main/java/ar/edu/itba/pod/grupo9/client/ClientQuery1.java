package ar.edu.itba.pod.grupo9.client;

import ar.edu.itba.pod.grupo9.client.util.City;
import ar.edu.itba.pod.grupo9.model.Pair;
import com.hazelcast.client.HazelcastClient;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class ClientQuery1 extends ClientQuery{
    private static final Logger logger = LoggerFactory.getLogger(ClientQuery1.class);

    public ClientQuery1() throws IOException, InterruptedException, ExecutionException {
    }

    public static void main(String[] args) {
        try (ClientQuery1 client = new ClientQuery1()){
            try {
                logger.info("Loading data...");
                City city = City.getCity(client.cityStr);
                city.getQueryLoader().loadQuery1(client.hazelcastInstance, client.inputPath);

                logger.info("Running query...");
                List<Map.Entry<Pair<String, String>, Integer>> resultList = city.getQueryEngine().runQuery1(client.hazelcastInstance);

                logger.info("Writing results...");
                writeResults(resultList, client.outputPath + "/query1.csv");

                logger.info("Client execution completed.");
            } finally {
                HazelcastClient.shutdownAll();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Write results
    public static void writeResults(List<Map.Entry<Pair<String, String>, Integer>> resultList, String outputPath) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(outputPath),
                ';',
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END)) {
            // Write header
            String[] header = {"Infraction", "Agency", "Tickets"};
            writer.writeNext(header);

            // Write data
            for (Map.Entry<Pair<String, String>, Integer> entry : resultList) {
                //pair first is infraction, second is agency and the integer value is the count
                // we must print to the csv format: infraction;agency;count
                String[] line = {entry.getKey().getFirst(), entry.getKey().getSecond(), entry.getValue().toString()};
                writer.writeNext(line);
            }
        } catch (IOException e) {
            logger.error("Error writing results", e);
            System.exit(1);
        }
    }
}
