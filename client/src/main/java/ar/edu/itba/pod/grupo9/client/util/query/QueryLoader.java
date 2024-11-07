package ar.edu.itba.pod.grupo9.client.util.query;

import ar.edu.itba.pod.grupo9.client.util.City;
import ar.edu.itba.pod.grupo9.client.util.parser.TicketParser;
import ar.edu.itba.pod.grupo9.model.Infraction;
import ar.edu.itba.pod.grupo9.model.Ticket;
import com.hazelcast.core.*;
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
            loadDataQuery1(hazelcastInstance, inPath, City.NYC);
        }

        @Override
        public void loadQuery2(HazelcastInstance hazelcastInstance, String inPath) {
            loadDataQuery2(hazelcastInstance, inPath, City.NYC);
        }

        @Override
        public void loadQuery3(HazelcastInstance hazelcastInstance, String inPath) {
            loadDataQuery3(hazelcastInstance, inPath, City.NYC);
        }

        @Override
        public void loadQuery4(HazelcastInstance hazelcastInstance, String inPath) {
            loadDataQuery4(hazelcastInstance, inPath, City.NYC);
        }
    },
    CHI {
        @Override
        public void loadQuery1(HazelcastInstance hazelcastInstance, String inPath) {
            loadDataQuery1(hazelcastInstance, inPath, City.CHI);
        }

        @Override
        public void loadQuery2(HazelcastInstance hazelcastInstance, String inPath) {
            loadDataQuery2(hazelcastInstance, inPath, City.CHI);
        }

        @Override
        public void loadQuery3(HazelcastInstance hazelcastInstance, String inPath) {
            loadDataQuery3(hazelcastInstance, inPath, City.CHI);
        }

        @Override
        public void loadQuery4(HazelcastInstance hazelcastInstance, String inPath) {
            loadDataQuery4(hazelcastInstance, inPath, City.CHI);
        }
    };

    private static final Logger logger = LoggerFactory.getLogger(QueryLoader.class);

    private static void loadDataQuery1(HazelcastInstance hazelcastInstance, String inPath, City city) {
        Properties prop = loadProperties();
        MultiMap<String, Ticket> tickets = hazelcastInstance.getMultiMap(prop.getProperty("hz.collection.tickets." + city.name().toLowerCase()));
        ReplicatedMap<String, Infraction> infractions = hazelcastInstance.getReplicatedMap(prop.getProperty("hz.collection.infractions." + city.name().toLowerCase()));
        ReplicatedMap<String, Integer> agencies = hazelcastInstance.getReplicatedMap(prop.getProperty("hz.collection.agencies." + city.name().toLowerCase()));

        loadTickets(tickets, inPath + "/" + city.getTicketsPath(), city);
        loadInfractions(infractions, inPath + "/" + city.getInfractionsPath(), city);
        loadAgencies(agencies, inPath + "/" + city.getAgenciesPath(), city);
    }

    private static void loadDataQuery2(HazelcastInstance hazelcastInstance, String inPath, City city) {
        Properties prop = loadProperties();
        MultiMap<String, Ticket> tickets = hazelcastInstance.getMultiMap(prop.getProperty("hz.collection.tickets." + city.name().toLowerCase()));
        ReplicatedMap<String, Integer> agencies = hazelcastInstance.getReplicatedMap(prop.getProperty("hz.collection.agencies." + city.name().toLowerCase()));

        loadTickets(tickets, inPath + "/" + city.getTicketsPath(), city);
        loadAgencies(agencies, inPath + "/" + city.getAgenciesPath(), city);
    }

    private static void loadDataQuery3(HazelcastInstance hazelcastInstance, String inPath, City city) {
        Properties prop = loadProperties();
        MultiMap<String, Ticket> tickets = hazelcastInstance.getMultiMap(prop.getProperty("hz.collection.tickets." + city.name().toLowerCase()));

        loadTickets(tickets, inPath + "/" + city.getTicketsPath(), city);
    }

    private static void loadDataQuery4(HazelcastInstance hazelcastInstance, String inPath, City city) {
        Properties prop = loadProperties();

        MultiMap<String, Ticket> tickets = hazelcastInstance.getMultiMap(prop.getProperty("hz.collection.tickets." + city.name().toLowerCase()));
        ReplicatedMap<String, Infraction> infractions = hazelcastInstance.getReplicatedMap(prop.getProperty("hz.collection.infractions." + city.name().toLowerCase()));
        ReplicatedMap<String, Integer> agencies = hazelcastInstance.getReplicatedMap(prop.getProperty("hz.collection.agencies." + city.name().toLowerCase()));

        loadTickets(tickets, inPath + "/" + city.getTicketsPath(), city);
        loadInfractions(infractions, inPath + "/" + city.getInfractionsPath(), city);
        loadAgencies(agencies, inPath + "/" + city.getAgenciesPath(), city);

    }

    private static Properties loadProperties() {
        Properties prop = new Properties();
        try (InputStream inputStream = QueryLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new RuntimeException("Property file not found in the classpath");
            }
        } catch (IOException e) {
            logger.error("Error loading properties file", e);
            System.exit(1);
        }
        return prop;
    }

    private static void loadTickets(MultiMap<String, Ticket> tickets, String filePath, City city) {
        logger.info("{} tickets loading started", city);
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(filePath))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .withSkipLines(1)
                .build()) {

            String[] line;
            while ((line = reader.readNext()) != null) {
                Ticket ticket = city == City.NYC ? TicketParser.ticketFromNycCsv(line) : TicketParser.ticketFromChiCsv(line);
                tickets.put(ticket.getCode(), ticket);
            }
        } catch (IOException | CsvValidationException e) {
            logger.error("Error reading tickets file", e);
            System.exit(1);
        } finally {
            logger.info("{} tickets loading finished", city);
        }
    }

    private static void loadTicketsQuery1Optimized(MultiMap<String, Ticket> tickets, String filePath, City city) {
        logger.info("{} tickets loading started", city);
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(filePath))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .withSkipLines(1)
                .build()) {

            String[] line;
            while ((line = reader.readNext()) != null) {
                Ticket ticket = city == City.NYC ? TicketParser.ticketFromNycCsvOptimized(line) : TicketParser.ticketFromChiCsvOptimized(line);
                tickets.put(ticket.getCode(), ticket);
            }
        } catch (IOException | CsvValidationException e) {
            logger.error("Error reading tickets file", e);
            System.exit(1);
        } finally {
            logger.info("{} tickets loading finished", city);
        }
    }

    private static void loadAgencies(ReplicatedMap<String, Integer> agencies, String filePath, City city) {
        logger.info("{} agencies loading started", city);
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(filePath))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .withSkipLines(1)
                .build()) {

            String[] line;
            while ((line = reader.readNext()) != null) {
                agencies.put(line[0], 1);
            }
        } catch (IOException | CsvValidationException e) {
            logger.error("Error reading agencies file", e);
            System.exit(1);
        } finally {
            logger.info("Agencies loading finished");
        }
    }

    private static void loadInfractions(ReplicatedMap<String, Infraction> infractions, String filePath, City city) {
        logger.info("{} infractions loading started", city);
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(filePath))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .withSkipLines(1)
                .build()) {

            String[] line;
            while ((line = reader.readNext()) != null) {
                Infraction infraction = Infraction.fromInfractionCsv(line);
                infractions.put(infraction.getCode(), infraction);
            }
        } catch (IOException | CsvValidationException e) {
            logger.error("Error reading infractions file", e);
            System.exit(1);
        } finally {
            logger.info("{} infractions loading finished", city);
        }
    }


    public abstract void loadQuery1(HazelcastInstance hazelcastInstance, String inPath);

    public abstract void loadQuery2(HazelcastInstance hazelcastInstance, String inPath);

    public abstract void loadQuery4(HazelcastInstance hazelcastInstance, String inPath);

    public abstract void loadQuery3(HazelcastInstance hazelcastInstance, String inPath);
}
