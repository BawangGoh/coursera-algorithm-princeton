/* *****************************************************************************
 *  Name:              Dennis Goh Jia Wang
 *  Last modified:     24/9/2025
 **************************************************************************** */

/* *****************************************************************************
 * Experimenting the differences between Fast Three Sum (binary search vs Brute
 * Force Three Sum (Nested triple loops)
 **************************************************************************** */

import java.util.Arrays;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stopwatch;

// Although ThreeSum & BinarySearch already available, this is just for funs
public class ThreeSum {
    /**
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
     * Returns the number of triples (i, j, k) with {i < j < k}
     * such that {a[i] + a[j] + a[k] == 0}.
     *
     * @param a the array of integers
     * @return the number of triples (i, j, k) with {i < j < k}
     * such that {a[i] + a[j] + a[k] == 0}
     */
    public static int countBinary(int[] a) {
        int n = a.length;
        Arrays.sort(a);
        int count = 0;
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

    // Static method for binary search
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

    // Test the elapsed time for three sum approach
    public static void main(String[] args) {
        In in = new In(args[0]);
        int[] a = in.readAllInts();

        // Calculate time elapsed for binary search
        Stopwatch sw_binary = new Stopwatch();
        int countBin = countBinary(a);
        double timeBin = sw_binary.elapsedTime();
        StdOut.println("Binary search for three sums to 0: " + countBin);
        StdOut.println("Three sums binary search elapsed time: " + timeBin);

        // Calculate time elapsed for brute force triple nested loop
        Stopwatch sw_brute = new Stopwatch();
        int countBF = countBrute(a);
        double timeBF = sw_brute.elapsedTime();
        StdOut.println("Binary search for three sums to 0: " + countBF);
        StdOut.println("Three sums binary search elapsed time: " + timeBF);
    }
}
