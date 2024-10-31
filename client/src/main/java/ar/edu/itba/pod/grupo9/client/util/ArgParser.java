package ar.edu.itba.pod.grupo9.client.util;

import java.io.File;

public class ArgParser {
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

}
