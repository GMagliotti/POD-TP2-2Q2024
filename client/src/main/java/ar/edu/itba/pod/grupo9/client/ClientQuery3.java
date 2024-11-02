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

public class ClientQuery3 {
    private static final Logger logger = LoggerFactory.getLogger(ClientQuery3.class);

    public static void main(String[] args) {

        String cityStr = System.getProperty("city");
        String inputPath = System.getProperty("inPath");
        String outputPath = System.getProperty("outPath");
        String addresses = System.getProperty("addresses");
        String n = System.getProperty("n");
        String fromDate = System.getProperty("from");
        String toDate = System.getProperty("to");

        ArgParser.validateProperties(cityStr, inputPath, outputPath, addresses);
        ArgParser.validateQuery3Properties(n, fromDate, toDate);

        LocalDate from = LocalDate.parse(fromDate, ArgParser.DATE_FORMATTER);
        LocalDate to = LocalDate.parse(toDate, ArgParser.DATE_FORMATTER);

        String[] addressesArr = addresses.replaceAll("^'|'$", "").split(";");

        try(
                InputStream inputStream = ClientQuery3.class.getClassLoader().getResourceAsStream("config.properties")
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
                city.getQueryLoader().loadQuery3(hazelcastInstance, inputPath);

                logger.info("Data loaded");
                logger.info("Running query...");

                // Solve query
                List<Map.Entry<String, Double>> resultList = city.getQueryEngine().runQuery3(hazelcastInstance, Integer.parseInt(n), from, to);

                logger.info("Query executed");
                logger.info("Writing results...");

                // Write results
                writeResults(resultList, outputPath + "/query3.csv");


                logger.info("Results written");


            } finally {
                HazelcastClient.shutdownAll();
            }
        } catch (Exception e) {
            logger.error("Error loading properties file", e);
            System.exit(1);
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
