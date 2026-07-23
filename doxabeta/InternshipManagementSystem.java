package doxabeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Holds the student/mentor data and the business rules that operate on it.
 *
 * UPDATE (second pass): the very first version of this prototype put all
 * of this logic directly inside Main, mixed in with System.out.println
 * calls. That got messy fast once mentor assignment and search were
 * added, so this class was pulled out to keep Main responsible only for
 * console interaction.
 *
 * UPDATE (third pass): added validation for blank first/last names on
 * addStudent, after realising the console layer only checked for a blank
 * ID and nothing else.
 */
public class InternshipManagementSystem {

    private final List<Student> students = new ArrayList<>();
    private final List<Mentor> mentors = new ArrayList<>();

    // ----- Student management -----

    /**
     * Adds a student, rejecting a duplicate student ID or a blank first
     * or last name. Returns an AddResult so the caller can show a
     * specific message rather than a generic failure.
     */
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

    public enum AddResult { SUCCESS, DUPLICATE_ID, INVALID_NAME }

    public List<Student> getAllStudents() {
        return students;
    }

    public Optional<Student> findStudentById(String studentId) {
        return students.stream()
                .filter(s -> s.getStudentId().equalsIgnoreCase(studentId))
                .findFirst();
    }

    public boolean removeStudent(String studentId) {
        return students.removeIf(s -> s.getStudentId().equalsIgnoreCase(studentId));
    }

    public List<Student> getStudentsSortedByLastName() {
        List<Student> copy = new ArrayList<>(students);
        StudentSorter.sortByLastName(copy);
        return copy;
    }

    public List<Student> searchByStatus(String status) {
        List<Student> results = new ArrayList<>();
        for (Student s : students) {
            if (s.getStatus().equalsIgnoreCase(status.trim())) {
                results.add(s);
            }
        }
        StudentSorter.sortByLastName(results);
        return results;
    }

    public List<Student> searchByCollege(String collegeKeyword) {
        List<Student> results = new ArrayList<>();
        String needle = collegeKeyword.toLowerCase().trim();
        for (Student s : students) {
            if (s.getCollege().toLowerCase().contains(needle)) {
                results.add(s);
            }
        }
        StudentSorter.sortByLastName(results);
        return results;
    }

    public List<Student> getStudentsMissingMentor() {
        List<Student> results = new ArrayList<>();
        for (Student s : students) {
            if (s.getMentorName() == null || s.getMentorName().isBlank()) {
                results.add(s);
            }
        }
        StudentSorter.sortByLastName(results);
        return results;
    }

    // ----- Mentor management -----

    public void addMentor(Mentor mentor) {
        mentors.add(mentor);
    }

    public List<Mentor> getAllMentors() {
        return mentors;
    }

    public Optional<Mentor> findMentorByName(String name) {
        return mentors.stream()
                .filter(m -> m.getName().equalsIgnoreCase(name.trim()))
                .findFirst();
    }

    /**
     * Assigns a mentor to a student. If the student already had a
     * different mentor, that mentor's slot is freed first so counts
     * stay accurate.
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
}
