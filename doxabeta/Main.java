package doxabeta;

import java.util.List;
import java.util.Scanner;

/**
 * Doxabeta Internship Management System — INCOMPLETE PROTOTYPE
 *
 * UPDATE (second pass): mentor assignment, search, a "missing mentor"
 * report and status updates were added, and the business logic moved
 * into InternshipManagementSystem so this class only handles console I/O.
 *
 * UPDATE (third pass): added a "view student details" option, blank
 * first/last name validation on add, a startup self-check of the sorting
 * algorithm (see SelfCheck.java), and a live student/mentor count in the
 * menu banner. Reviews and hours logging are still deliberately left out
 * — flagged as "coming soon" rather than silently missing, since that's
 * genuinely the next planned step (Week 3 of the delivery plan).
 */
public class Main {

    private static final InternshipManagementSystem system = new InternshipManagementSystem();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        SelfCheck.runSortSelfCheck();
        seedSampleData();
        boolean running = true;

        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> addStudent();
                case "2" -> viewAllStudentsSortedByLastName();
                case "3" -> assignMentor();
                case "4" -> updateStudentStatus();
                case "5" -> searchStudents();
                case "6" -> viewStudentsMissingMentor();
                case "7" -> viewMentors();
                case "8" -> System.out.println("Hours logging is planned for the complete prototype (Week 3) — not built yet.\n");
                case "9" -> System.out.println("Reviews are planned for the complete prototype (Week 3) — not built yet.\n");
                case "10" -> removeStudent();
                case "11" -> viewStudentDetails();
                case "12" -> running = false;
                default -> System.out.println("Please choose a valid option (1-12).\n");
            }
        }

        System.out.println("Goodbye!");
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("=========================================");
        System.out.println(" DOXABETA INTERNSHIP MANAGEMENT SYSTEM");
        System.out.println(" (Incomplete Prototype - In Progress)");
        System.out.printf(" %d student(s), %d mentor(s) currently loaded%n",
                system.getAllStudents().size(), system.getAllMentors().size());
        System.out.println("=========================================");
        System.out.println(" 1. Add a new student");
        System.out.println(" 2. View all students (sorted by last name)");
        System.out.println(" 3. Assign a mentor to a student");
        System.out.println(" 4. Update a student's status");
        System.out.println(" 5. Search students (by status or college)");
        System.out.println(" 6. View students missing a mentor");
        System.out.println(" 7. View mentors");
        System.out.println(" 8. Log placement hours              [coming soon]");
        System.out.println(" 9. Add a review                     [coming soon]");
        System.out.println("10. Remove a student");
        System.out.println("11. View full details for one student");
        System.out.println("12. Exit");
        System.out.print("Choose an option: ");
    }

    private static void addStudent() {
        System.out.print("Student ID (e.g. DCA-026): ");
        String id = scanner.nextLine().trim();

        if (id.isEmpty()) {
            System.out.println("Student ID can't be empty — cancelled.\n");
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

        System.out.print("Target hours: ");
        double target = parseDoubleSafe(scanner.nextLine().trim());

        Student student = new Student(id, first, last, college, course, null, "Active", target, 0.0);
        InternshipManagementSystem.AddResult result = system.addStudent(student);

        switch (result) {
            case SUCCESS -> System.out.println("Student added. No mentor assigned yet — use option 3.\n");
            case DUPLICATE_ID -> System.out.println("A student with ID " + id + " already exists — not added.\n");
            case INVALID_NAME -> System.out.println("First and last name can't be empty — not added.\n");
        }
    }

    private static void viewAllStudentsSortedByLastName() {
        printStudentTable(system.getStudentsSortedByLastName());
    }

    private static void viewStudentDetails() {
        System.out.print("Student ID: ");
        String id = scanner.nextLine().trim();
        var studentOpt = system.findStudentById(id);

        if (studentOpt.isEmpty()) {
            System.out.println("Could not find that student.\n");
            return;
        }

        System.out.println();
        System.out.println(studentOpt.get().toDetailString());
        System.out.println();
    }

    private static void assignMentor() {
        System.out.print("Student ID: ");
        String studentId = scanner.nextLine().trim();
        System.out.print("Mentor name (must already exist — see option 7): ");
        String mentorName = scanner.nextLine().trim();

        boolean ok = system.assignMentor(studentId, mentorName);
        System.out.println(ok ? "Mentor assigned.\n" : "Could not find that student or mentor — check the ID and name and try again.\n");
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
            System.out.println("Not a valid status — nothing changed. (" + e.getMessage() + ")\n");
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
        System.out.println("\nID       Name                 College                   Mentor         Status     Hours");
        System.out.println("--------------------------------------------------------------------------------------------");
        for (Student s : studentList) {
            System.out.println(s);
        }
        System.out.println();
    }

    private static double parseDoubleSafe(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            System.out.println("(Could not read a number — defaulting to 0.0)");
            return 0.0;
        }
    }

    /** Seeds a small sample dataset based on Doxabeta's existing workbook. */
    private static void seedSampleData() {
        system.addMentor(new Mentor("Solomon Quansah", "solomon@doxabeta.example.com", "Founder & Analyst"));
        system.addMentor(new Mentor("Daniela Oliveira", "daniela@doxabeta.example.com", "Strategic Operations Lead"));
        system.addMentor(new Mentor("Jessica Ogbeta", "jessica@doxabeta.example.com", "Employability & Skills Coach"));
        system.addMentor(new Mentor("Nana Appiah", "nana@doxabeta.example.com", "Developer / Technical Support"));
        system.addMentor(new Mentor("Tomi Balogun", "tomi@doxabeta.example.com", "Co-Founder"));

        system.addStudent(new Student("DCA-016", "Sam", "King", "Hertford Regional College",
                "Digital Production, Design and Development (T-Level)", null, "Active", 157.5, 52.5));
        system.addStudent(new Student("DCA-019", "Morgan", "Davis", "St John Bosco College",
                "Digital Production, Design and Development (T-Level)", null, "Active", 315.0, 142.5));
        system.addStudent(new Student("DCA-020", "Benjamin", "Brown", "St John Bosco College",
                "Digital Production, Design and Development (T-Level)", null, "Active", 157.5, 90.0));
        system.addStudent(new Student("DCA-022", "Grace", "Foster", "St John Bosco College",
                "Digital Production, Design and Development (T-Level)", null, "Active", 157.5, 90.0));
        system.addStudent(new Student("DCA-025", "Morgan", "Hall", "St John Bosco College",
                "Digital Production, Design and Development (T-Level)", null, "Active", 157.5, 52.5));

        system.assignMentor("DCA-016", "Daniela Oliveira");
        system.assignMentor("DCA-019", "Jessica Ogbeta");
        system.assignMentor("DCA-020", "Daniela Oliveira");
        system.assignMentor("DCA-022", "Tomi Balogun");
        // DCA-025 deliberately left unassigned to demonstrate option 6.
    }
}
