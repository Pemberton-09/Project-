package doxabeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a mentor/team member who can be assigned to students.
 * Tracks capacity against hours/week so the system can flag
 * over-allocation, mirroring the "Hours Available / Week" vs
 * current-student-count columns used in Doxabeta's Mentors & Team tab.
 */
public class Mentor {

    private final String name;
    private final String email;
    private final String role;
    private final double hoursAvailablePerWeek;
    private final List<String> assignedStudentIds = new ArrayList<>();

    public Mentor(String name, String email, String role, double hoursAvailablePerWeek) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.hoursAvailablePerWeek = hoursAvailablePerWeek;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public double getHoursAvailablePerWeek() {
        return hoursAvailablePerWeek;
    }

    public List<String> getAssignedStudentIds() {
        return assignedStudentIds;
    }

    public void assignStudent(String studentId) {
        if (!assignedStudentIds.contains(studentId)) {
            assignedStudentIds.add(studentId);
        }
    }

    public void unassignStudent(String studentId) {
        assignedStudentIds.remove(studentId);
    }

    public int getCurrentStudentCount() {
        return assignedStudentIds.size();
    }

    /**
     * Very simple over-allocation check: assumes each assigned student
     * needs roughly 1 hour/week of mentor time. Flags the mentor if
     * their current headcount exceeds their stated availability. This is
     * a deliberately simple heuristic for the prototype, not a real
     * scheduling calculation.
     */
    public boolean isOverAllocated() {
        return getCurrentStudentCount() > hoursAvailablePerWeek;
    }

    @Override
    public String toString() {
        String flag = isOverAllocated() ? "  [OVER-ALLOCATED]" : "";
        return String.format("%-20s %-30s %-25s %2d student(s) | %.1f hrs/wk available%s",
                name, email, role, getCurrentStudentCount(), hoursAvailablePerWeek, flag);
    }
}
