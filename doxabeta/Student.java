package doxabeta;

/**
 * Represents a single internship student record.
 *
 * This is intentionally a *simple* domain object for the incomplete
 * (mid-point) prototype. It covers only the fields needed to demonstrate
 * the core data model and the last-name sorting requirement. Fields such
 * as reviews, daily hours logs and safeguarding notes are added in the
 * complete prototype (see Section 7 of the Research & Delivery Plan).
 */
public class Student {

    private final String studentId;
    private final String firstName;
    private final String lastName;
    private final String college;
    private final String courseProgramme;
    private String mentorName;
    private String status;
    private double targetHours;
    private double hoursCompleted;

    public Student(String studentId, String firstName, String lastName, String college,
                   String courseProgramme, String mentorName, String status,
                   double targetHours, double hoursCompleted) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.college = college;
        this.courseProgramme = courseProgramme;
        this.mentorName = mentorName;
        this.status = status;
        this.targetHours = targetHours;
        this.hoursCompleted = hoursCompleted;
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

    public String getStatus() {
        return status;
    }

    /**
     * Updates the student's status, restricted to the values Doxabeta
     * already uses (Active, Upcoming, Completed, On Hold, Pending).
     * Added after the first pass of this prototype once it became clear
     * free-text status values would break the status-based reporting
     * planned for the complete prototype.
     */
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
        return targetHours - hoursCompleted;
    }

    @Override
    public String toString() {
        return String.format(
                "%-8s %-20s %-25s %-14s %-10s %6.1f/%6.1f hrs",
                studentId, getFullName(), college, mentorName == null ? "Unassigned" : mentorName,
                status, hoursCompleted, targetHours
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
                "Status:          %s%n" +
                "Target hours:    %.1f%n" +
                "Hours completed: %.1f%n" +
                "Hours remaining: %.1f",
                studentId, getFullName(), college, courseProgramme,
                mentorName == null ? "Unassigned" : mentorName, status,
                targetHours, hoursCompleted, getHoursRemaining()
        );
    }
}
