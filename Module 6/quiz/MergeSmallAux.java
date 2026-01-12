/* *****************************************************************************
 *  Name:              Dennis Goh Jia Wang
 *  Last modified:     11/1/2026
 *  Interview Question: Mergesort (Quiz)
 **************************************************************************** */

/* *****************************************************************************
 * Merging with smaller auxiliary array:
 * Suppose that the subarray a[0] to a[n-1] is sorted and the subarray a[n] to
 * a[2*n - 1] is sorted. How can you merge the two subarrays so that a[0] to
 * a[2*n - 1] is sorted using an auxiliary array of length n (instead of 2n) ?
 **************************************************************************** */
import edu.princeton.cs.algs4.StdOut;
import java.util.Arrays;

public class MergeSmallAux {
    /* *************************************************************************
     * Use only n extra space to copy left half arr[0...n - 1] into aux and the
     * right half stays in-place. Perform mergin as usual by:
     * 1) i tranverses aux[0...n-1] (Left subarray)
     * 2) j tranverses arr[n...2n-1] (Right subarray)
     * 3) Overwrite arr[k] in-place from k = 0 to 2n-1
     ************************************************************************ */
    private static void merge(int[] arr, int lo, int mid, int hi) {
        assert isSorted(arr, lo, mid);
        assert isSorted(arr, mid + 1, hi);

        // copy left half to aux[mid + 1] because round_down(odd/2) => mid + 1
        int[] aux = new int[mid + 1];
        for (int k = 0; k <= mid; k++) {
            aux[k] = arr[k];
        }

        // merge back to a[]
        for (int k = 0, i = lo, j = mid + 1; k <= hi; k++) {
            // Left subarray is exhausted (Copy the rest from right)
            if (i > mid) arr[k] = arr[j++];

            // Right subarray is exhausted (Copy the rest from left)
            else if (j > hi) arr[k] = aux[i++];

            // Right subarray element < left subarray element (Take right)
            else if (arr[j] <= aux[i]) arr[k] = arr[j++];

            // Take left
            else arr[k] = aux[i++];
        }

        // postcondition: a[lo .. hi] is sorted
        assert isSorted(arr, lo, hi);
    }

    private static boolean isSorted(int[] arr, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++)
            if (arr[i - 1] > arr[i]) return false;
        return true;
    }

    public static void main(String[] args) {
        int[] test = {1, 4, 6, 9, 11, 2, 3, 8, 10};
        merge(test, 0, test.length / 2, test.length - 1);
    }
}
