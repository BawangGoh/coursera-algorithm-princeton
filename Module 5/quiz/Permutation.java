/* *****************************************************************************
 *  Name:              Dennis Goh Jia Wang
 *  Last modified:     28/12/2025
 *  Interview Question: Elementary Sorts (Quiz)
 **************************************************************************** */

/* *****************************************************************************
 * Permutation:
 * Given two integer arrays of size n, design a subquadratic algorithm to
 * determine whether one is a permutation of the other. That is, do they contain
 * exactly the same entries but, possibly, in a different order.
 **************************************************************************** */
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Shell;
import edu.princeton.cs.algs4.StdOut;
import java.util.HashMap;
import java.util.Map;

/* *****************************************************************************
 * Two integer arrays a[] and b[] are permutations of each other iff they
 * contain the exact elements (order doesn't matter). There are two approach
 * to solve this problem with subquadratic algorithm better than O(n²):
 * 1) Sorting + Compare
 * 2) Hash Counting
 **************************************************************************** */
public class Permutation {
    /* *************************************************************************
     * Need to implicitly upcast (autoboxing) from primitive int type to its
     * corresponding object wrapper class (int -> Integer):
     * 1) Sorting algorithm take Objects parameter (e.g. Comparable[] a)
     * 2) Objects classes provide methods e.g. equals(), compareTo(), etc
     * for easier comparisons
     ************************************************************************ */
    private static Integer[] box(int[] a) {
        Integer[] boxed = new Integer[a.length];

        // Loop through primitive array and copy element into Integer objects
        for (int i = 0; i < a.length; i++) {
            boxed[i] = a[i];
        }
        return boxed;
    }

    /* *************************************************************************
     * Sorting + Compare approach:
     * 1) Sort both array a[] and b[]
     * 2) Check if they are identical element by element
     * -------------------------------------------------------------------------
     * e.g.
     * a[] = [3, 1, 2, 2]
     * b[] = [2, 3, 1, 2]
     * 1) Sort a[] = [1, 2, 2, 3]
     * 2) Sort b[] = [1, 2, 2, 3]
     * 3) Check a[i] == b[i]
     ************************************************************************ */
    public static boolean isPermutateSort(int[] a, int[] b) {
        // Return false if either of the array length does not match
        if (a == null || b == null) return false;
        if (a.length != b.length) return false;

        Integer[] A = box(a);
        Integer[] B = box(b);
        Shell.sort(A);
        Shell.sort(B);

        // Check each elements
        for (int i = 0; i < A.length; i++) {
            if (!A[i].equals(B[i])) return false;
        }
        return true;
    }

    /* *************************************************************************
     * Hash Counting approach:
     * 1) Insert all a[] element into Hash Map
     * 2) Count frequencies from a, then decrement with b
     * 3) Check if any count goes negative or mismatches, not a permutation
     * -------------------------------------------------------------------------
     * e.g.
     * a[] = [3, 1, 2, 2]
     * b[] = [2, 3, 1, 2]
     * 1) a{} = {1: 1, 2: 2, 3: 1}
     * 2) Loop through b[] and get the keys for a{} mapping
     ************************************************************************ */
    public static boolean isPermutateHash(int[] a, int[] b) {
        // Pre-sizing the set to avoid rehashes (resize) to a larget capacity
        // Default load factor was 0.75 (3/4) => rehashes when reach 3rd element
        Map<Integer, Integer> freq = new HashMap<>(a.length * 2);

        // Insert all a[] points into hashmap using advance foreach loop
        // (primitive and object wrapper are able to use foreach)
        for (int key : a) {
            freq.put(key, freq.getOrDefault(key, 0) + 1);
        }

        // Loop through b[] to get key mapping
        for (int key : b) {
            Integer count = freq.get(key);

            // Element not found or mismatch (since remaining count is zero)
            if (count == null || count == 0) {
                return false;
            } else if (count == 1) {
                freq.remove(key);
            } else {
                freq.put(key, count - 1);
            }
        }

        // Check if hashmap is empty (suppose to be empty)
        return freq.isEmpty();
    }

    public static void main(String[] args) {
        // Initialize sample array
        int[] a = {3, 1, 2, 2};
        int[] b = {2, 3, 1, 2};
        StdOut.println("Sorting + Compare approach");
        StdOut.println("Permutable: " + isPermutateSort(a, b));
        StdOut.println("Hash Counting approach");
        StdOut.println("Permutable: " + isPermutateHash(a, b));
    }
}
