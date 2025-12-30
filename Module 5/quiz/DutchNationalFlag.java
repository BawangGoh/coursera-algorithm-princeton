/* *****************************************************************************
 *  Name:              Dennis Goh Jia Wang
 *  Last modified:     28/12/2025
 *  Interview Question: Elementary Sorts (Quiz)
 **************************************************************************** */

/* *****************************************************************************
 * Dutch National Flag:
 * Given an array of n buckets, each containing a red, white, or blue pebble,
 * sort them by color. The allowed operations are:
 * 1) swap(i, j): swap the pebble in bucket i with the pebble in bucket j
 * 2) color(i): determine the color of the pebble in bucket i
 *
 * The perforcement requirements are as follows:
 * 1) at most n calls to color()
 * 2) at most n calls to swap()
 * 3) constant extra space
 **************************************************************************** */
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdOut;
import java.util.Arrays;

public class DutchNationalFlag {
    private int[] buckets;

    // Constructor for Dutch Flag
    public DutchNationalFlag(int n) {
        this.buckets= new int[n];

        // Generate random color for buckets (0 = RED, 1 = WHITE, 2 = BLUE)
        for (int i = 0; i < n; i++) {
            buckets[i] = StdRandom.uniformInt(3);
        }
    }

    // Swap operation
    public void swap(int i, int j) {
        int tmp = buckets[i];
        buckets[i] = buckets[j];
        buckets[j] = tmp;
    }

    // Return color of bucket
    public int color(int i) {
        return buckets[i];
    }

    /* *************************************************************************
     * Sort using 3-way partition algorithms:
     * Maintaining 3 pointer (lo, mid, hi) to indicate color region
     * 1) lo: next position to place 0 (RED)
     * 2) mid: current index for inspecting
     * 3) hi: next position to place 2 (BLUE)
     * The end result is as follows:
     * [0 ... lo - 1] are RED region
     * [lo ... mid - 1] are WHITE region
     * [mid ... hi] are UNKNOWN region (current inspecting for sorting)
     * [hi + 1 ... n - 1] are BLUE region
     * -------------------------------------------------------------------------
     * e.g.
     * [2, 0, 1, 2, 1, 0, 0, 2, 1, 0] -> [0, 0, 0, 0, 1, 1, 1, 2, 2, 2]
     ************************************************************************ */
    public void sort() {
        // Mid does not start half-way since we need to sort from left to right
        int lo = 0, mid = 0;
        int hi = buckets.length - 1;

        while (mid <= hi) {
            if (color(mid) == 0) {
                // Avoid self-swap
                if (lo != mid) {
                    swap(lo, mid);
                }
                lo++;
                mid++;
            } else if (color(mid) == 1) {
                mid++;
            } else {
                // Avoid self-swap
                if (mid != hi) swap(mid, hi);
                hi--;
                // NOTES: Do not mid++ because we need to inspect mid after swap
            }
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(buckets);
    }

    public static void main(String[] args) {
        DutchNationalFlag buckets = new DutchNationalFlag(16);
        StdOut.println("Before sorting: " + buckets);
        buckets.sort();
        StdOut.println("After sorting: " + buckets);
    }
}
