/* *****************************************************************************
 *  Name:              Dennis Goh Jia Wang
 *  Last modified:     24/9/2025
 *  Interview Question: Analysis of Algorithms (Quiz)
 **************************************************************************** */

/* *****************************************************************************
 * 3-SUM in quadratic time:
 * Design an algorithm for the 3-SUM problem that takes time proportional to n^2
 * in the worst case. You may assume that you can sort the n integers in time
 * proportional to n^2 or better.
 **************************************************************************** */

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stopwatch;

// Brute force and binary search approaches are already available in algs4 package
// import edu.princeton.cs.algs4.ThreeSum;
// import edu.princeton.cs.algs4.ThreeSumFast;

/* *****************************************************************************
 * Solution
 * There are 4 methods to solve the 3-SUM problem:
 * 1. Brute force method (Nested for loops)
 * 2. Binary search method
 * 3. Two pointer method
 * 4. HashMap method
 **************************************************************************** */
public class ThreeSum {
    /**
     * Brute force method: Nested for loop O(n^3)
     * Returns the number of triples (i, j, k) with {i < j < k}
     * such that {a[i] + a[j] + a[k] == 0}.
     *
     * @param  a the array of integers
     * @return the number of triples (i, j, k) with {i < j < k}
     *         such that {a[i] + a[j] + a[k] == 0}
     */
    public static int countBrute(int[] a) {
        int n = a.length;
        int count = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i+1; j < n; j++) {
                for (int k = j+1; k < n; k++) {
                    if (a[i] + a[j] + a[k] == 0) {
                        StdOut.println(a[i] + " " + a[j] + " " + a[k]);
                        count++;
                    }
                }
            }
        }
        return count;
    }

    /**
     * Binary search method: O(n^2logn)
     * Returns the number of triples (i, j, k) with {i < j < k}
     * such that {a[i] + a[j] + a[k] == 0}.
     *
     * @param a the array of integers (need to be sorted)
     * @return the number of triples (i, j, k) with {i < j < k}
     * such that {a[i] + a[j] + a[k] == 0}
     */
    /* *****************************************************************************
     * Binary search
     * (-40, -20)   60 (not found)
     * (-40, -10)   50 (not found)
     * (-40, 0)     40 (found)
     * ...
     **************************************************************************** */
    public static int countBinary(int[] a) {
        int n = a.length;
        int count = 0;
        Arrays.sort(a);

        // Avoid duplicated triplets
        if (containsDuplicates(a)) throw new IllegalArgumentException("array contains duplicate integers");

        // First element greater than 0
        if (a[0] > 0) throw new IllegalArgumentException("sum is always greater than 0");

        for (int i = 0; i < n; i++) {
            for (int j = i+1; j < n; j++) {
                int k = binarySearch(a, -(a[i] + a[j]));
                if (k > j) {
                    StdOut.println(a[i] + " " + a[j] + " " + a[k]);
                    count++;
                }
            }
        }
        return count;
    }

    // returns true if the sorted array a[] contains any duplicated integers
    private static boolean containsDuplicates(int[] a) {
        for (int i = 1; i < a.length; i++)
            if (a[i] == a[i-1]) return true;
        return false;
    }

    public static int binarySearch(int[] a, int key) {
        int lo = 0;
        int hi = a.length - 1;

        // Split the array into half
        while (lo <= hi) {
            int mid = lo + (hi - lo) /2;    // reason of (hi -lo) because of subarray
            if (key < a[mid]) hi = mid - 1;
            else if (key > a[mid]) lo = mid + 1;
            else return mid;
        }
        return -1;
    }

    /**
     * Two pointers method: O(n^2)
     * Returns the number of triples (i, j, k) with {i < j < k}
     * such that {a[i] + a[j] + a[k] == 0}.
     *
     * @param a the array of integers (need to be sorted)
     * @return the number of triples (i, j, k) with {i < j < k}
     * such that {a[i] + a[j] + a[k] == 0}
     */
    /* *****************************************************************************
     * Two pointers
     * -40 -20 -10 0 5 10 30 40
     * (*) (^)               (^)
     * target = -(-40) = 40
     * current = first pointer + last pointer = -20-40 = -60
     * current < target -> first pointer ++
     **************************************************************************** */
    public static int countPointers (int[] a){
        int n = a.length;
        int count = 0;
        Arrays.sort(a);

        // Avoid duplicated triplets
        if (containsDuplicates(a)) throw new IllegalArgumentException("array contains duplicate integers");

        // First element greater than 0
        if (a[0] > 0) throw new IllegalArgumentException("sum is always greater than 0");

        // Iterate outer loop up to n - 2 to ensure at least two element for two-pointer
        for (int i = 0; i < n - 2; i++) {
            int first = i + 1;
            int last = n - 1;

            // Keep searching for valid sum if first < last, otherwise last > first mean no valid triplets are found
            // Case 1: current < target mean it required bigger sum to achieve 0
            // Case 2: current > target mean it need to lower valud to achieve 0
            // Case 3: current = target mean it found triplets, move both pointers to find other pairs
            while (first < last) {
                if (a[first] + a[last] < -a[i]) first++;
                else if (a[first] + a[last] > -a[i]) last--;
                else {
                    StdOut.println(a[i] + " " + a[first] + " " + a[last]);
                    count++;
                    first++;
                    last--;
                }
            }
        }
        return count;
    }

    /**
     * Hash mapping method: O(n^2)
     * Returns the number of triples (i, j, k) with {i < j < k}
     * such that {a[i] + a[j] + a[k] == 0}.
     *
     * @param a the array of integers (need to be sorted)
     * @return the number of triples (i, j, k) with {i < j < k}
     * such that {a[i] + a[j] + a[k] == 0}
     */
    /* *****************************************************************************
     * Hash Map
     * -40 -20 -10 0 5 10 30 40
     * a[i] a[j] a[k]  ...
     * a[i] + a[j] + a[k] = 0
     * First iteration: fix a
     * a[j] + a[k] = -a[i]
     * Second iteration: fix b
     * a[k] = -a[i]-a[j]
     * if hasKey(-a[i]-a[j]), triplets found, otherwise store current a[k] in hash
     * in future, a[k] = -a[i]-a[j] such that hash find a[k] in O(1) time and will
     * return triplet since a[k] is subset of array a
     **************************************************************************** */
    public static int countHash(int[] a) {
        int n = a.length;
        int count = 0;
        Arrays.sort(a);

        // Avoid duplicated triplets
        if (containsDuplicates(a)) throw new IllegalArgumentException("array contains duplicate integers");

        // First element greater than 0
        if (a[0] > 0) throw new IllegalArgumentException("sum is always greater than 0");

        // Iterate outer loop up to n - 2 to ensure at least a pairs for sum
        for (int i = 0; i < n-2; i++) {
            HashMap<Integer, Integer> map = new HashMap<>();

            // Inner loop iterates from i+1 to n-1
            for (int j = i+1; j < n; j++) {
                // Check if complement exist in map
                if (map.containsKey(-a[i]-a[j])) {
                    StdOut.println(a[i] + " " + a[j] + " " + (-a[i]-a[j]));
                    count++;
                }

                // Add current element for future lookups since triplets is subset of the array
                map.put(a[j], j);
            }
        }
        return count;
    }

    // Test the elapsed time for three sum approach
    public static void main(String[] args) {
        In in = new In(args[0]);
        int[] a = in.readAllInts();

        // Calculate time elapsed for brute force triple nested loop
        Stopwatch sw_brute = new Stopwatch();
        int countBF = countBrute(a);
        double timeBF = sw_brute.elapsedTime();
        StdOut.println("Brute force for three sums to 0: " + countBF);
        StdOut.println("Three sums brute force elapsed time: " + timeBF);

        // Calculate time elapsed for binary search
        Stopwatch sw_binary = new Stopwatch();
        int countBin = countBinary(a);
        double timeBin = sw_binary.elapsedTime();
        StdOut.println("Binary search for three sums to 0: " + countBin);
        StdOut.println("Three sums binary search elapsed time: " + timeBin);

        // Calculate time elapsed for two pointers
        Stopwatch sw_tp = new Stopwatch();
        int countTP = countPointers(a);
        double timeTP = sw_tp.elapsedTime();
        StdOut.println("Two pointers for three sums to 0: " + countTP);
        StdOut.println("Three sums two pointers elapsed time: " + timeTP);

        // Calculate time elapsed for hash mapping
        Stopwatch sw_hm = new Stopwatch();
        int countHM = countHash(a);
        double timeHM = sw_hm.elapsedTime();
        StdOut.println("Hash mapping for three sums to 0: " + countHM);
        StdOut.println("Three sum hash mapping elapsed time: " + timeHM);
    }
}
