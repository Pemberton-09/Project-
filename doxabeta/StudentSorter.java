package doxabeta;

import java.util.List;

/**
 * Sorting utility for Student records.
 *
 * INCOMPLETE-PROTOTYPE NOTE:
 * This version uses a simple Bubble Sort. It is easy to explain and verify
 * by hand, which suits a mid-point demonstration, but it runs in O(n^2)
 * time. The complete prototype replaces this with a Merge Sort (O(n log n))
 * once the dataset size and feature set have grown — see the "Two-Version
 * Prototype Strategy" section of the plan document for the justification.
 */
public class StudentSorter {

    /**
     * Sorts the given list of students in place, alphabetically by last
     * name (case-insensitive). If two students share a last name, first
     * name is used as a tie-breaker.
     */
    public static void sortByLastName(List<Student> students) {
        int n = students.size();
        boolean swapped;

        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                Student a = students.get(j);
                Student b = students.get(j + 1);

                if (compareByLastThenFirst(a, b) > 0) {
                    students.set(j, b);
                    students.set(j + 1, a);
                    swapped = true;
                }
            }
            // Small optimisation: stop early if a full pass made no swaps.
            if (!swapped) {
                break;
            }
        }
    }

    private static int compareByLastThenFirst(Student a, Student b) {
        int lastNameCompare = a.getLastName().compareToIgnoreCase(b.getLastName());
        if (lastNameCompare != 0) {
            return lastNameCompare;
        }
        return a.getFirstName().compareToIgnoreCase(b.getFirstName());
    }
}
