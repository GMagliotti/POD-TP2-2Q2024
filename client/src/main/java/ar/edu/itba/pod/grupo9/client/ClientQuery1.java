package ar.edu.itba.pod.grupo9.client;

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
import ar.edu.itba.pod.grupo9.client.util.ArgParser;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class ClientQuery1 {
    private static final Logger logger = LoggerFactory.getLogger(ClientQuery1.class);

    public static void main(String[] args) throws InterruptedException, IOException, ExecutionException {

        String cityStr = System.getProperty("city");
        String inputPath = System.getProperty("inPath");
        String outputPath = System.getProperty("outPath");
        String addresses = System.getProperty("addresses");

        // Validate city
        if (!ArgParser.isValidCity(cityStr)) {
            logger.error("Invalid city: " + cityStr);
            return;
        }

        // Validate input path
        if (!ArgParser.pathExists(inputPath)) {
            logger.error("Invalid input path: " + inputPath);
            return;
        }

        // Validate output path
        if (!ArgParser.pathExists(outputPath)) {
            logger.error("Invalid output path: " + outputPath);
            return;
        }

        // Validate addresses. There may be several addresses separated by ;
        // For example: 127.0.0.1:5701;10.0.16.1:5701;127.0.0.3:5701
        if (!ArgParser.areValidAddresses(addresses)) {
            logger.error("Invalid addresses: " + addresses);
            return;
        }

        String[] addressesArr = addresses.replaceAll("^'|'$", "").split(";");

        // get properties file from resources

        try (
                InputStream inputStream = ClientQuery1.class.getClassLoader().getResourceAsStream("config.properties")
        ) {
            Properties prop = new Properties();
            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new RuntimeException("property file not found in the classpath");
            }

            try {
                logger.info("hz-bike-rentals Client Starting ...");

                // Group Config
                GroupConfig groupConfig = new GroupConfig().setName(prop.getProperty("hz.cluster.name"))
                        .setPassword(prop.getProperty("hz.cluster.password"));

                // Client Network Config
                ClientNetworkConfig clientNetworkConfig = new ClientNetworkConfig();
                clientNetworkConfig.addAddress(addressesArr);

                // Client Config
                ClientConfig clientConfig = new ClientConfig().setGroupConfig(groupConfig).setNetworkConfig(clientNetworkConfig);

                // Node Client
                HazelcastInstance hazelcastInstance = HazelcastClient.newHazelcastClient(clientConfig);

                logger.info("Client started");
                logger.info("Loading data...");

                City city = City.getCity(cityStr);

                logger.info("City: " + cityStr);

                // Load data
                city.getQueryLoader().loadQuery1(hazelcastInstance, inputPath);

                logger.info("Data loaded");
                logger.info("Running query...");

                // Solve query
                List<Map.Entry<Pair<String, String>, Integer>> resultList = city.getQueryEngine().runQuery1(hazelcastInstance);

                logger.info("Query executed");
                logger.info("Writing results...");

                // Write results
                writeResults(resultList, outputPath + "/query1.csv");

                logger.info("Results written");


            } finally {
                HazelcastClient.shutdownAll();
            }

        } catch (IOException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }
}
