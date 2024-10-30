package ar.edu.itba.pod.grupo9.model;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a traffic ticket.
 * <p>
 * {@code plate} is the license plate of the vehicle.
 * {@code code} is the code of the infraction.
 * {@code fineAmount} is the amount of the fine.
 * {@code issuingAgency} is the agency that issued the ticket.
 * {@code issueDate} is the date when the ticket was issued.
 * {@code countyName} is the name of the county where the ticket was issued.
 * <p>
 * This class is {@link DataSerializable} to be used with Hazelcast.
 * <p>
 * This class is immutable.
 */
public class Ticket implements DataSerializable {
    private String plate;
    private String code;
    private Long fineAmount;
    private String issuingAgency;
    private LocalDate issueDate;
    private String countyName;

    public Ticket() {
        // empty constructor for Hazelcast
    }

    public Ticket(String plate, String code, Long fineAmount, String issuingAgency, LocalDate issueDate, String countyName) {
        this.plate = plate;
        this.code = code;
        this.fineAmount = fineAmount;
        this.issuingAgency = issuingAgency;
        this.issueDate = issueDate;
        this.countyName = countyName;
    }

    public static Ticket fromChiCsv(String[] line) {
        return new Ticket(
                line[3],
                line[4],
                Long.parseLong(line[5]),
                line[2],
                LocalDate.parse(line[0], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                line[1]
        );
    }

    public static Ticket fromNycCsv(String[] line) {
        return new Ticket(
                line[0],
                line[1],
                Long.parseLong(line[2]),
                line[3],
                LocalDate.parse(line[4], DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                line[5]
        );
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(plate);
        out.writeUTF(code);
        out.writeLong(fineAmount);
        out.writeUTF(issuingAgency);
        out.writeUTF(countyName);
        out.writeLong(issueDate.toEpochDay());
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        plate = in.readUTF();
        code = in.readUTF();
        fineAmount = in.readLong();
        issuingAgency = in.readUTF();
        countyName = in.readUTF();
        issueDate = LocalDate.ofEpochDay(in.readLong());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Ticket ticket = (Ticket) obj;
        return plate.equals(ticket.plate) && code.equals(ticket.code) &&
                fineAmount.equals(ticket.fineAmount) && issuingAgency.equals(ticket.issuingAgency) &&
                issueDate.equals(ticket.issueDate) && countyName.equals(ticket.countyName);
    }

    @Override
    public int hashCode() {
        return plate.hashCode() + code.hashCode() + fineAmount.hashCode() + issuingAgency.hashCode() +
                issueDate.hashCode() + countyName.hashCode();
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "plate='" + plate + '\'' +
                ", code='" + code + '\'' +
                ", fineAmount=" + fineAmount +
                ", issuingAgency='" + issuingAgency + '\'' +
                ", issueDate=" + issueDate +
                ", countyName='" + countyName + '\'' +
                '}';
    }

    public String getPlate() {
        return plate;
    }

    public String getCode() {
        return code;
    }

    public Long getFineAmount() {
        return fineAmount;
    }

    public String getIssuingAgency() {
        return issuingAgency;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public String getCountyName() {
        return countyName;
    }
}
