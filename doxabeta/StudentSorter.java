package doxabeta;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Sorting utility for Student records.
 *
 * COMPLETE-PROTOTYPE NOTE:
 * The incomplete prototype used Bubble Sort (O(n^2)) because it was easy
 * to explain against a handful of records. Now that the system carries a
 * fuller record per student (reviews, hours log, sensitive info) and is
 * intended to represent Doxabeta's real cohort sizes, this version uses
 * Merge Sort - a divide-and-conquer algorithm with guaranteed O(n log n)
 * time complexity - so sorting stays fast as the number of students
 * grows. It is also a *stable* sort, meaning students with identical
 * last names keep their original relative order unless a first-name
 * tie-break is applied, which matters if the list was previously ordered
 * by, e.g., start date.
 */
public class StudentSorter {

    /** Returns a new, sorted list; does not modify the input list. */
    public static List<Student> sortByLastName(List<Student> students) {
        List<Student> copy = new ArrayList<>(students);
        if (copy.size() <= 1) {
            return copy;
        }
        return mergeSort(copy);
    }

    /**
     * Groups students by college (colleges in alphabetical order), with each
     * college's students sorted alphabetically by last name (then first
     * name as a tie-break), using the same merge sort as sortByLastName.
     * Returns a LinkedHashMap so callers can iterate colleges in order.
     */
    public static Map<String, List<Student>> groupByCollegeThenLastName(List<Student> students) {
        // TreeMap buckets colleges alphabetically (case-insensitive) as students are added.
        Map<String, List<Student>> byCollege = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        for (Student s : students) {
            String college = (s.getCollege() == null || s.getCollege().isBlank())
                    ? "Unspecified College"
                    : s.getCollege();
            byCollege.computeIfAbsent(college, k -> new ArrayList<>()).add(s);
        }

        Map<String, List<Student>> result = new LinkedHashMap<>();
        for (Map.Entry<String, List<Student>> entry : byCollege.entrySet()) {
            result.put(entry.getKey(), sortByLastName(entry.getValue()));
        }
        return result;
    }

    private static List<Student> mergeSort(List<Student> list) {
        if (list.size() <= 1) {
            return list;
        }

        int mid = list.size() / 2;
        List<Student> left = mergeSort(new ArrayList<>(list.subList(0, mid)));
        List<Student> right = mergeSort(new ArrayList<>(list.subList(mid, list.size())));

        return merge(left, right);
    }

    private static List<Student> merge(List<Student> left, List<Student> right) {
        List<Student> merged = new ArrayList<>(left.size() + right.size());
        int i = 0;
        int j = 0;

        while (i < left.size() && j < right.size()) {
            Student a = left.get(i);
            Student b = right.get(j);

            if (compareByLastThenFirst(a, b) <= 0) {
                merged.add(a);
                i++;
            } else {
                merged.add(b);
                j++;
            }
        }

        while (i < left.size()) {
            merged.add(left.get(i++));
        }
        while (j < right.size()) {
            merged.add(right.get(j++));
        }

        return merged;
    }

    private static int compareByLastThenFirst(Student a, Student b) {
        int lastNameCompare = a.getLastName().compareToIgnoreCase(b.getLastName());
        if (lastNameCompare != 0) {
            return lastNameCompare;
        }
        return a.getFirstName().compareToIgnoreCase(b.getFirstName());
    }
}
