package ar.edu.itba.pod.grupo9.client.util.parser;

import ar.edu.itba.pod.grupo9.model.Ticket;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TicketParser {
    public static Ticket ticketFromChiCsv(String[] line) {
        return new Ticket(
                line[3],
                line[4],
                Double.parseDouble(line[5]),
                line[2],
                LocalDate.parse(line[0], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                line[1]
        );
    }

    public static Ticket ticketFromNycCsv(String[] line) {
        return new Ticket(
                line[0],
                line[1],
                Double.parseDouble(line[2]),
                line[3],
                LocalDate.parse(line[4], DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                line[5]
        );
    }
}
