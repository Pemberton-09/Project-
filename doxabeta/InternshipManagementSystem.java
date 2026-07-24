package doxabeta;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Central service class for the internship management prototype.
 *
 * Keeping this logic separate from Main (the console UI) follows the
 * single-responsibility principle: this class owns the data and the
 * business rules, Main only owns user interaction. That separation is
 * also what would let the same logic be reused behind a future Spring
 * Boot REST API without rewriting it (see "Future Improvements" in the
 * plan document).
 */
public class InternshipManagementSystem {

    public enum AddResult { SUCCESS, DUPLICATE_ID, INVALID_NAME }

    private final List<Student> students = new ArrayList<>();
    private final List<Mentor> mentors = new ArrayList<>();

    // ----- Student management -----

    public AddResult addStudent(Student student) {
        if (student.getFirstName() == null || student.getFirstName().isBlank()
                || student.getLastName() == null || student.getLastName().isBlank()) {
            return AddResult.INVALID_NAME;
        }
        if (findStudentById(student.getStudentId()).isPresent()) {
            return AddResult.DUPLICATE_ID;
        }
        students.add(student);
        return AddResult.SUCCESS;
    }

    public List<Student> getAllStudents() {
        return students;
    }

    public Optional<Student> findStudentById(String studentId) {
        return students.stream().filter(s -> s.getStudentId().equalsIgnoreCase(studentId)).findFirst();
    }

    public boolean removeStudent(String studentId) {
        Optional<Student> studentOpt = findStudentById(studentId);
        if (studentOpt.isEmpty()) {
            return false;
        }

        Student student = studentOpt.get();
        if (student.getMentorName() != null) {
            findMentorByName(student.getMentorName()).ifPresent(m -> m.unassignStudent(student.getStudentId()));
        }

        students.remove(student);
        return true;
    }

    public List<Student> getStudentsSortedByLastName() {
        return StudentSorter.sortByLastName(students);
    }

    /** Students grouped by college (colleges alphabetical), sorted by last name within each college. */
    public Map<String, List<Student>> getStudentsGroupedByCollege() {
        return StudentSorter.groupByCollegeThenLastName(students);
    }

    public List<Student> searchByStatus(String status) {
        List<Student> results = new ArrayList<>();
        for (Student s : students) {
            if (s.getStatus().equalsIgnoreCase(status.trim())) {
                results.add(s);
            }
        }
        return StudentSorter.sortByLastName(results);
    }

    public List<Student> searchByCollege(String collegeKeyword) {
        List<Student> results = new ArrayList<>();
        String needle = collegeKeyword.toLowerCase().trim();
        for (Student s : students) {
            if (s.getCollege().toLowerCase().contains(needle)) {
                results.add(s);
            }
        }
        return StudentSorter.sortByLastName(results);
    }

    public List<Student> getStudentsMissingMentor() {
        List<Student> results = new ArrayList<>();
        for (Student s : students) {
            if (s.getMentorName() == null || s.getMentorName().isBlank()) {
                results.add(s);
            }
        }
        return StudentSorter.sortByLastName(results);
    }

    // ----- Mentor management -----

    public void addMentor(Mentor mentor) {
        mentors.add(mentor);
    }

    public List<Mentor> getAllMentors() {
        return mentors;
    }

    public Optional<Mentor> findMentorByName(String name) {
        return mentors.stream().filter(m -> m.getName().equalsIgnoreCase(name.trim())).findFirst();
    }

    /**
     * Assigns a mentor to a student, updating both sides of the
     * relationship. Returns false if either the student or mentor
     * cannot be found.
     */
    public boolean assignMentor(String studentId, String mentorName) {
        Optional<Student> studentOpt = findStudentById(studentId);
        Optional<Mentor> mentorOpt = findMentorByName(mentorName);

        if (studentOpt.isEmpty() || mentorOpt.isEmpty()) {
            return false;
        }

        Student student = studentOpt.get();
        Mentor mentor = mentorOpt.get();

        if (student.getMentorName() != null) {
            findMentorByName(student.getMentorName()).ifPresent(old -> old.unassignStudent(studentId));
        }

        student.setMentorName(mentor.getName());
        mentor.assignStudent(studentId);
        return true;
    }

    // ----- Hours & reviews -----

    public boolean logHours(String studentId, HoursLogEntry entry) {
        Optional<Student> studentOpt = findStudentById(studentId);
        studentOpt.ifPresent(student -> student.logHours(entry));
        return studentOpt.isPresent();
    }

    public boolean addReview(String studentId, Review review) {
        Optional<Student> studentOpt = findStudentById(studentId);
        studentOpt.ifPresent(student -> student.addReview(review));
        return studentOpt.isPresent();
    }

    // ----- Reporting -----

    /** Produces the same kind of KPI summary as Doxabeta's "Dashboard Data" tab. */
    public String getDashboardSummary() {
        Map<String, Integer> statusCounts = new LinkedHashMap<>();
        double totalTarget = 0;
        double totalCompleted = 0;

        for (Student s : students) {
            statusCounts.merge(s.getStatus(), 1, Integer::sum);
            totalTarget += s.getTargetHours();
            totalCompleted += s.getHoursCompleted();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Total students: ").append(students.size()).append("\n");
        for (Map.Entry<String, Integer> entry : statusCounts.entrySet()) {
            sb.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        sb.append(String.format("Total target hours: %.1f%n", totalTarget));
        sb.append(String.format("Total hours completed: %.1f%n", totalCompleted));
        if (totalTarget > 0) {
            sb.append(String.format("Overall completion: %.1f%%%n", (totalCompleted / totalTarget) * 100));
        }
        sb.append("Students missing a mentor: ").append(getStudentsMissingMentor().size()).append("\n");

        long overAllocated = mentors.stream().filter(Mentor::isOverAllocated).count();
        sb.append("Mentors over-allocated: ").append(overAllocated).append("\n");

        return sb.toString();
    }
}
