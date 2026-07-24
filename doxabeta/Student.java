package doxabeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single internship student record.
 *
 * COMPLETE-PROTOTYPE NOTE: this extends the incomplete prototype's model
 * with reviews, an hours log, a project field, and a separate
 * SensitiveInfo object. Keeping sensitive fields (health conditions,
 * safeguarding notes, dietary requirements) in their own nested class
 * means a future access-control layer can restrict who is allowed to
 * read them without restructuring the whole Student class (see NFR1 in
 * the plan document).
 */
public class Student {

    private final String studentId;
    private final String firstName;
    private final String lastName;
    private final String college;
    private final String courseProgramme;
    private String mentorName;
    private String projectName;
    private String status; // Active, Upcoming, Completed, On Hold, Pending
    private double targetHours;
    private double hoursCompleted;
    private final SensitiveInfo sensitiveInfo;
    private final List<Review> reviews = new ArrayList<>();
    private final List<HoursLogEntry> hoursLog = new ArrayList<>();

    public Student(String studentId, String firstName, String lastName, String college,
                   String courseProgramme, String mentorName, String projectName, String status,
                   double targetHours, double hoursCompleted) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.college = college;
        this.courseProgramme = courseProgramme;
        this.mentorName = mentorName;
        this.projectName = projectName;
        this.status = status;
        this.targetHours = targetHours;
        this.hoursCompleted = hoursCompleted;
        this.sensitiveInfo = new SensitiveInfo();
    }

    public String getStudentId() {
        return studentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getCollege() {
        return college;
    }

    public String getCourseProgramme() {
        return courseProgramme;
    }

    public String getMentorName() {
        return mentorName;
    }

    public void setMentorName(String mentorName) {
        this.mentorName = mentorName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        String normalised = status.trim();
        boolean valid = normalised.equalsIgnoreCase("Active")
                || normalised.equalsIgnoreCase("Upcoming")
                || normalised.equalsIgnoreCase("Completed")
                || normalised.equalsIgnoreCase("On Hold")
                || normalised.equalsIgnoreCase("Pending");

        if (!valid) {
            throw new IllegalArgumentException(
                    "Status must be one of: Active, Upcoming, Completed, On Hold, Pending");
        }
        this.status = normalised;
    }

    public double getTargetHours() {
        return targetHours;
    }

    public double getHoursCompleted() {
        return hoursCompleted;
    }

    public double getHoursRemaining() {
        return Math.max(0.0, targetHours - hoursCompleted);
    }

    public double getCompletionPercentage() {
        if (targetHours <= 0) return 0.0;
        return Math.min(100.0, (hoursCompleted / targetHours) * 100.0);
    }

    public SensitiveInfo getSensitiveInfo() {
        return sensitiveInfo;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void addReview(Review review) {
        reviews.add(review);
    }

    public List<HoursLogEntry> getHoursLog() {
        return hoursLog;
    }

    /** Logs a day's hours and keeps the running completed-hours total in sync. */
    public void logHours(HoursLogEntry entry) {
        hoursLog.add(entry);
        if (entry.isSubmitted()) {
            this.hoursCompleted += entry.getHours();
        }
    }

    @Override
    public String toString() {
        return String.format(
                "%-8s %-20s %-25s %-16s %-10s %5.1f%% complete",
                studentId, getFullName(), college, mentorName == null ? "Unassigned" : mentorName,
                status, getCompletionPercentage()
        );
    }

    /** Full multi-line record view, used by the "view student details" menu option. */
    public String toDetailString() {
        return String.format(
                "Student ID:      %s%n" +
                "Name:            %s%n" +
                "College:         %s%n" +
                "Course:          %s%n" +
                "Mentor:          %s%n" +
                "Project:         %s%n" +
                "Status:          %s%n" +
                "Target hours:    %.1f%n" +
                "Hours completed: %.1f%n" +
                "Hours remaining: %.1f%n" +
                "Reviews logged:  %d%n" +
                "Hours log entries: %d",
                studentId, getFullName(), college, courseProgramme,
                mentorName == null ? "Unassigned" : mentorName,
                projectName == null ? "Unassigned" : projectName, status,
                targetHours, hoursCompleted, getHoursRemaining(),
                reviews.size(), hoursLog.size()
        );
    }

    /**
     * Sensitive / safeguarding-relevant data, deliberately separated from
     * the main Student fields so that a future role-based view can hide
     * this object from anyone other than an authorised coordinator.
     */
    public static class SensitiveInfo {
        private String healthConditions = "None";
        private String dietaryRequirements = "None";
        private String safeguardingNotes = "None";

        public String getHealthConditions() {
            return healthConditions;
        }

        public void setHealthConditions(String healthConditions) {
            this.healthConditions = healthConditions;
        }

        public String getDietaryRequirements() {
            return dietaryRequirements;
        }

        public void setDietaryRequirements(String dietaryRequirements) {
            this.dietaryRequirements = dietaryRequirements;
        }

        public String getSafeguardingNotes() {
            return safeguardingNotes;
        }

        public void setSafeguardingNotes(String safeguardingNotes) {
            this.safeguardingNotes = safeguardingNotes;
        }
    }
}
