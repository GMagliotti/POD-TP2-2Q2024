package ar.edu.itba.pod.grupo9.client;

import ar.edu.itba.pod.grupo9.client.util.ArgParser;
import ar.edu.itba.pod.grupo9.client.util.City;
import ar.edu.itba.pod.grupo9.model.Pair;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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

        try(ClientQuery3 client = new ClientQuery3()) {
            try {
                logger.info("Client started");
                logger.info("Loading data...");

                City city = City.getCity(client.cityStr);

                logger.info("City: " + client.cityStr);

                // Load data
                city.getQueryLoader().loadQuery3(client.hazelcastInstance, client.inputPath);

                logger.info("Data loaded");
                logger.info("Running query...");

                // Solve query
                List<Map.Entry<String, Double>> resultList = city.getQueryEngine().runQuery3(client.hazelcastInstance, Integer.parseInt(n), from, to);

                logger.info("Query executed");
                logger.info("Writing results...");

                // Write results
                writeResults(resultList, client.outputPath + "/query3.csv");
                logger.info("Results written");

            } finally {
                HazelcastClient.shutdownAll();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeResults(List<Map.Entry<String, Double>> resultList, String outputPath) {
        try (
                CSVWriter writer = new CSVWriter(new java.io.FileWriter(outputPath),
                        ';',
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END);
                ) {

            writer.writeNext(new String[]{"County", "Percentage"});
            for (Map.Entry<String, Double> entry : resultList) {
                writer.writeNext(new String[]{entry.getKey(), entry.getValue().toString()});
            }
        } catch (IOException e) {
            logger.error("Error writing results", e);
            System.exit(1);
        }
    }
}
