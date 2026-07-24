package doxabeta;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Doxabeta Internship Management System - COMPLETE PROTOTYPE
 *
 * Covers the full requirement set from the plan document: student CRUD,
 * mentor assignment with capacity awareness, hours logging, mid/final
 * reviews, search/filter, a KPI dashboard, and last-name sorting (now
 * via Merge Sort - see StudentSorter for the justification).
 */
public class Main {

    private static final InternshipManagementSystem system = new InternshipManagementSystem();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        seedSampleData();
        boolean running = true;

        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> addStudent();
                case "2" -> viewAllStudentsSortedByLastName();
                case "3" -> assignMentor();
                case "4" -> logHours();
                case "5" -> addReview();
                case "6" -> searchStudents();
                case "7" -> viewStudentsMissingMentor();
                case "8" -> viewMentors();
                case "9" -> viewDashboard();
                case "10" -> removeStudent();
                case "11" -> viewStudentDetails();
                case "12" -> updateStudentStatus();
                case "13" -> running = false;
                default -> System.out.println("Please choose a valid option (1-13).\n");
            }
        }

        System.out.println("Goodbye!");
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("=========================================");
        System.out.println(" DOXABETA INTERNSHIP MANAGEMENT SYSTEM");
        System.out.println(" (Complete Prototype - Final Presentation)");
        System.out.printf(" %d student(s), %d mentor(s) currently loaded%n",
                system.getAllStudents().size(), system.getAllMentors().size());
        System.out.println("=========================================");
        System.out.println(" 1. Add a new student");
        System.out.println(" 2. View all students (grouped by college, sorted by last name)");
        System.out.println(" 3. Assign a mentor to a student");
        System.out.println(" 4. Log placement hours for a student");
        System.out.println(" 5. Add a review for a student");
        System.out.println(" 6. Search students (by status or college)");
        System.out.println(" 7. View students missing a mentor");
        System.out.println(" 8. View mentors & capacity");
        System.out.println(" 9. View KPI dashboard");
        System.out.println("10. Remove a student");
        System.out.println("11. View full details for one student");
        System.out.println("12. Update a student's status");
        System.out.println("13. Exit");
        System.out.print("Choose an option: ");
    }

    private static void addStudent() {
        System.out.print("Student ID (e.g. DCA-026): ");
        String id = scanner.nextLine().trim();
        if (id.isEmpty()) {
            System.out.println("Student ID can't be empty - cancelled.\n");
            return;
        }

        System.out.print("First name: ");
        String first = scanner.nextLine().trim();
        System.out.print("Last name: ");
        String last = scanner.nextLine().trim();
        System.out.print("College: ");
        String college = scanner.nextLine().trim();
        System.out.print("Course/Programme: ");
        String course = scanner.nextLine().trim();
        System.out.print("Project name: ");
        String project = scanner.nextLine().trim();
        System.out.print("Target hours: ");
        double target = parseDoubleSafe(scanner.nextLine().trim());

        Student student = new Student(id, first, last, college, course, null, project, "Active", target, 0.0);
        InternshipManagementSystem.AddResult result = system.addStudent(student);

        switch (result) {
            case SUCCESS -> System.out.println("Student added. No mentor assigned yet - use option 3.\n");
            case DUPLICATE_ID -> System.out.println("A student with ID " + id + " already exists - not added.\n");
            case INVALID_NAME -> System.out.println("First and last name can't be empty - not added.\n");
        }
    }

    private static void viewAllStudentsSortedByLastName() {
        Map<String, List<Student>> grouped = system.getStudentsGroupedByCollege();

        if (grouped.isEmpty()) {
            System.out.println("No matching students.\n");
            return;
        }

        for (Map.Entry<String, List<Student>> entry : grouped.entrySet()) {
            System.out.println("\n=== " + entry.getKey() + " ===");
            printStudentTable(entry.getValue());
        }
    }

    private static void viewStudentDetails() {
        System.out.print("Student ID: ");
        String id = scanner.nextLine().trim();
        var studentOpt = system.findStudentById(id);

        if (studentOpt.isEmpty()) {
            System.out.println("Could not find that student.\n");
            return;
        }

        Student s = studentOpt.get();
        System.out.println();
        System.out.println(s.toDetailString());

        if (!s.getReviews().isEmpty()) {
            System.out.println("\nReviews:");
            s.getReviews().forEach(r -> System.out.println("  " + r));
        }
        if (!s.getHoursLog().isEmpty()) {
            System.out.println("\nHours log:");
            s.getHoursLog().forEach(h -> System.out.println("  " + h));
        }
        System.out.println();
    }

    private static void assignMentor() {
        System.out.print("Student ID: ");
        String studentId = scanner.nextLine().trim();
        System.out.print("Mentor name (must already exist - see option 8): ");
        String mentorName = scanner.nextLine().trim();

        boolean ok = system.assignMentor(studentId, mentorName);
        System.out.println(ok ? "Mentor assigned.\n" : "Could not find that student or mentor.\n");
    }

    private static void logHours() {
        System.out.print("Student ID: ");
        String studentId = scanner.nextLine().trim();
        System.out.print("Hours worked today: ");
        double hours = parseDoubleSafe(scanner.nextLine().trim());
        System.out.print("Submitted? (y/n): ");
        boolean submitted = scanner.nextLine().trim().equalsIgnoreCase("y");

        HoursLogEntry entry = new HoursLogEntry(LocalDate.now(), hours, submitted);
        boolean ok = system.logHours(studentId, entry);
        System.out.println(ok ? "Hours logged.\n" : "Could not find that student.\n");
    }

    private static void addReview() {
        System.out.print("Student ID: ");
        String studentId = scanner.nextLine().trim();
        System.out.print("Review type (MID / FINAL / CHECK_IN): ");
        Review.Type type;
        try {
            type = Review.Type.valueOf(scanner.nextLine().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Unrecognised review type - defaulting to CHECK_IN.");
            type = Review.Type.CHECK_IN;
        }
        System.out.print("Reviewer name: ");
        String reviewer = scanner.nextLine().trim();
        int attendance = parseIntSafe(promptRating("Attendance"));
        int progress = parseIntSafe(promptRating("Progress"));
        int quality = parseIntSafe(promptRating("Work quality"));
        int communication = parseIntSafe(promptRating("Communication"));
        System.out.print("Notes: ");
        String notes = scanner.nextLine().trim();

        Review review = new Review(type, reviewer, attendance, progress, quality, communication, notes);
        boolean ok = system.addReview(studentId, review);
        System.out.println(ok ? "Review added.\n" : "Could not find that student.\n");
    }

    private static String promptRating(String label) {
        System.out.print(label + " rating (1-5): ");
        return scanner.nextLine().trim();
    }

    private static void updateStudentStatus() {
        System.out.print("Student ID: ");
        String studentId = scanner.nextLine().trim();
        var studentOpt = system.findStudentById(studentId);

        if (studentOpt.isEmpty()) {
            System.out.println("Could not find that student.\n");
            return;
        }

        System.out.print("New status (Active / Upcoming / Completed / On Hold / Pending): ");
        String status = scanner.nextLine().trim();

        try {
            studentOpt.get().setStatus(status);
            System.out.println("Status updated.\n");
        } catch (IllegalArgumentException e) {
            System.out.println("Not a valid status - nothing changed. (" + e.getMessage() + ")\n");
        }
    }

    private static void searchStudents() {
        System.out.println("Search by: 1) Status  2) College");
        String mode = scanner.nextLine().trim();

        if (mode.equals("1")) {
            System.out.print("Status (Active / Upcoming / Completed / On Hold / Pending): ");
            String status = scanner.nextLine().trim();
            printStudentTable(system.searchByStatus(status));
        } else if (mode.equals("2")) {
            System.out.print("College name (or part of it): ");
            String college = scanner.nextLine().trim();
            printStudentTable(system.searchByCollege(college));
        } else {
            System.out.println("Not a valid option.\n");
        }
    }

    private static void viewStudentsMissingMentor() {
        printStudentTable(system.getStudentsMissingMentor());
    }

    private static void viewMentors() {
        List<Mentor> mentors = system.getAllMentors();
        if (mentors.isEmpty()) {
            System.out.println("No mentors recorded yet.\n");
            return;
        }
        System.out.println();
        for (Mentor m : mentors) {
            System.out.println(m);
        }
        System.out.println();
    }

    private static void viewDashboard() {
        System.out.println("\n--- KPI DASHBOARD ---");
        System.out.println(system.getDashboardSummary());
    }

    private static void removeStudent() {
        System.out.print("Student ID to remove: ");
        String id = scanner.nextLine().trim();
        boolean ok = system.removeStudent(id);
        System.out.println(ok ? "Student removed.\n" : "Could not find that student.\n");
    }

    private static void printStudentTable(List<Student> studentList) {
        if (studentList.isEmpty()) {
            System.out.println("No matching students.\n");
            return;
        }
        System.out.println("\nID       Name                 College                   Mentor           Status     Completion");
        System.out.println("----------------------------------------------------------------------------------------------");
        for (Student s : studentList) {
            System.out.println(s);
        }
        System.out.println();
    }

    private static double parseDoubleSafe(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            System.out.println("(Could not read a number - defaulting to 0.0)");
            return 0.0;
        }
    }

    private static int parseIntSafe(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /** Seeds sample data reflecting the real shape of Doxabeta's workbook. */
    private static void seedSampleData() {
        system.addMentor(new Mentor("Solomon Quansah", "solomon@doxabeta.example.com", "Founder & Analyst", 6));
        system.addMentor(new Mentor("Daniela Oliveira", "daniela@doxabeta.example.com", "Strategic Operations Lead", 4));
        system.addMentor(new Mentor("Jessica Ogbeta", "jessica@doxabeta.example.com", "Employability & Skills Coach", 6));
        system.addMentor(new Mentor("Nana Appiah", "nana@doxabeta.example.com", "Developer / Technical Support", 2));
        system.addMentor(new Mentor("Tomi Balogun", "tomi@doxabeta.example.com", "Co-Founder", 6));

        Student s1 = new Student("DCA-016", "Sam", "King", "Hertford Regional College",
                "Digital Production, Design and Development (T-Level)", null, "Pathnexis / DCA Placement Project",
                "Active", 157.5, 52.5);
        Student s2 = new Student("DCA-019", "Morgan", "Davis", "St John Bosco College",
                "Digital Production, Design and Development (T-Level)", null, "Pathnexis / DCA Placement Project",
                "Active", 315.0, 142.5);
        Student s3 = new Student("DCA-020", "Benjamin", "Brown", "St John Bosco College",
                "Digital Production, Design and Development (T-Level)", null, "Pathnexis / DCA Placement Project",
                "Active", 157.5, 90.0);
        Student s4 = new Student("DCA-022", "Grace", "Foster", "St John Bosco College",
                "Digital Production, Design and Development (T-Level)", null, "Pathnexis / DCA Placement Project",
                "Active", 157.5, 90.0);
        Student s5 = new Student("DCA-025", "Morgan", "Hall", "St John Bosco College",
                "Digital Production, Design and Development (T-Level)", null, "Pathnexis / DCA Placement Project",
                "Active", 157.5, 52.5);

        system.addStudent(s1);
        system.addStudent(s2);
        system.addStudent(s3);
        system.addStudent(s4);
        system.addStudent(s5);

        system.assignMentor("DCA-016", "Daniela Oliveira");
        system.assignMentor("DCA-019", "Jessica Ogbeta");
        system.assignMentor("DCA-020", "Daniela Oliveira");
        system.assignMentor("DCA-022", "Tomi Balogun");
        // DCA-025 deliberately left unassigned to demonstrate option 7.

        system.addReview("DCA-016", new Review(Review.Type.MID, "Daniela Oliveira", 5, 4, 4, 5,
                "Settling in well, confident communicator."));
        system.logHours("DCA-016", new HoursLogEntry(LocalDate.now().minusDays(1), 6.0, true));
    }
}
