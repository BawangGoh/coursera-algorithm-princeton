/* *****************************************************************************
 *  Name:              Dennis Goh Jia Wang
 *  Last modified:     22/11/2025
 **************************************************************************** */

/* *****************************************************************************
 * Randomized Queue:
 * A randomized queue is similar to a stack or queue, except that the item
 * removed is chosen uniformly at random among items in the data structure.
 * Create a generic data type RandomizedQueue that implements the following API:
 *
 * Iterator:
 * Each iterator must return the items in uniformly random order. The order of
 * two or more iterators to the same randomized queue must be mutually
 * independent; each iterator must maintain its own random order.
 *
 * Corner Cases. Throw the specified exception for the following corner cases:
 * 1) Throw an IllegalArgumentException if the client calls enqueue() or with a
 * null argument.
 * 2) Throw a java.util.NoSuchElementException if the client calls either
 * sample() or dequeue() when the randomized queue is empty.
 * 3) Throw a java.util.NoSuchElementException if the client calls the next()
 * method in the iterator when there are no more items to return.
 * 4) Throw an UnsupportedOperationException if the client calls the remove()
 * method in the iterator.
 *
 * Unit testing:
 * Main() method must call directly every public constructor and method to help
 * verify that they work as prescribed (by printing results to standard output).
 *
 * Performance requirements:
 * Your randomized queue implementation must support each randomized queue
 * operation (besides creating an iterator) in constant amortized time. That is,
 * any intermixed sequence of m randomized queue operations (starting from an
 * empty queue) must take at most (c * m) steps in the worst case, for some
 * constant c. A randomized queue containing n items must use at most 48n + 192
 * bytes of memory. Additionally, your iterator implementation must support
 * operations next() and hasNext() in constant worst-case time; and construction
 * in linear time; you may (and will need to) use a linear amount of extra
 * memory per iterator.
 *
 * Conclusion:
 * Randomized queue using array implementation since average operation takes
 * constant amortized time O(1) except the iterator creation. There is no
 * amortized benefit for iterator creation because:
 * 1) Every iterator must copy all item from queue into new array -> O(n)
 * 2) Shuffle the array using Fishes-Yates (Knuth's shuffling) -> O(n)
 * 3) Worst-case: O(n) + O(n) = O(n)
 *
 * Memory Calculation:
 * 1) Each item (Node-free, array-based) (48N bytes):
 *      a) Instance fields -> (8 + 4 + 4) = 16 bytes
 *          i) Item[] data -> 8 bytes (reference to array)
 *          ii) size -> 4 bytes
 *          iii) capacity -> 4 bytes
 *      b) Array overhead -> (16 + 4 + 4) = 24 bytes
 *          i) Object overhead -> 16 bytes
 *          ii) Length field -> 4 bytes
 *          iii) Padding -> 4 bytes
 *          iv) Array elements (resizing amortized) -> (8 * capacity) = 8N bytes
 * 2) Randomized queue object overhead (192 bytes):
 *      a) Object header → typically 16 bytes (on 64-bit JVM)
 *      b) items reference -> 8 bytes
 *      c) int size -> 4 bytes
 *      d) int capacity → 4 bytes
 *      e) Padding for alignment → 4 bytes (to align to 8-byte boundary)
 *      f) Object header + Instance fields = 16 + 20 = 36 (rounded to 48 bytes)
 *      g) Extra overhead for references and internal structures (like iterator
 *         object, class metadata, etc.)
 **************************************************************************** */
import java.util.Iterator;
import java.util.NoSuchElementException;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class RandomizedQueue<Item> implements Iterable<Item> {
    // Private attribute for randomized queue
    private Item queue[];
    private int size;

    // initial capacity of underlying resizing array
    private static final int INIT_CAPACITY = 8;

    // construct an empty randomized queue
    public RandomizedQueue() {
        /* *********************************************************************
         * Need to explicitly cast to object Item since generic array creation
         * are not allow in Java.Randomized queue does not need first and last
         * pointer since order doesnot matter
         ******************************************************************** */
        queue = (Item[]) new Object[INIT_CAPACITY];
        size = 0;
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return size;
    }

    /* *************************************************************************
     * A randomized queue does not need first or last pointer like normal queue
     * because there is no fixed order to remove an item. Therefore, adding and
     * removing item in randomized queue can happened at one end of the array
     * like a stack.
     * -------------------------------------------------------------------------
     * Queue enqueue() operation:
     * 1) Add an item at queue[size] (next available slot)
     * 2) Increment size++
     * 3) This avoid shifting elements andd keeps the array compact.
     * 4) When array is full, resize the array to 2x capacity -> amortized O(1)
     * -------------------------------------------------------------------------
     * E.g. Inserting new element
     * 1) queue: [1, 2, 3, null]
     *    size = 3
     * 2) insert: [4] -> queue[size++]
     *    queue: [1, 2, 3, 4]
     *    size++ => size = 4
     * 3) insert: [9] -> resize queue[8] (capacity = 8)
     *    queue: [1, 2, 3, 4, null, null, null, null]
     *    queue: [1, 2, 3, 4, 9, null, null, null]
     *    size++ => size = 5
     ************************************************************************ */
    // add the item
    public void enqueue(Item item) {
        if (item == null) throw new IllegalArgumentException("Illegal null argument");

        // Resize queue (Logical shift left == multiple of 2)
        if (size == queue.length) resize(queue.length << 1);
        queue[size++] = item;
    }

    /* *************************************************************************
     * Queue dequeue() operation:
     * 1) Pick a random index i in [0, size - 1]
     * 2) Swap queue[i] with queue[size - 1] (last element) to avoid leaving a
     * gap in the middle of the queue.
     * 3) Set queue[size - 1] = null to avoid loitering
     * 4) Decrement size--
     * 5) When array is 1/4 full, resize the array to capacity/2
     * -------------------------------------------------------------------------
     * E.g. Removing an element
     * 1) queue: [1, 3, 4, 9, null, null, null, null]
     *    size = 4
     * 2) remove: rand(0, 3) = 1 -> queue[1] = 3
     *    swap: queue[1] <-> queue[3]
     *    queue: [1, 9, 4, null, null, null, null, null]
     *    size-- => size = 3
     * 3) remove: rand(0, 2) = 0 -> queue[0] = 1
     *    swap: queue[0] <-> queue[2]
     *    queue: [4, 9, null, null, null, null, null, null]
     *    size-- => size = 2 -> resize queue[4] (capacity = 4)
     *    queue: [4, 9, null, null]
     ************************************************************************ */
    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) throw new NoSuchElementException("queue is empty");

        // Randomized index for removal
        int randIdx = StdRandom.uniformInt(size);
        Item item = queue[randIdx];
        queue[randIdx] = null;      // Avoid loitering
        queue[randIdx] = queue[size - 1];
        size--;

        // Resize queue (Logical shift right == divide by 2)
        if (size > 0 && size == queue.length / 4) resize(queue.length >> 1);
        return item;
    }

    // resize the underlying array
    private void resize(int capacity) {
        assert capacity >= size();
        Item[] copy = (Item[]) new Object[capacity];

        // Copying array does not need to modulo since order does not matter
        for (int i = 0; i < size; i++) {
            copy[i] = queue[i];
        }
        queue = copy;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (isEmpty())  throw new NoSuchElementException("queue is empty");
        int randIdx = StdRandom.uniformInt(size);
        return queue[randIdx];
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new ArrayIterator();
    }

    private class ArrayIterator implements Iterator<Item> {
        private int idx;
        private Item[] item;

        // Iterator constructor (copying queue array)
        public ArrayIterator() {
            idx = size - 1;
            item = (Item[]) new Object[size];
            for (int i = 0; i < size; i++) {
                item[i] = queue[i];
            }
            StdRandom.shuffle(item);
        }

        public boolean hasNext() {
            return idx >= 0;
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException("No more items");
            return item[idx--];
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        // Test function
        RandomizedQueue<Integer> randomQueue = new RandomizedQueue<>();
        StdOut.println("Queue is empty: " + randomQueue.isEmpty());
        randomQueue.enqueue(5);
        randomQueue.enqueue(4);
        randomQueue.enqueue(8);
        randomQueue.enqueue(6);
        randomQueue.enqueue(9);
        StdOut.println("Queue is not empty: " + randomQueue.isEmpty());
        StdOut.println("Queue size of 5: " + randomQueue.size());
        StdOut.println("1st random queue sampling: " + randomQueue.sample());
        StdOut.println("2nd random queue sampling: " + randomQueue.sample());
        StdOut.println("Random dequeue element: " + randomQueue.dequeue());
        StdOut.println("Random dequeue element: " + randomQueue.dequeue());
        StdOut.println("Randomized queue elements: ");
        for (int i : randomQueue) {
            StdOut.print(i + " ");
        }
        StdOut.println();

        // Test on mutually independent iterator for queue
        int n = 5;
        RandomizedQueue<Integer> queue = new RandomizedQueue<Integer>();
        for (int i = 0; i < n; i++)
            queue.enqueue(i);
        for (int a : queue) {
            for (int b : queue)
                StdOut.print(a + "-" + b + " ");
            StdOut.println();
        }
    }
}
