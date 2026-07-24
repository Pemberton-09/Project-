package doxabeta;

/** A single mid-point, final, or check-in review recorded against a student. */
public class Review {

    public enum Type { MID, FINAL, CHECK_IN }

    private final Type type;
    private final String reviewer;
    private final int attendanceRating;
    private final int progressRating;
    private final int workQualityRating;
    private final int communicationRating;
    private final String notes;

    public Review(Type type, String reviewer, int attendanceRating, int progressRating,
                  int workQualityRating, int communicationRating, String notes) {
        this.type = type;
        this.reviewer = reviewer;
        this.attendanceRating = attendanceRating;
        this.progressRating = progressRating;
        this.workQualityRating = workQualityRating;
        this.communicationRating = communicationRating;
        this.notes = notes;
    }

    public Type getType() {
        return type;
    }

    public String getReviewer() {
        return reviewer;
    }

    public double getAverageRating() {
        return (attendanceRating + progressRating + workQualityRating + communicationRating) / 4.0;
    }

    public String getNotes() {
        return notes;
    }

    @Override
    public String toString() {
        return String.format("[%s] by %-18s avg rating %.1f/5 - %s", type, reviewer, getAverageRating(), notes);
    }
}
