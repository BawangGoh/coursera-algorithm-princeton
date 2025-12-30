/* *****************************************************************************
 *  Name:              Dennis Goh Jia Wang
 *  Last modified:     28/12/2025
 *  Interview Question: Elementary Sorts (Quiz)
 **************************************************************************** */

/* *****************************************************************************
 * Intersection of two sets:
 * Given two arrays a[] and b[], each containing n distinct 2D points in the
 * plane, design a subquadratic algorithm to count the number of points that are
 * contained both in array a[] and array b[].
 **************************************************************************** */
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Shell;
import edu.princeton.cs.algs4.StdOut;
import java.util.HashMap;
import java.util.Map;

public class Permutation {
    /* *************************************************************************
     * Two integer arrays a[] and b[] are permutations of each other iff they
     * contain the exact elements (order doesn't matter). There are two approach
     * to solve this problem with subquadratic algorithm better than O(n²):
     * 1) Sorting + Compare
     * 2) Hash Counting
     * -------------------------------------------------------------------------
     * Sorting + Compare approach:
     * 1) Sort both array a[] and b[]
     * 2) Check if they are identical element by element
     * -------------------------------------------------------------------------
     * e.g.
     * a[] = [(0, 1), (2, 4), (3, 7)]
     * b[] = [(1, 3), (1, 4), (3, 7)]
     * 1) Comparing: (0, 1) < (1, 3)
     * Y-order: 1 < 3 => require higher y-value from a[] => i++
     * 2) Comparing: (2, 4) > (1, 3)
     * Y-order: 4 > 3 => require higher y-value from b[] => j++
     * 3) Comparing (2, 4) > (1, 4)
     * Y-order: 4 = 4 => compare X-order
     * X-order: 2 > 1 => require higher x-value from b[] => j++
     * 4) Comparing: (3, 7) > (2, 4)
     * Y-order: 7 > 4 => require higher y-value from a[] => i++
     * 5) Comparing (3, 7) == (3, 7)
     * Y-order: 3 = 3 => compare X-order
     * X-order: 7 = 7 => count++, i++, j++
     ************************************************************************ */
    public static int countIntersectSort(Point2D[] a, Point2D[] b) {
        Shell.sort(a);
        Shell.sort(b);
        int i = 0, j = 0, count = 0;

        // Using Point2D natural order that order points by y-value, then by
        // x-value (lexicographic on (y, x))
        while (i < a.length && j < b.length) {
            int cmp = a[i].compareTo(b[j]);
            if (cmp == 0) {
                count++;
                i++;
                j++;
            } else if (cmp < 0) {
                i++;
            } else {
                j++;
            }
        }
        return count;
    }

    /* *************************************************************************
     * Hashing approach:
     * 1) Insert all 2D points from array a[] into a hash set
     * 2) Scan array b[], check if 2D points already existed in the hash set
     * -------------------------------------------------------------------------
     * e.g.
     * a[] = [(0, 1), (2, 4), (3, 7)]
     * b[] = [(1, 3), (1, 4), (3, 7)]
     * 1) Push a[] into Set{}
     * Set{(0, 1), (2, 4), (3, 7)}
     * 2) Scan each 2D points
     * (3, 7) point are found
     ************************************************************************ */
    public static int countIntersectHash(Point2D[] a, Point2D[] b) {
        // Pre-sizing the set to avoid rehashes (resize) to a larget capacity
        // Default load factor was 0.75 (3/4) => rehashes when reach 3rd element
        Set<Point2D> set = new HashSet<>(a.length * 2);
        int count = 0;

        // Insert all a[] points into set
        for (Point2D p : a) set.add(p);

        // Check if points exist in a[]
        for (Point2D q : b) {
            if (set.contains(q)) count++;
        }
        return count;
    }

    public static void main(String[] args) {
        // Initialize array of random 2D points
        Point2D[] A = {new Point2D(0, 1), new Point2D(2, 4), new Point2D(3, 7)};
        Point2D[] B = {new Point2D(1, 3), new Point2D(1, 4), new Point2D(3, 7)};
        StdOut.println("Sorting + Two Pointers approach");
        StdOut.println("Number of intersection points: " + countIntersectSort(A, B));
        StdOut.println("Hashing approach");
        StdOut.println("Number of intersection points: " + countIntersectHash(A, B));
    }
}
