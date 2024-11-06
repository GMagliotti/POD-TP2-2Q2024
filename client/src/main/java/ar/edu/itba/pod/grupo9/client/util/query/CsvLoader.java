package ar.edu.itba.pod.grupo9.client.util.query;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.function.Consumer;

public class CsvLoader {

    private static final Logger logger = LoggerFactory.getLogger(CsvLoader.class);

    public static void loadData(String filePath, Consumer<String[]> processor) {
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(filePath))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .withSkipLines(1)
                .build()) {

            String[] line;
            while ((line = reader.readNext()) != null) {
                processor.accept(line);
            }
        } catch (IOException | CsvValidationException e) {
            logger.error("Error reading CSV file", e);
            System.exit(1);
        }
    }
}
