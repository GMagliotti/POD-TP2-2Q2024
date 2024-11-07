package ar.edu.itba.pod.grupo9.server;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;

public class Server {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        logger.info(" Server Starting ...");

        String address = "192.168.1.*";
        int port = 5701;

        // Get address from system property
        String addressProperty = System.getProperty("address");
        if (addressProperty != null) {
            try {
                address = addressProperty;
            } catch (NumberFormatException e) {
                logger.error("Invalid port number format, using default port: " + port);
            }
        }

        // Get port from system property
        String portProperty = System.getProperty("port");
        if (portProperty != null) {
            try {
                port = Integer.parseInt(portProperty);
            } catch (NumberFormatException e) {
                logger.error("Invalid port number format, using default port: " + port);
            }
        }

        // get properties file
        try(
                InputStream inputStream = Server.class.getClassLoader().getResourceAsStream("config.properties")
                ) {

            Properties prop = new Properties();
            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new RuntimeException("property file not found in the classpath");
            }

            // Config
            Config config = new Config();

            // Group Config
            GroupConfig groupConfig = new GroupConfig().setName(prop.getProperty("hz.cluster.name"))
                    .setPassword(prop.getProperty("hz.cluster.password"));
            config.setGroupConfig(groupConfig);

            // Network Config
            MulticastConfig multicastConfig = new MulticastConfig();

            JoinConfig joinConfig = new JoinConfig().setMulticastConfig(multicastConfig);

            InterfacesConfig interfacesConfig = new InterfacesConfig()
                    .setInterfaces(Collections.singletonList(address))
                    .setEnabled(true);

            NetworkConfig networkConfig = new NetworkConfig().setInterfaces(interfacesConfig)
                    .setJoin(joinConfig)
                    .setPort(port);


            config.setNetworkConfig(networkConfig);

            // Opcional: Logger detallado
//        java.util.logging.Logger rootLogger = LogManager.getLogManager().getLogger("");
//        rootLogger.setLevel(Level.FINE);
//        for (Handler h : rootLogger.getHandlers()) {
//            h.setLevel(Level.FINE);
//        }

            // Start cluster
            Hazelcast.newHazelcastInstance(config);

        } catch (Exception e) {
            logger.error("Error reading properties file");
            e.printStackTrace();
            System.exit(1);
        }

    }

}
