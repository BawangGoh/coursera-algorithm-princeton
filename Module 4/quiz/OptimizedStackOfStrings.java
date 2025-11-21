// Java implementation for Array Stack data structure with repeated doubling
import java.util.NoSuchElementException;

/* *****************************************************************************
 * Stack push() operation:
 * 1) When the array is full during push(), double its capacity:
 *          1 -> 2 -> 4 -> 8 -> ... -> 2^(k - 1)
 * 2) Each resizing will copy all existing element into new array takes O(n)
 *          1 + 2 + 4 + 8 + ... + 2^(k-1)
 *          S = a * (r^k - 1)/(r - 1)
 *            = 2^k - 1
 *            = 2 * 2^(k - 1) - 1
 * 3) If the last term is n = 2^(k-1)
 *          1 + 2 + 4 + 8 + ... + n
 *          S = 2n - 1 ~ 2n
 *          Total cost = O(n)
 * 4) This operation rarely happened as the array grows
 * 5) Adding new element is O(1)
 * -----------------------------------------------------------------------------
 * Stack pop() operation:
 * 1) When the array becomes too empty (e,g, size <= 1/4 of capacity), halves
 * its capacity
 *          2^(k - 1) -> ... -> 8 -> 4 -> 2 -> 1
 * 2) This prevent wasting memory when stack shrinks significantly
 *          Total cost = O(n) since it is same geometric series
 * 3) Array is always between 25% and 100% full
 * -----------------------------------------------------------------------------
 * Amortized analysis:
 * 1) Push: Most operations are O(1) and occasionally, resizing costs O(n), but
 * happens infrequently.
 * 2) Pop: Same logic applies when halving.
 * 3) The expensive operations O(n) are spread out over n operations.
 *          Average cost per operation = O(n)/n = O(1)
 * 4) Cost spikes occur during resizing (copying elements)
 * 5) Between spikes, operations are constant time.
 * 6) Over the time, spikes are flatten out -> amortized O(1)
 **************************************************************************** */
public class OptimizedStackOfStrings {
    private String[] s;
    private int N = 0;

    public OptimizedStackOfStrings() {
        s = new String[1];
    }

    public boolean isEmpty() {
        return N == 0;
    }

    public void push(String item) {
        if(N == s.length) resize(2 * s.length);
        s[N++] = item;
    }

    public String pop() {
        if (isEmpty()) throw new NoSuchElementException("Stack underflow");
        String item = s[--N];
        s[N] = null;    // avoid loitering

        // Optimzied approach on shrinking the array size when one-quarter full
        if (N > 0 && N == s.length/4) resize(s.length/2);
        return item;
    }

    private void resize(int capacity) {
        String[] copy = new String[capacity];
        for (int i = 0; i < N; i++) {
            copy[i] = s[i];
        }
        s = copy;
    }
}
