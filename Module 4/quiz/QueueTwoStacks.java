/* *****************************************************************************
 *  Name:              Dennis Goh Jia Wang
 *  Last modified:     6/12/2025
 *  Interview Question: Stacks and Queues (Quiz)
 **************************************************************************** */

/* *****************************************************************************
 * Queue with two stacks:
 * Implementing a queue using two stacks (in & out). For IN stack, simple
 * operation like enqueue since queue is an FIFO data structure. For OUT stack,
 * it need to reverse the order of the IN stack in order to dequeue the element.
 * Therefore, push everything in IN stack into OUT stack to maintain the order
 * during dequeue operation.
 * Queue <=> FIFO
 * e.g. x -> y -> z => [z, y, x]
 * 1) dequeue => [z, y] => x
 * 2) dequeue => [z] => y
 * Stack <=> LIFO
 * e.g. x -> y -> z => [z, y, x]
 * IN Stack
 * 1) pop => [y, x] => z (incorrect order)
 * OUT Stack
 * 1) push => z => [z]
 * 2) push => y => [y, z]
 * 3) push => x => [x, y, z]
 * 4) pop => [x, y, z] => x (correct order)
 **************************************************************************** */
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;
import java.util.NoSuchElementException;
import java.util.Iterator;

public class QueueTwoStacks<Item> implements Iterable<Item> {
    private Stack<Item> in, out;

    /**
     * Initializes an empty queue with two stacks.
     */
    public QueueTwoStacks() {
        in = new Stack<>();
        out = new Stack<>();
    }

    /**
     * Returns true if this queue is empty by checking both stacks.
     *
     * @return {@code true} if this queue is empty; {@code false} otherwise
     */
    public boolean isEmpty() {
        return in.isEmpty() && out.isEmpty();
    }

    /**
     * Returns the total number of items of two stacks since OUT stack contain
     * the reverse order of IN stack
     *
     * @return the number of items in this queue
     */
    public int size() {
        return in.size() + out.size();
    }

    /**
     * Adds the item to this queue by pushing into IN stack
     *
     * @param  item the item to add
     */
    public void enqueue(Item item) {
        in.push(item);
    }

    /**
     * Removes and returns the item on this queue that was least recently added
     * by first pushing all item from IN to OUT stacks, then pop the item from
     * OUT stack to maintain the FIFO order.
     *
     * @return the item on this queue that was least recently added
     * @throws NoSuchElementException if this queue is empty
     */
    public Item dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("queue is empty");
        }

        // If OUT stack is empty
        if (out.isEmpty()) {
            // Push all item into OUT stack from IN stack
            while (!in.isEmpty()) {
                out.push(in.pop());
            }
        }
        return out.pop();
    }

    /**
     * Returns the item least recently added to this queue. Similar operation to
     * dequeue
     *
     * @return the item least recently added to this queue
     * @throws NoSuchElementException if this queue is empty
     */
    public Item peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("queue is empty");
        }

        if (out.isEmpty()) {
            while (!in.isEmpty()) {
                out.push(in.pop());
            }
        }
        return out.peek();
    }

    /**
     * Returns an iterator that iterates over the items in this queue in FIFO
     * order. First traverse OUT stack (these are the element at the front of
     * the queue/top of stack). Then traverse IN stack with reverse order
     * (because newer element is at the back of queue/bottom of stack)
     *
     * @return an iterator that iterates over the items in this queue in FIFO
     * order
     */
    public Iterator<Item> iterator()  {
        return new QueueIterator();
    }

    private class QueueIterator implements Iterator<Item> {
        private Stack<Item> tempOut = new Stack<>();
        private Stack<Item> tempIn = new Stack<>();

        public QueueIterator() {
            // Copy item from OUT stack (front of queue)
            for (Item item : out) {
                tempOut.push(item);
            }

            // Copy item from IN stack in reverse order
            Stack<Item> reverseIn = new Stack<>();
            for (Item item : in) {
                reverseIn.push(item);
            }
            while (!reverseIn.isEmpty()) {
                tempIn.push(reverseIn.pop());
            }
        }

        @Override
        public boolean hasNext() {
            return !tempOut.isEmpty() || !tempIn.isEmpty();
        }

        @Override
        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            if (!tempOut.isEmpty()) {
                return tempOut.pop(); // front elements
            } else {
                return tempIn.pop(); // reverse order
            }
        }
    }

    public static void main(String[] args) {
        QueueTwoStacks<Integer> queue = new QueueTwoStacks<>();
        queue.enqueue(2);
        queue.enqueue(5);
        queue.enqueue(8);

        StdOut.println(queue.size());
        StdOut.println("Expected queue order: [2, 5, 8]");

        // Normal for loop standard output [2, 5, 8]
        while (!queue.isEmpty()) {
            StdOut.println(queue.dequeue());
        }

        // Advanced iterator for-each standard output [8, 5, 2]
//        for (int x: queue) {
//            StdOut.println(x);
//        }
    }
}
