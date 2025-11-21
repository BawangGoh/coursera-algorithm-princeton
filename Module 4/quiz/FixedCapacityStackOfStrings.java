// Basic Java implementation for Array Stack data structure
import java.util.NoSuchElementException;

public class FixedCapacityStackOfStrings {
    private String[] s;
    private int N = 0;

    // Provide capacity for client does not implement API since it cannot be
    // grow or shrink
    public FixedCapacityStackOfStrings(int capacity) {
        s = new String[capacity];
    }

    public boolean isEmpty() {
        return N == 0;
    }

    public void push(String item) {
        // Post-increment: use index into array, then increment N
        // e.g N = 2 => s[2] = item => N + 1 = 3
        s[N++] = item;
    }

    /* *************************************************************************
     * Stack array implementation:
     * Pop method if return s[--N] will cause loitering which holding a
     * reference to an object when it is no longer needed. Java Runtime Garbage
     * Collector cannot reclaim the memory if there is still reference.
     * -------------------------------------------------------------------------
     * E.g. Loitering:
     * stack: [to][be][or][not][to][be]
     *         N
     * s[N]     -> [to]
     * s[N - 1] -> [be]
     * ...
     * s[0]     -> [be]
     * -------------------------------------------------------------------------
     * Push() operation always post-increment N++: N = 6
     * Pop() operation always pre-decrement --N: N = 5
     * 1) pop() without null assignment:
     * BEFORE: s = [to][be][or][not][to][be]
     *              5
     * s[5] -> [to] (removed)
     * s[4] -> [be]
     * ...
     * s[0] -> [be]
     * AFTER: s = [to][be][or][not][to][be]
     *                  4
     * [to] still exist inside stack, therefore s[5] still accessible as it is
     * pointing to it
     * -------------------------------------------------------------------------
     * 2) pop() with null assignment:
     * BEFORE: s = [null][be][or][not][to][be]
     *               5
     * item -> [to] (removed)
     * s[5] -> [null]
     * s[4] -> [be]
     * ...
     * s[0] -> [be]
     * AFTER: s = [null][be][or][not][to][be]
     *                    4
     * s[5] = NullPointerException
     * s[5] no longer can be accessed as it is pointing to null reference and
     * will be cleared by garbage collector.
     ************************************************************************ */
    public String pop() {
        if (isEmpty()) throw new NoSuchElementException("Stack underflow");

        // Pre-decrement: Decrement N, then use to index into array
        // e.g. N = 5 => N - 1 = 4 => return s[4]
        String item = s[--N];
        s[N] = null;    // Explicitly nullify the reference
        return item;

        // NOTES: return s[--N] will cause loitering issue
        // return s[--N];
    }
}
