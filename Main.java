package doxabeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Doxabeta Internship Management System — INCOMPLETE PROTOTYPE
 *
 * Purpose (for mid-point presentation): demonstrate the core Student /
 * Mentor domain model, a working console menu, and the last-name sorting
 * requirement, against a small in-memory dataset seeded from Doxabeta's
 * existing workbook.
 *
 * Deliberately NOT included yet (see complete prototype):
 *   - Reviews (mid / final)
 *   - Daily hours log entries
 *   - Cohort / college / project reporting
 *   - Input validation and error handling beyond the basics
 *   - Persistence (data resets each run)
 */
public class Main {

    private static final List<Student> students = new ArrayList<>();
    private static final List<Mentor> mentors = new ArrayList<>();
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
                case "3" -> viewMentors();
                case "4" -> running = false;
                default -> System.out.println("Please choose a valid option (1-4).\n");
            }
        }

        System.out.println("Goodbye!");
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("=========================================");
        System.out.println(" DOXABETA INTERNSHIP MANAGEMENT SYSTEM");
        System.out.println(" (Incomplete Prototype - Mid-point Demo)");
        System.out.println("=========================================");
        System.out.println("1. Add a new student");
        System.out.println("2. View all students (sorted by last name)");
        System.out.println("3. View mentors");
        System.out.println("4. Exit");
        System.out.print("Choose an option: ");
    }

    private static void addStudent() {
        System.out.print("Student ID (e.g. DCA-026): ");
        String id = scanner.nextLine().trim();

        System.out.print("First name: ");
        String first = scanner.nextLine().trim();

        System.out.print("Last name: ");
        String last = scanner.nextLine().trim();

        System.out.print("College: ");
        String college = scanner.nextLine().trim();

        System.out.print("Course/Programme: ");
        String course = scanner.nextLine().trim();

        System.out.print("Mentor name (leave blank if unassigned): ");
        String mentor = scanner.nextLine().trim();
        if (mentor.isEmpty()) {
            mentor = null;
        }

        System.out.print("Target hours: ");
        double target = parseDoubleSafe(scanner.nextLine().trim());

        Student student = new Student(id, first, last, college, course, mentor, "Active", target, 0.0);
        students.add(student);

        System.out.println("Student added.\n");
    }

    private static void viewAllStudentsSortedByLastName() {
        if (students.isEmpty()) {
            System.out.println("No students recorded yet.\n");
            return;
        }

        // Work on a copy so the sort doesn't reorder the master list
        // unexpectedly if this menu option is used repeatedly.
        List<Student> copy = new ArrayList<>(students);
        StudentSorter.sortByLastName(copy);

        System.out.println("\nID       Name                 College                   Mentor         Status     Hours");
        System.out.println("--------------------------------------------------------------------------------------------");
        for (Student s : copy) {
            System.out.println(s);
        }
        System.out.println();
    }

    private static void viewMentors() {
        if (mentors.isEmpty()) {
            System.out.println("No mentors recorded yet.\n");
            return;
        }
        System.out.println("\nName                 Email                          Role");
        System.out.println("---------------------------------------------------------------");
        for (Mentor m : mentors) {
            System.out.println(m);
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
        mentors.add(new Mentor("Solomon Quansah", "solomon@doxabeta.example.com", "Founder & Analyst"));
        mentors.add(new Mentor("Daniela Oliveira", "daniela@doxabeta.example.com", "Strategic Operations Lead"));
        mentors.add(new Mentor("Jessica Ogbeta", "jessica@doxabeta.example.com", "Employability & Skills Coach"));
        mentors.add(new Mentor("Nana Appiah", "nana@doxabeta.example.com", "Developer / Technical Support"));
        mentors.add(new Mentor("Tomi Balogun", "tomi@doxabeta.example.com", "Co-Founder"));

        students.add(new Student("DCA-016", "Sam", "King", "Hertford Regional College",
                "Digital Production, Design and Development (T-Level)", "Daniela Oliveira", "Active", 157.5, 52.5));
        students.add(new Student("DCA-019", "Morgan", "Davis", "St John Bosco College",
                "Digital Production, Design and Development (T-Level)", "Jessica Ogbeta", "Active", 315.0, 142.5));
        students.add(new Student("DCA-020", "Benjamin", "Brown", "St John Bosco College",
                "Digital Production, Design and Development (T-Level)", "Daniela Oliveira", "Active", 157.5, 90.0));
        students.add(new Student("DCA-022", "Grace", "Foster", "St John Bosco College",
                "Digital Production, Design and Development (T-Level)", "Tomi Balogun", "Active", 157.5, 90.0));
        students.add(new Student("DCA-025", "Morgan", "Hall", "St John Bosco College",
                "Digital Production, Design and Development (T-Level)", null, "Active", 157.5, 52.5));
    }
}
