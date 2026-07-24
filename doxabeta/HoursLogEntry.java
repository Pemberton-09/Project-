package doxabeta;

import java.time.LocalDate;

/** A single day's logged placement hours, mirroring Doxabeta's Daily Hours Log tab. */
public class HoursLogEntry {

    private final LocalDate date;
    private final double hours;
    private final boolean submitted;

    public HoursLogEntry(LocalDate date, double hours, boolean submitted) {
        this.date = date;
        this.hours = hours;
        this.submitted = submitted;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getHours() {
        return hours;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    @Override
    public String toString() {
        return String.format("%s | %.1f hrs | %s", date, hours, submitted ? "Submitted" : "NOT submitted");
    }
}
