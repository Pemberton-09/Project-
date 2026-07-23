package doxabeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a mentor who can be assigned to students.
 *
 * UPDATE (second pass): the first version of this class only stored
 * name/email/role. Once "assign a mentor" was added to the menu it became
 * obvious mentors needed to track who they're currently assigned to,
 * otherwise there was no way to tell how many students a mentor already
 * had, or to build the "students missing a mentor" report. That capacity
 * tracking is kept simple here; the complete prototype uses it to flag
 * over-allocation against hours/week as well.
 */
public class Mentor {

    private final String name;
    private final String email;
    private final String role;
    private final List<String> assignedStudentIds = new ArrayList<>();

    public Mentor(String name, String email, String role) {
        this.name = name;
        this.email = email;
        this.role = role;
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

    @Override
    public String toString() {
        return String.format("%-20s %-30s %-25s %d student(s) currently assigned",
                name, email, role, getCurrentStudentCount());
    }
}
