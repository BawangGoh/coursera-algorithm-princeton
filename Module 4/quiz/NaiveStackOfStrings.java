// Java implementation for Array Stack data structure with Naive approach
import java.util.NoSuchElementException;

/* *****************************************************************************
 * Stack push() operation:
 * 1) Array copy takes O(n) time, where n is the current size of stack
 * 2) Adding new element is O(1)
 * 3) Therefore, operation
 *          push() = O(n)
 * 4) If push n elements one by one, total cost is:
 *          O(1 + 2 + 3 + ... + n) = O(n^2)
 * -----------------------------------------------------------------------------
 * Stack pop() operation:
 * 1) Copying N elements take O(n) time
 * 2) Clearing reference (s[N] = null) is O(1)
 * 3) Therefore, operation
 *          pop() = O(n)
 * 4) If pop all element one by one:
 *          O(n + (n - 1) + (n - 2) + ... + 1) = O(n^2)
 * -----------------------------------------------------------------------------
 * Worst case operation:
 * 1) Array resizing happened too frequently e.g. alternatinig operation:
 *          push(), pop(), push(), pop(), ...
 * 2) Each push() take O(n) time
 * 3) Each pop() take O(n) time
 * 4) For k alternating operation on a stack that grows up to size n:
 *          O(n) + O(n - 1) + O(n) + O(n - 1) + ... ~ O(k * n)
 *          O(k * n) ~ O(n^2)       when k = n (push and pop n times)
 **************************************************************************** */
public class NaiveStackOfStrings {
    private String[] s;
    private int N = 0;

    // Initialize constructor with minimum strings array
    public NaiveStackOfStrings() {
        s = new String[1];
    }

    public boolean isEmpty() {
        return N == 0;
    }

    public void push(String item) {
        if(N == s.length) resize(s.length + 1);
        s[N++] = item;
    }

    public String pop() {
        if (isEmpty()) throw new NoSuchElementException("Stack underflow");
        String item = s[--N];
        s[N] = null;    // avoid loitering

        // Naive approach on shrinking the array size since N is always 1 value
        // decrement
        if (N > 0) resize(N);
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
