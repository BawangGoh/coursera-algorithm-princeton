/* *****************************************************************************
 *  Name:              Dennis Goh Jia Wang
 *  Last modified:     8/10/2025
 *  Interview Question: Analysis of Algorithms (Quiz)
 **************************************************************************** */

/* *****************************************************************************
 * Search in a bitonic array:
 * An array is bitonic if it is comprised of an increasing sequence of integers
 * followed immediately by a decreasing sequence of integers. Write a program
 * that, given a bitonic array of n distinct integer values, determines whether
 * a given integer is in the array.
 * 1) Standard version: Use ~3log(N) compares in the worst case.
 * 2) Signing bonus: Use ~2log(N) compares in worst case (and prove that no
 * algorithm can guarantee to perform fewer than ~2log(N) compares in worst case).
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;

public class BitonicArray {
    private final int[] a;

    // Constructor
    public BitonicArray(int[] array) {
        this.a = array;
    }

    // Validate bitonic array for distinct integers
    private static boolean validate(int[] a) {
        // Check duplicate value
        for (int i = 1; i < a.length; i++)
            if (a[i] == a[i-1]) return true;

        // Check if array length more than 3
        if (a.length < 3) return true;
        return false;
    }

    /* *****************************************************************************
     * Solution
     * Standard version with time complexity "3log(n)":
     * 1) Find the bitonic point (peak element) to divide into two sorted parts
     * 2) Search the element in ascending segment with binary search
     * 3) Search the element in descending segment with binary search
     * -------------------------------------------------------
     * E.g.
     * mid = low + high / 2 => 8/2 = 4
     * index    =   0    1     2      3      4      5      6    7    8
     * array[]  =   8    10    100    200    400    500    3    2    1
     *            (low)                     (mid)                  (high)
     * array[mid] < array[mid + 1] => 400 < 500
     * -------------------------------------------------------
     * Update low pointer:
     * low = mid + 1 => 4 + 1 = 5
     * mid = low + high / 2 => 5 + 8 / 2 = 6
     * index    =   0    1     2      3      4      5      6    7    8
     * array[]  =   8    10    100    200    400    500    3    2    1
     *                                             (low) (mid)     (high)
     * array[mid] > array[mid + 1] => 3 > 2
     * -------------------------------------------------------
     * Update high pointer:
     * high = mid - 1 => 6 - 1 = 5
     * mid = low + high / 2 => 5 + 5 / 2 = 5
     * index    =   0    1     2      3      4      5      6    7    8
     * array[]  =   8    10    100    200    400    500    3    2    1
     *                                             (low)
     *                                             (high)
     *                                             (mid)
     **************************************************************************** */
    public int findBitonicPeak() {
        if (validate(a)) {
            throw new IllegalArgumentException("Bitonic array must be distinct integers or length > 2");
        }
        int lo = 0;
        int hi = a.length - 1;
        while (lo <= hi) {
            int mid = (lo + hi) / 2;

            // Check if bitonic peak is at ascending or descending segment
            if (a[mid] < a[mid + 1]) {
                // Update the low to mid + 1
                lo = mid + 1;
            } else {
                // Update the high to mid - 1
                hi = mid - 1;
            }
        }
        return lo;
    }

    // Modified binary search for searching target element in ascending and
    // descending segments of bitonic array
    public boolean modifiedBinarySearch(int target, int lo, int hi, boolean ascending) {
        while (lo <= hi) {
            int mid = (lo + hi) / 2;
            if (target == a[mid]) return true;

            /*
             * Two-way compare to search left or right subarray
             * Check if target lies in monotonic increase or decrease parts
             */
            if (target < a[mid] == ascending) {
                hi = mid - 1;
            } else {
                lo = mid + 1;
            }
        }

        // Element not found in the bitonic array
        return false;
    }

    public boolean bitonicStandard(int target) {
        int peak = findBitonicPeak();

        // Search element for ascending or descending subarray
        return modifiedBinarySearch(target, 0, peak, true) ||
                modifiedBinarySearch(target, peak+1, a.length-1, false);
    }

    /* *****************************************************************************
     * Solution
     * Optimzed version with time complexity "2log(n)":
     * 1) Combine finding peak and search into single recursive binary search even
     * the subarray is not sorted
     * 2) Avoids redundant comparisons on both ascending and descending subarray
     * ------------------------------------------------------------------------
     * E.g.
     * target   =   3
     * index    =   0    1     2      3      4      5      6    7    8
     * array[]  =   8    10    100    200    400    500    3    2    1
     *            (low)                     (mid)                  (high)
     * array[mid - 1] < array[mid] < array[mid + 1] => 200 < 400 < 500
     * Ascending parts:
     * target < a[mid] => 3 < 400 => recursive(target, low, mid - 1)
     * ------------------------------------------------------------------------
     * 1st recursive search:
     * target   =   3
     * index    =   0    1     2      3
     * subarr[] =   8    10    100    200
     *            (low) (mid)        (high)
     * array[mid - 1] < array[mid] < array[mid + 1] => 8 < 10 < 100
     * Ascending parts:
     * target < a[mid] => 3 < 10 => recursive(target, low, mid - 1)
     * ------------------------------------------------------------------------
     * 2nd recursive search:
     * target   =   3
     * index    =   0
     * subarr[] =   8
     *            (low)
     *            (high)
     *            (mid)
     * mid == 0 => reach the end of subarray
     * Ascending parts:
     * target < a[mid] => 3 < 8 => recursive(target, low, mid - 1)
     * ------------------------------------------------------------------------
     * 3rd recursive search:
     * lo > hi => 0 > -1 => return false
     **************************************************************************** */
    public boolean recursiveSearch(int target, int lo, int hi) {
        // low pointer > high pointer mean no element found
        if (lo > hi) return false;

        // Return true if mid element is the target
        int mid = (lo + hi) / 2;
        if (a[mid] == target) return true;

        // Use the shape of array at mid to decide search direction
        boolean leftIsSmaller = mid == 0 || a[mid - 1] < a[mid];
        boolean rightIsSmaller = mid == a.length - 1 || a[mid] > a[mid + 1];

        // Decide to recursively search if element is peak, ascending or descending parts
        if (leftIsSmaller && rightIsSmaller) {
            /*
             * Peak element found (a[mid - 1] < a[mid] > a[mid + 1]),
             * search both sides
             */
            return recursiveSearch(target, lo, mid - 1) || recursiveSearch(target, mid + 1, hi);
        } else if (leftIsSmaller) {
            /*
             * Ascending parts (a[mid -1] < a[mid] < a[mid + 1]),
             * search left if target < a[mid],
             * otherwise search right unsorted subarray
             */
            if (target < a[mid]) {
                return recursiveSearch(target, lo, mid - 1);
            } else {
                return recursiveSearch(target, mid + 1, hi);
            }
        } else {
            /*
             * Descending parts (a[mid - 1] > a[mid] > a[mid + 1]),
             * search right if target < a[mid],
             * otherwise search left unsorted subarray
             */
            if (target < a[mid]) {
                return recursiveSearch(target, mid + 1, hi);
            } else {
                return recursiveSearch(target, lo, mid - 1);
            }
        }
    }

    public boolean bitonicOptimzed(int target) {
        return recursiveSearch(target, 0, a.length - 1);
    }

    /* *****************************************************************************
     * Corner cases:
     * 1) False assumption of array shape
     * 2) Recursive search skip the actual peak
     * ------------------------------------------------------------------------
     * E.g.
     * target   =   3
     * index    =   0    1     2      3      4      5      6    7    8
     * array[]  =   2    10    100    200    400    500    3    2    1
     *            (low)                     (mid)                  (high)
     * array[mid - 1] < array[mid] < array[mid + 1] => 200 < 400 < 500
     * Ascending parts:
     * target < a[mid] => 3 < 400 => recursive(target, low, mid - 1)
     * ------------------------------------------------------------------------
     * 1st recursive search:
     * target   =   3
     * index    =   0    1     2      3
     * subarr[] =   2    10    100    200
     *            (low) (mid)        (high)
     * array[mid - 1] < array[mid] < array[mid + 1] => 8 < 10 < 100
     * Ascending parts:
     * target < a[mid] => 3 < 10 => recursive(target, low, mid - 1)
     * ------------------------------------------------------------------------
     * 2nd recursive search:
     * target   =   3
     * index    =   0
     * subarr[] =   2
     *            (low)
     *            (high)
     *            (mid)
     * mid == 0 => reach the end of subarray
     * Ascending parts:
     * target > a[mid] => 3 > 2 => recursive(target, mid + 1, hi)
     * ------------------------------------------------------------------------
     * 3rd recursive search:
     * lo > hi => 1 > 0 => return false
     * ------------------------------------------------------------------------
     * ------------------------------------------------------------------------
     * Fix:
     * Search both sides instead of assuming the shape of array at mid
     * ------------------------------------------------------------------------
     * E.g.
     * target   =   3
     * index    =   0    1     2      3      4      5      6    7    8
     * array[]  =   2    10    100    200    400    500    3    2    1
     *            (low)                     (mid)                  (high)
     * recursive_1(target, lo, mid - 1)
     * recursive_2(target, mid + 1, hi)
     * ------------------------------------------------------------------------
     * Recursive_1:
     * target   =   3
     * index    =   0    1     2      3
     * subarr[] =   2    10    100    200
     *            (low) (mid)        (high)
     * recursive_3(target, lo, mid - 1)
     * recursive_4(target, mid + 1, hi)
     * Recursive_2:
     * index    =   0     1     2     3
     * subarr[] =   500   3     2     1
     *             (low) (mid)      (high)
     * a[mid] == target => return true
     * ------------------------------------------------------------------------
     * Recursive_3:
     * target   =   3
     * index    =   0
     * subarr[] =   2
     *            (low)
     *            (high)
     *            (mid)
     * recursive_5(target, lo, mid - 1)
     * recursive_6(target, mid + 1, hi)
     * Recursive_4:
     * index    =   0     1
     * subarr[] =   2     3
     *             (low) (high)
     *             (mid)
     * recursive_7(target, lo, mid - 1)
     * recursive_8(target, mid + 1, hi)
     * ------------------------------------------------------------------------
     * Recursive_5:
     * lo > hi => 0 > -1 => return false
     * Recursive_6:
     * lo > hi => 1 > 0 => return false
     * Recursive_7:
     * lo > hi => 0 > -1 => return false
     * Recursive_8:
     * lo > hi => 0 > 0 => return false
     * ------------------------------------------------------------------------
     * recursiveCorner -> (false || true) => true
     *      recursive_1 -> (false || false) => false
     *          recursive_3 -> (false || false) => false
     *              recursive_5 -> return false
     *              recursive_6 -> return false
     *          recursive_4 -> (false || false) => false
     *              recursive_7 -> return false
     *              recursive_8 -> return false
     *      recursive_2 -> return true
     **************************************************************************** */
    public boolean recursiveCorner(int target, int lo, int hi) {
        // low pointer > high pointer mean no element found
        if (lo > hi) return false;

        // Return true if mid element is the target
        int mid = (lo + hi) / 2;
        if (a[mid] == target) return true;

        // Instead of assuming shape of array at mid, search both sides of unsorted subarry
        return recursiveCorner(target, lo, mid - 1) || recursiveCorner(target, mid + 1, hi);
    }

    public boolean bitonicCorner(int target) {
        return recursiveCorner(target, 0, a.length - 1);
    }

    /* *****************************************************************************
     * Solution
     * Naive approach with time complexity "n + 2log(n)":
     * 1) Iterate bitonic array and store the peak element index
     * 2) Search both sides using binary search
     **************************************************************************** */
    // Linear scan to store the peak element index
    public int findNaivePeak() {
        if (validate(a)) {
            throw new IllegalArgumentException("Bitonic array must be distinct integers or length > 2");
        }

        // Start traverse from 1 till n - 1 since 0 is already stored
        for (int i = 1; i < a.length; i++) {
            if (a[i] > a[i - 1] && a[i] > a[i + 1]){
                return i;   // Get peak index
            }
        }

        return -1;
    }

    public boolean bitonicNaive(int target) {
        int peak = findNaivePeak();

        // Search element for ascending or descending subarray
        return modifiedBinarySearch(target, 0, peak, true) ||
                modifiedBinarySearch(target, peak+1, a.length-1, false);
    }

    public static void main(String[] args) {
        // Good bitonic array example
        int[] bitonic_1 = {1, 3, 8, 12, 14, 13, 10, 6, 2};
        BitonicArray standardObj1 = new BitonicArray(bitonic_1);
        BitonicArray optimizedObj1 = new BitonicArray(bitonic_1);
        BitonicArray cornerObj1 = new BitonicArray(bitonic_1);
        BitonicArray naiveObj1 = new BitonicArray(bitonic_1);

        // Bitonic array corner case
//        int[] bitonic_2 = {8, 10, 100, 200, 400, 500, 3, 2, 1};
        int[] bitonic_2 = {2, 10, 100, 200, 400, 500, 3, 2, 1};
        BitonicArray standardObj2 = new BitonicArray(bitonic_2);
        BitonicArray optimizedObj2 = new BitonicArray(bitonic_2);
        BitonicArray cornerObj2 = new BitonicArray(bitonic_2);
        BitonicArray naiveObj2 = new BitonicArray(bitonic_2);

        // Search element in bitonic array
        int target = 3;

        // Standard Output for good case
        StdOut.println("Bitonic Array Good Case");
        StdOut.println("Find element " + target + " using standard version: " + standardObj1.bitonicStandard(target));
        StdOut.println("Find element " + target + " using optimzed version: " + optimizedObj1.bitonicOptimzed(target));
        StdOut.println("Find element " + target + " using corner case version: " + cornerObj1.bitonicCorner(target));
        StdOut.println("Find element " + target + " using naive version: " + naiveObj1.bitonicCorner(target));

        // Standard Output for corner case
        StdOut.println("Bitonic Array Corner Case");
        StdOut.println("Find element " + target + " using standard version: " + standardObj2.bitonicStandard(target));
        StdOut.println("Find element " + target + " using optimzed version: " + optimizedObj2.bitonicOptimzed(target));
        StdOut.println("Find element " + target + " using corner case version: " + cornerObj2.bitonicCorner(target));
        StdOut.println("Find element " + target + " using naive case version: " + naiveObj2.bitonicCorner(target));
    }
}
