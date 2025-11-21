// Java implementation for Array Queue data structure with repeated doubling
import java.util.NoSuchElementException;

/* *****************************************************************************
 * Queue enqueue() operation:
 * 1) Maintain an array q[] and two indices: head and tail
 * 2) When the array is full during enqueue(), double its capacity:
 *          1 -> 2 -> 4 -> 8 -> ... -> 2^(k - 1)
 * 3) Each resizing will copy all existing element into new array takes O(n)
 * 4) Adding new element at q[tail] takes O(1)
 * 5) Update tail modulo the capacity
 * 6) Modulo ensures that if tail reaches the end of the array, it wraps back to
 * 0 (circular behavior).
 * -----------------------------------------------------------------------------
 * E.g. Inserting new element at tail
 * 1) queue: [to] [be] [or] [not]
 *           head            tail
 *    head = 0, tail = 3, n = 4
 * 2) insert: [hello] -> resize q[8] (capacity = 8)
 *    reset: head = 0, tail = n = 4
 *    queue: [to] [be] [or] [not] [hello] [null] [null] [null]
 *           head                  tail
 * 4) update:
 *    tail = (tail + 1) % q.length = 5 % 8 = 5
 *    head = 0, tail = 5, n++ => n = 5
 *    queue: [to] [be] [or] [not] [hello] [null] [null] [null]
 *           head                          tail
 * -----------------------------------------------------------------------------
 * Queue dequeue() operation:
 * 1) Maintain an array q[] and two indices: head and tail
 * 2) Remove an element from q[head] and reference it to null for better memory
 * management takes O(1)
 * 3) When the array becomes too empty (e,g, size <= 1/4 of capacity), halves
 * its capacity
 *          2^(k - 1) -> ... -> 8 -> 4 -> 2 -> 1
 * 4) Each resizing will copy q[head] till q[tail] existing element into new
 * array takes O(n)
 * 5) Update head modulo the capacity
 * -----------------------------------------------------------------------------
 * E.g. Removing element at head
 * 1) queue: [to] [be] [or] [not] [hello] [null] [null] [null]
 *           head                  tail
 *    capacity = 8, head = 0, tail = 5, n = 5
 * 2) remove: [to]
 *    update: head = (head + 1) % q.length = 1 % 8 = 1
 *    queue: [null] [be] [or] [not] [hello] [null] [null] [null]
 *                  head             tail
 *    head = 1, tail = 5, n-- => n = 4
 * 3) remove: [be]
 *    queue: [null] [null] [or] [not] [hello] [null] [null] [null]
 *                         head         tail
 *    head = 2, tail = 5, n-- => n = 3
 * 4) remove: [or]
 *    queue: [null] [null] [null] [not] [hello] [null] [null] [null]
 *                                 head   tail
 *    head = 3, tail = 5, n-- => n = 2
 * 5) shrink:
 *    n = capacity/4 = 8/4 = 2
 *    copy:
 *    index = (head + i) % q.length, where i < n, i++
 *    for i = 0, copy[0] = q[(3 % 8)] = q[3]
 *    for i = 1, copy[1] = q[(4 % 8)] = q[4]
 *    reset: head = 0, tail = n = 2
 *    queue: [not] [hello] [null] [null]
 *            head          tail
 * -----------------------------------------------------------------------------
 * Circular behavior (Rare cases):
 * 1) Although array shrinking will reset head and tail but circular behavior is
 * not eliminated permanenetly
 * 2) For instance, frequent enqueue and dequeue operations will cause tail and
 * head to wrap around (tail < head)
 * 3) Circular behavior depends on modulo indexing, not on whether shrinking
 * happened
 * -----------------------------------------------------------------------------
 * E.g. Wrap-around cases:
 * 1) queue: [null] [null] [null] [null]
 *           head            tail
 *    capacity = 4, head = 0, tail = 0, n = 0
 * 2) insert: [A]
 *    tail = (tail + 1) % q.length = 1 % 4 = 1
 *    head = 0, tail = 1, n++ => n = 1
 *    queue: [A] [null] [null] [null]
 *           head tail
 * 3) insert: [B]
 *    head = 0, tail = 2, n++ => n = 2
 *    queue: [A] [B] [null] [null]
 *           head     tail
 * 4) insert: [C]
 *    head = 0, tail = 3, n++ => n = 3
 *    queue: [A] [B] [C] [null]
 *           head         tail
 * 9) remove: [A]
 *    head = (head + 1) % q.length = 1 % 4 = 1
 *    head = 1, tail = 3, n-- => n = 2
 *    queue: [null] [B] [C] [null]
 *                  head     tail
 * 10) remove: [B]
 *    head = 2, tail = 3, n-- => n = 1
 *    queue: [null] [null] [C] [null]
 *                         head tail
 * 11) insert: [D]
 *    tail = (tail + 1) % q.length = 4 % 4 = 0
 *    head = 2, tail = 0, n++ => n = 2
 *    queue: [null] [null] [C] [D]
 *            tail         head
 * 12) insert: [E]
 *    head = 2, tail = 1, n++ => n = 3
 *    queue: [E] [null] [C] [D]
 *                tail  head
 **************************************************************************** */
public class OptimizedQueueOfStrings {
    private String[] q;
    private int head = 0;
    private int tail = 0;
    private int N = 0;

    public OptimizedQueueOfStrings() {
        q = new String[1];
    }

    public boolean isEmpty() {
        return N == 0;
    }

    // Insert new element to tail
    public void enqueue(String item) {
        if (N == q.length) resize(2 * q.length);
        q[tail] = item;
        tail = (tail + 1) % q.length;
        N++;
    }

    // Delete element from head
    public String dequeue() {
        if (isEmpty()) throw new NoSuchElementException("Queue underflow");
        String item = q[head];
        q[head] = null;     // Avoid loitering
        head = (head + 1) % q.length;
        N--;

        // Optimzied approach on shrinking the array size when one-quarter full
        if (N > 0 && N == q.length/4) resize(q.length/2);
        return item;
    }

    private void resize(int capacity) {
        String[] copy = new String[capacity];
        for (int i = 0; i < N; i++) {
            copy[i] = q[(head + i) % q.length];
        }
        q = copy;

        // Reset head and tail pointer
        head = 0;
        tail = N;
    }
}
