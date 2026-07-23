package doxabeta;

import java.util.ArrayList;
import java.util.List;

/**
 * A lightweight, dependency-free self-check run once at startup.
 *
 * UPDATE (third pass): after writing the formal test plan and running it
 * manually a few times, it became clear it would be easy to accidentally
 * break the sorting algorithm during later changes without noticing until
 * the next manual test pass. This class runs a small, fixed check of
 * StudentSorter every time the app starts, using assert-style comparisons
 * rather than a full testing framework (no external dependencies are
 * available in this environment). It is not a replacement for the formal
 * test plan — it only guards the one behaviour that would be most obvious
 * to break by accident: sorting order.
 */
public class SelfCheck {

    /**
     * Runs the check and returns true if it passes. Prints a short
     * message either way so it's visible on startup without being noisy.
     */
    public static boolean runSortSelfCheck() {
        List<Student> sample = new ArrayList<>();
        sample.add(new Student("T-1", "Zoe", "Turner", "Test College", "Test Course", null, "Active", 100, 0));
        sample.add(new Student("T-2", "Amy", "Baker", "Test College", "Test Course", null, "Active", 100, 0));
        sample.add(new Student("T-3", "Sam", "Baker", "Test College", "Test Course", null, "Active", 100, 0));
        sample.add(new Student("T-4", "Leo", "Miller", "Test College", "Test Course", null, "Active", 100, 0));

        StudentSorter.sortByLastName(sample);

        boolean orderCorrect =
                sample.get(0).getLastName().equals("Baker") && sample.get(0).getFirstName().equals("Amy")
                        && sample.get(1).getLastName().equals("Baker") && sample.get(1).getFirstName().equals("Sam")
                        && sample.get(2).getLastName().equals("Miller")
                        && sample.get(3).getLastName().equals("Turner");

        if (orderCorrect) {
            System.out.println("[Self-check] Sorting algorithm OK (surnames ordered, first-name tie-break correct).\n");
        } else {
            System.out.println("[Self-check] WARNING: sorting algorithm did not produce the expected order. See StudentSorter.\n");
        }

        return orderCorrect;
    }
}
