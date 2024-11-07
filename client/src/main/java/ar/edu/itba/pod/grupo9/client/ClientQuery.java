package ar.edu.itba.pod.grupo9.client;

import ar.edu.itba.pod.grupo9.client.util.parser.ArgParser;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;


import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;


public abstract class ClientQuery implements Closeable {

    private static final Logger logger = LoggerFactory.getLogger(ClientQuery.class);

    protected final String cityStr;
    protected final String inputPath;
    protected final String outputPath;
    protected final String addresses;
    protected final HazelcastInstance hazelcastInstance;

    public ClientQuery() throws IOException, InterruptedException, ExecutionException {
        this.cityStr = System.getProperty("city");
        this.inputPath = System.getProperty("inPath");
        this.outputPath = System.getProperty("outPath");
        this.addresses = System.getProperty("addresses");

        ArgParser.validateProperties(this.cityStr, this.inputPath, this.outputPath, this.addresses);

        String[] addressesArr = addresses.replaceAll("^'|'$", "").split(";");

        Properties prop = loadProperties("config.properties");

        logger.info("Starting client...");
        this.hazelcastInstance = createHazelcastClient(prop, addressesArr);
    }

    public static Properties loadProperties(String filename) {
        Properties prop = new Properties();
        try (InputStream inputStream = ClientQuery.class.getClassLoader().getResourceAsStream(filename)) {
            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new RuntimeException("Property file not found in the classpath");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }

    private HazelcastInstance createHazelcastClient(Properties prop, String[] addressesArr) {
        GroupConfig groupConfig = new GroupConfig()
                .setName(prop.getProperty("hz.cluster.name"))
                .setPassword(prop.getProperty("hz.cluster.password"));

        ClientNetworkConfig clientNetworkConfig = new ClientNetworkConfig();
        clientNetworkConfig.addAddress(addressesArr);

        ClientConfig clientConfig = new ClientConfig()
                .setGroupConfig(groupConfig)
                .setNetworkConfig(clientNetworkConfig);

        return HazelcastClient.newHazelcastClient(clientConfig);
    }

    protected <K, V> void executeQuery(
            String queryType,
            Function<HazelcastInstance, List<Map.Entry<K, V>>> queryExecutor,
            Consumer<HazelcastInstance> dataLoader
    ) {
        try {
            logTimestamp("Inicio de la lectura de los archivos de entrada");
            logger.info("Loading data...");

            dataLoader.accept(this.hazelcastInstance);

            logTimestamp("Fin de lectura de los archivos de entrada");
            logger.info("Data loaded");

            logger.info("Running query...");
            logTimestamp("Inicio de un trabajo MapReduce");
            List<Map.Entry<K, V>> resultList = queryExecutor.apply(this.hazelcastInstance);
            logTimestamp("Fin de un trabajo MapReduce");

            logger.info("Query executed");
            logger.info("Writing results...");
            logTimestamp("Inicio de la escritura de los resultados");
            writeResults(resultList, this.outputPath + "/" + queryType + ".csv");
            logTimestamp("Fin de la escritura de los resultados");
            logger.info("Results written");
        } catch (Exception e) {
            logger.error("Error executing query", e);
        } finally {
            HazelcastClient.shutdownAll();
        }
    }

    protected abstract <K, V> void writeResults(List<Map.Entry<K, V>> rows, String outputPath);

    protected void writeToCSV(List<String[]> rows, String outputPath) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(outputPath),
                ';',
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.NO_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END)) {
            for (String[] row : rows) {
                writer.writeNext(row);
            }
        } catch (IOException e) {
            logger.error("Error writing results", e);
            System.exit(1);
        }
    }

    protected void logTimestamp(String message) {
        String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSSS").format(new Date());
        String logEntry = timestamp + " - " + message;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.outputPath + "/time.txt", true))) {
            writer.write(logEntry);
            writer.newLine();
        } catch (IOException e) {
            logger.error("Error writing to time log", e);
        }
        logger.info(message);
    }

    @Override
    public void close() throws IOException {

    }
}
