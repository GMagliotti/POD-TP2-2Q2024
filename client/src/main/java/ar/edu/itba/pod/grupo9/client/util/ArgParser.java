package ar.edu.itba.pod.grupo9.client.util;

import org.apache.commons.lang3.time.DateUtils;

import java.io.File;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ArgParser {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private enum Cities {
        NYC, CHI
    }

    /**
     * Checks if the given string is a valid city.
     * @param city The string to check.
     * @return True if the string
     */
    public static boolean isValidCity(String city) {
        for (Cities c : Cities.values()) {
            if (c.name().toUpperCase().equals(city)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given path exists.
     * @param path The path to check.
     * @return True if the path exists.
     */
    public static boolean pathExists(String path) {
        return path != null && !path.isEmpty()
                && new File(path).exists();
    }

    /**
     * Checks if the given string is a valid address with format "address:port".
     * @param addresses The string to check.
     * @return True if the string is a valid address.
     */
    public static boolean areValidAddresses(String addresses) {
        if (addresses == null || addresses.isEmpty()) {
            return false;
        }
        // format of addresses: "address:port;address:port;address:port"
        String[] addressList = addresses.split(";");
        for (String address : addressList) {
            String[] parts = address.split(":");
            if (parts.length != 2) {
                return false;
            }
            try {
                Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    public static void validateProperties(String city, String inPath, String outPath, String addresses) {
        if (!isValidCity(city)) {
            //throw new IllegalArgumentException("Invalid city: " + city);
            System.err.println("Invalid city: " + city);
            System.exit(1);
        }

        if (!pathExists(inPath)) {
            //throw new IllegalArgumentException("Invalid input path: " + inPath);
            System.err.println("Invalid input path: " + inPath);
            System.exit(1);
        }

        if (!pathExists(outPath)) {
            //throw new IllegalArgumentException("Invalid output path: " + outPath);
            System.err.println("Invalid output path: " + outPath);
            System.exit(1);
        }

        if (!areValidAddresses(addresses)) {
            //throw new IllegalArgumentException("Invalid addresses: " + addresses);
            System.err.println("Invalid addresses: " + addresses);
            System.exit(1);
        }
    }

    public static void validateQuery3Properties(String n, String fromDate, String toDate) {
        // n must be a number
        if (n == null || n.isEmpty() || !n.matches("\\d+")) {
            //throw new IllegalArgumentException("Invalid n: " + n);
            System.err.println("Invalid n: " + n);
            System.exit(1);
        }
        try {
            int nInt = Integer.parseInt(n);
            if (nInt < 2) {
                //throw new IllegalArgumentException("n must be greater or equal to 2");
                System.err.println("n must be greater or equal to 2");
                System.exit(1);
            }
        } catch (NumberFormatException e) {
            //throw new IllegalArgumentException("Invalid n: " + n);
            System.err.println("Invalid n: " + n);
            System.exit(1);
        }

        try {
            if (fromDate == null || fromDate.isEmpty()) {
                //throw new IllegalArgumentException("Invalid from date: " + fromDate);
                System.err.println("Invalid from date: " + fromDate);
                System.exit(1);
            }

            if (toDate == null || toDate.isEmpty()) {
                //throw new IllegalArgumentException("Invalid to date: " + toDate);
                System.err.println("Invalid to date: " + toDate);
                System.exit(1);
            }

            // check if the dates are in the correct format
            DateUtils.parseDate(fromDate, "dd/MM/yyyy");
            DateUtils.parseDate(toDate, "dd/MM/yyyy");

            if (DateUtils.parseDate(fromDate, "dd/MM/yyyy").after(DateUtils.parseDate(toDate, "dd/MM/yyyy"))) {
                //throw new IllegalArgumentException("From date must be before to date");
                System.err.println("From date must be before to date");
                System.exit(1);
            }
        } catch (ParseException e) {
            //throw new IllegalArgumentException("Invalid date format. Use format: dd/MM/yyyy");
            System.err.println("Invalid date format. Use format: dd/MM/yyyy");
            System.exit(1);
        }
    }

}
