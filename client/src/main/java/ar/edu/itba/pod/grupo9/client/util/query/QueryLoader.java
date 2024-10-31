package ar.edu.itba.pod.grupo9.client.util.query;

import ar.edu.itba.pod.grupo9.client.util.City;
import ar.edu.itba.pod.grupo9.model.Infraction;
import ar.edu.itba.pod.grupo9.model.Ticket;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;
import com.opencsv.*;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public enum QueryLoader {
    NYC {
        @Override
        public void loadQuery1(HazelcastInstance hazelcastInstance, String inPath) {
            // load the tickets and infractions for NYC using IMap and MultiMap. We also have the opencsv library to read the csv files
            logger.info("Loading tickets and infractions for NYC");

            try(
                    InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties")
                    ) {

                Properties prop = new Properties();
                if (inputStream != null) {
                    prop.load(inputStream);
                } else {
                    throw new RuntimeException("property file not found in the classpath");
                }

                final MultiMap<String, Ticket> tickets = hazelcastInstance.getMultiMap(prop.getProperty("hz.collection.tickets"));
                final IMap<String, Infraction> infractions = hazelcastInstance.getMap(prop.getProperty("hz.collection.infractions"));
                logger.info("Loading tickets and infractions for NYC");

                String ticketsFile = inPath + "/" + City.NYC.getTicketsPath();
                String infractionsFile = inPath + "/" + City.NYC.getInfractionsPath();

                try (
                        CSVReader reader = new CSVReaderBuilder(new FileReader(ticketsFile))
                                .withCSVParser(new CSVParserBuilder()
                                        .withSeparator(';')
                                        .build()
                                )
                                .withSkipLines(1)
                                .build()
                ) {
                    String[] line;
                    logger.info("NYC tickets loading started");
                    while ((line = reader.readNext()) != null) {
                        final Ticket ticket = Ticket.fromNycCsv(line);
                        tickets.put(ticket.getCode(), ticket);
                    }
                } catch (IOException e) {
                    System.out.println("Error reading tickets file");
                    e.printStackTrace();
                    System.exit(1);
                } catch (CsvValidationException e) {
                    throw new RuntimeException(e);
                } finally {
                    logger.info("NYC tickets loading finished");
                }

                try (
                        CSVReader reader = new CSVReaderBuilder(new FileReader(infractionsFile))
                                .withCSVParser(new CSVParserBuilder()
                                        .withSeparator(';')
                                        .build()
                                )
                                .withSkipLines(1)
                                .build()
                ) {
                    String[] line;
                    logger.info("NYC infractions loading started");
                    while ((line = reader.readNext()) != null) {
                        final Infraction infraction = Infraction.fromInfractionCsv(line);
                        infractions.put(infraction.getCode(), infraction);
                    }
                } catch (IOException e) {
                    System.out.println("Error reading infractions file");
                    e.printStackTrace();
                    System.exit(1);
                } catch (CsvValidationException e) {
                    throw new RuntimeException(e);
                } finally {
                    logger.info("NYC infractions loading finished");
                }

            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }


        }
    }, CHI {
        @Override
        public void loadQuery1(HazelcastInstance hazelcastInstance, String inPath) {

            logger.info("Loading tickets and infractions for CHI");
            try (
                    InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties")
                    ) {

                Properties prop = new Properties();
                if (inputStream != null) {
                    prop.load(inputStream);
                } else {
                    throw new RuntimeException("property file not found in the classpath");
                }

                final MultiMap<String, Ticket> tickets = hazelcastInstance.getMultiMap(prop.getProperty("hz.collection.tickets"));
                final IMap<String, Infraction> infractions = hazelcastInstance.getMap(prop.getProperty("hz.collection.infractions"));

                logger.info("Loading tickets and infractions for CHI");

                String ticketsFile = inPath + "/" + City.CHI.getTicketsPath();
                String infractionsFile = inPath + "/" + City.CHI.getInfractionsPath();

                try (
                        CSVReader reader = new CSVReaderBuilder(new FileReader(ticketsFile))
                                .withCSVParser(new CSVParserBuilder()
                                        .withSeparator(';')
                                        .build()
                                )
                                .withSkipLines(1)
                                .build()
                ) {
                    String[] line;
                    logger.info("CHI tickets loading started");
                    while ((line = reader.readNext()) != null) {
                        final Ticket ticket = Ticket.fromChiCsv(line);
                        tickets.put(ticket.getCode(), ticket);
                    }
                } catch (IOException e) {
                    System.out.println("Error reading tickets file");
                    e.printStackTrace();
                    System.exit(1);
                } catch (CsvValidationException e) {
                    throw new RuntimeException(e);
                } finally {
                    logger.info("CHI tickets loading finished");
                }

                try (
                        CSVReader reader = new CSVReaderBuilder(new FileReader(infractionsFile))
                                .withCSVParser(new CSVParserBuilder()
                                        .withSeparator(';')
                                        .build()
                                )
                                .withSkipLines(1)
                                .build()
                ) {
                    String[] line;
                    logger.info("CHI infractions loading started");
                    while ((line = reader.readNext()) != null) {
                        final Infraction infraction = Infraction.fromInfractionCsv(line);
                        infractions.put(infraction.getCode(), infraction);
                    }
                } catch (IOException e) {
                    System.out.println("Error reading infractions file");
                    e.printStackTrace();
                    System.exit(1);
                } catch (CsvValidationException e) {
                    throw new RuntimeException(e);
                } finally {
                    logger.info("CHI infractions loading finished");
                }

            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

        }
    };

    private static final Logger logger = LoggerFactory.getLogger(QueryLoader.class);

    public abstract void loadQuery1(HazelcastInstance hazelcastInstance, String inPath);
}
