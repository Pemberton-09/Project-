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

    public void setStatus(String status) {
        this.status = status;
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
}
