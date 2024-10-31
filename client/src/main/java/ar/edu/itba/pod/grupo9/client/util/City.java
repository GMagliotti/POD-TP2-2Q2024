package ar.edu.itba.pod.grupo9.client.util;

import ar.edu.itba.pod.grupo9.client.util.query.QueryEngine;
import ar.edu.itba.pod.grupo9.client.util.query.QueryLoader;

/**
 * Enum representing the cities that can be used in the application.
 */
public enum City {
    NYC("nyc", "ticketsNYC.csv", "infractionsNYC.csv", "agenciesNYC.csv", QueryEngine.NYC, QueryLoader.NYC),
    CHI("chi", "ticketsCHI.csv", "infractionsCHI.csv", "agenciesCHI.csv", QueryEngine.CHI, QueryLoader.CHI);

    private final String city;
    private final String ticketsPath;
    private final String infractionsPath;
    private final String agenciesPath;
    private final QueryEngine queryEngine;
    private final QueryLoader queryLoader;

    City(String city, String ticketsPath, String infractionsPath, String agenciesPath, QueryEngine queryEngine, QueryLoader queryLoader) {
        this.city = city;
        this.ticketsPath = ticketsPath;
        this.infractionsPath = infractionsPath;
        this.agenciesPath = agenciesPath;
        this.queryEngine = queryEngine;
        this.queryLoader = queryLoader;
    }

    public String getCity() {
        return city;
    }

    public String getTicketsPath() {
        return ticketsPath;
    }

    public String getInfractionsPath() {
        return infractionsPath;
    }

    public String getAgenciesPath() {
        return agenciesPath;
    }

    public QueryEngine getQueryEngine() {
        return queryEngine;
    }

    public QueryLoader getQueryLoader() {
        return queryLoader;
    }

    public static City getCity(String city) {
        for (City c : City.values()) {
            if (c.getCity().equalsIgnoreCase(city)) {
                return c;
            }
        }
        throw new IllegalArgumentException("City not found");
    }
}
