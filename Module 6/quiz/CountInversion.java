/* *****************************************************************************
 *  Name:              Dennis Goh Jia Wang
 *  Last modified:     11/1/2026
 *  Interview Question: Mergesort (Quiz)
 **************************************************************************** */

/* *****************************************************************************
 * Counting inversions:
 * An inversion in an array a[] is a pair of entries a[i] and a[j] such that
 * i < j but a[i] > a[j]. Given an array, design a linearithmic algorithm to
 * count the number of inversions.
 **************************************************************************** */
import edu.princeton.cs.algs4.StdOut;

public class CountInversion {
    /* *************************************************************************
     * Similar merge approach but track inversion count = (mid - i + 1) for
     * right half element that placed before left half.
     * Left-half: aux[lo ... mid]   (i pointer)
     * Right-half: arr[mid + 1 ... hi]  (j pointer)
     * -------------------------------------------------------------------------
     * Let suppose left-half sorted
     * aux[i] <= aux[i + 1] <= aux[i + 2] ... <= aux[mid]
     * arr[j] < aux[i] -> all left element after i are greater than arr[j]
     * arr[j] < aux[i] < aux[i + 1] ... < aux[mid]
     * inv count = mid - i + 1
     * -------------------------------------------------------------------------
     * E.g. arr = [2, 5, 7, 1, 6]
     * Left (sorted): aux = [2, 5, 7]
     * Right (sorted): arr = [1, 6]
     * At i = 0, mid = 2, j = 3
     * Compare: arr[3] < aux[0] = 1 < 2, j > i
     * Inversion: mid - i + 1 = 2 - 0 + 1 = 3 (since 5 and 7 also > 1)
     * At j++ = 4, i = 0 => no inversion,
     * At i++ = 2, j = 4 => inversion happened (arr[4] < aux[2])
     * Inversion: mid - i + 1 = 2 - 2 + 1 = 1 (since only 6 < 7)
     ************************************************************************ */
    private static int merge(int[] arr, int lo, int mid, int hi) {
        // copy left half to aux[mid]
        int[] aux = new int[mid + 1];
        for (int k = 0; k <= mid; k++) {
            aux[k] = arr[k];
        }

        // merge back to arr[]
        int count = 0;
        for (int k = lo, i = lo, j = mid + 1; k <= hi; k++) {
            if (i > mid) {
                // Left exhausted
                arr[k] = arr[j++];
            }
            else if (j > hi) {
                // Right exhausted
                arr[k] = aux[i++];
            }
            else if (aux[i] <= arr[j]) {
                // Take left
                arr[k] = aux[i++];
            }
            else {
                // Take right (inversion happened where j is always > i)
                arr[k] = arr[j++];
                count += (mid - i + 1);
            }
        }
        return count;
    }

    private static int sort(int[] arr, int lo, int hi) {
        if (hi <= lo) return 0;
        int mid = lo + (hi - lo) / 2;
        int count = 0;

        // Divide and conquer (resursive sort)
        count += sort(arr, lo, mid);
        count += sort(arr, mid + 1, hi);

        // Optional micro-optimization: if already ordered, skip merge.
        if (arr[mid] <= arr[mid + 1]) return count;

        // Merge
        count += merge(arr, lo, mid, hi);

        return count;
    }

    public static void main(String[] args) {
//        int[] test = {2, 11, 6, 3, 7, 5, 10};
        int[] test = {2, 5, 7, 1, 6};
        int inv = sort(test, 0, test.length - 1);
        StdOut.println(inv);
    }
}
