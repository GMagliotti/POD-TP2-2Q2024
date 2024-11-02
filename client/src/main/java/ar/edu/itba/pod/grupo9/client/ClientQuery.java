package ar.edu.itba.pod.grupo9.client;

import ar.edu.itba.pod.grupo9.client.util.ArgParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutionException;


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

        // Validate city
        if (!ArgParser.isValidCity(cityStr)) {
            logger.error("Invalid city: " + cityStr);
            throw new IllegalArgumentException("Invalid city: " + cityStr);
        }

        // Validate input path
        if (!ArgParser.pathExists(inputPath)) {
            logger.error("Invalid input path: " + inputPath);
            throw new IllegalArgumentException("Invalid input path: " + inputPath);
        }

        // Validate output path
        if (!ArgParser.pathExists(outputPath)) {
            logger.error("Invalid output path: " + outputPath);
            throw new IllegalArgumentException("Invalid output path: " + outputPath);
        }

        // Validate addresses
        if (!ArgParser.areValidAddresses(addresses)) {
            logger.error("Invalid addresses: " + addresses);
            throw new IllegalArgumentException("Invalid addresses: " + addresses);
        }

        String[] addressesArr = addresses.replaceAll("^'|'$", "").split(";");

        Properties prop = loadProperties("config.properties");

        logger.info("Starting client...");
        this.hazelcastInstance = createHazelcastClient(prop, addressesArr);
    }


    private void validatePath(String type, String path) {
        if (!ArgParser.pathExists(path)) {
            logger.error("Invalid " + type + " path: " + path);
        }
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

    @Override
    public void close() throws IOException {

    }
}
