/* *****************************************************************************
 *  Name:              Dennis Goh Jia Wang
 *  Last modified:     22/11/2025
 **************************************************************************** */

/* *****************************************************************************
 * Double-ended Queue (Dequeue):
 * A double-ended queue or deque (pronounced as “deck”) is a generalization of a
 * stack and a queue that supports adding and removing items from either the
 * front or the back of the data structure. Create a generic data type Deque
 * using arrays and linked lists, and iterators that implements the following
 * API.
 *
 * Corner Cases. Throw the specified exception for the following corner cases:
 * 1) Throw an IllegalArgumentException if the client calls either addFirst() or
 * addLast() with a null argument.
 * 2) Throw a java.util.NoSuchElementException if the client calls either
 * removeFirst() or removeLast when the deque is empty.
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
 * Deque implementation must support each deque operation (including
 * construction) in constant worst-case time. A deque containing n items must
 * use at most 48n + 192 bytes of memory. Additionally, your iterator
 * implementation must support each operation (including construction) in
 * constant worst-case time.
 *
 * Conclusion:
 * Double-ended queue using linked-list implementation since every operation
 * takes constant time in worst case O(1). The static inner class does not have
 * extra memory overhead (8 bytes) that it does not carry an extra pointer to
 * its enclosing Deque object.
 *
 * Memory Calculation:
 * 1) Each node (48 bytes):
 *      a) Object header/overhead → typically 16 bytes (on 64-bit JVM)
 *      b) Node<Item> next → reference (8 bytes)
 *      c) Item data -> reference (8 bytes on 64-bit JVM with compressed OOPs)
 *      d) Node<Item> prev → reference (8 bytes)
 *      e) Padding for alignment → usually 8 bytes
 * 2) Deque object overhead (192 bytes):
 *      a) Object header → typically 16 bytes (on 64-bit JVM)
 *      b) Node<Item> head -> refrence (8 bytes)
 *      c) Node<Item> tail -> refrence (8 bytes)
 *      d) int size → 4 bytes
 *      e) Padding for alignment → 4 bytes (to align to 8-byte boundary)
 *      f) Object header + Instance fields = 16 + 24 = 40 (rounded to 48 bytes)
 *      g) Extra overhead for references and internal structures (like iterator
 *         object, class metadata, etc.)
 **************************************************************************** */
import java.util.Iterator;
import java.util.NoSuchElementException;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Deque<Item> implements Iterable<Item> {
    // Private attribute for doubly linked-list
    private Node<Item> headDummy;
    private Node<Item> tailDummy;
    private int size;

    /* *************************************************************************
     * Static nested class for Node for better data encapsulation
     *
     * Explanation:
     * A doubly linked list contain pointers to previous node and the next node.
     * 1. Singly Linked List (SLL)
     *    			 _______		 _______         _______
     *    			|	|	|	    |	|	|		|	|	|
     *  root -----> |	| o	|-----> |	| o |----->	|	| o	|-----> null
     *              |___|___|		|___|___|		|___|___|
     *
     * 2. Doubly Linked List (DLL)
     *                   ___________		 ___________         ___________
     *      root ----->	|   |	|	|----->	|   |	|	|----->	|	|	|	|
     *                  | o |	| o	| 		| o |	| o |		| o	| 	| o |-----> null
     *      null <-----	|___|___|___|<-----	|___|___|___|<-----	|___|___|___|
     ************************************************************************ */
    private static class Node<Item> {
        private Node<Item> prev;    // A reference to the previous node
        private Item data;          // Data or a reference to data
        private Node<Item> next;    // A reference to the next node

        /**
         * Constrcutor for root node
         * Set the self-reference of next and prev to null
         *
         * @param data node
         */
        public Node(Item data) {
            this(null, data, null);
        }

        /**
         * Constrcutor for node
         *
         * @param data node
         * @param next reference to next node
         * @param prev reference to previous node
         */
        public Node(Node<Item> prev, Item data, Node<Item> next) {
            this.prev = prev;
            this.data = data;
            this.next = next;
        }

        // Getter and setter for current node
        public Item getData() {
            return data;
        }
        public void setData(Item data) {
            this.data = data;
        }

        // Getter and setter for next node
        public Node<Item> getNext() {
            return next;
        }
        public void setNext(Node<Item> next) {
            this.next = next;
        }

        // Getter and setter for previous node
        public Node<Item> getPrev() {
            return prev;
        }
        public void setPrev(Node<Item> prev) {
            this.prev = prev;
        }
    }

    // construct an empty deque
    public Deque() {
        size = 0;

        /* *********************************************************************
         * Using dummy nodes (sentinel nodes) method will eliminate special
         * handling for null when deque is empty in addFirst & addLast. First
         * and last dummy nodes are created and linked to each other forming a
         * circular structure when deque is empty.
         ******************************************************************** */
        headDummy = new Node<>(null);
        tailDummy = new Node<>(null);
        headDummy.next = tailDummy;
        tailDummy.prev = headDummy;
    }

    /* *************************************************************************
     * Using simplify constructor will have to handle all special cases in each
     * addFirst & addLast method. That added newNode have to check
     * 1) If: the deque is initally empty
     *  if(isEmpty())
     *      head = last = newNode;
     * 2) Else: update the head or tail pointer
     *  else {
     *      newNode.next = head;
     *      head.prev = newNode;
     *      head = newNode;
     *  }
     ************************************************************************ */
//    public Deque() {
//        size = 0;
//        first = last = null;
//    }

    public boolean isEmpty() {
        return size == 0;
    }

    // return the number of items on the deque
    public int size() {
        return size;
    }

    // add the item to the front
    /* *************************************************************************
     * 1. Empty deque (sentinel/dummy nodes exist for each other)
     *               head                tail
     *            ___________		 ___________
     *           |   |   |   |----->|   |	|	|
     * null <----| / | / | / | 		| / | / | / |-----> null
     *           |___|___|___|<-----|___|___|___|
     * 2. Add new nodes (update pointer of the sentinel nodes)
     *               head               current             tail
     *                                  (first)
     *            ___________		 ____________        ___________
     *           |   |   |   |----->|   |	 |	 |----->|   |	|	|
     * null <----| / | / | / |      | o | {} | o |		| / | / | / |-----> null
     *           |___|___|___|<-----|___|___ |___|<-----|___|___|___|
     ************************************************************************ */
    public void addFirst(Item item) {
        if (item == null) throw new IllegalArgumentException("Illegal null argument");

        // Set the first node next to head dummy node
        Node<Item> first = headDummy.getNext();

        // Insert new node between head dummy and first node
        Node<Item> curr = new Node<>(headDummy, item, first);

        // Update the remaining pointer of head and dummy node
        headDummy.setNext(curr);
        first.setPrev(curr);
        size++;
    }

    // add the item to the back
    /* *************************************************************************
     * 1. Empty deque (sentinel/dummy nodes exist for each other)
     *               head                tail
     *            ___________		 ___________
     *           |   |   |   |----->|   |	|	|
     * null <----| / | / | / | 		| / | / | / |-----> null
     *           |___|___|___|<-----|___|___|___|
     * 2. Add new nodes (update pointer of the sentinel nodes)
     *               head               current             tail
     *                                  (last)
     *            ___________		 ____________        ___________
     *           |   |   |   |----->|   |	 |	 |----->|   |	|	|
     * null <----| / | / | / |      | o | {} | o |		| / | / | / |-----> null
     *           |___|___|___|<-----|___|___ |___|<-----|___|___|___|
     ************************************************************************ */
    public void addLast(Item item) {
        if (item == null) throw new IllegalArgumentException("Illegal null argument");

        // Set the last node next to head dummy node
        Node<Item> last = tailDummy.getPrev();

        // Insert new node between last and tail dummy node
        Node<Item> curr = new Node<>(last, item, tailDummy);

        // Update the remaining pointer of last and dummy node
        last.setNext(curr);
        tailDummy.setPrev(curr);
        size++;
    }

    // remove and return the item from the front
    /* *************************************************************************
     * 1) Get the first element next to head dummy nodes
     *               head               remove             tail
     *            ___________		 ____________        ___________
     *           |   |   |   |----->|   |	 |	 |----->|   |	|	|
     * null <----| / | / | / |      | o | {} | o |		| / | / | / |-----> null
     *           |___|___|___|<-----|___|___ |___|<-----|___|___|___|
     * 2) Update the pointer of head dummy nodes to next removed nodes
     *               head                tail
     *            ___________        ___________
     *           |   |   |   |----->|   |	|	|
     * null <----| / | / | / |		| / | / | / |-----> null
     *           |___|___|___|<-----|___|___|___|
     ************************************************************************ */
    public Item removeFirst() {
        if (isEmpty())  throw new NoSuchElementException("deque is empty");

        // Remove the node right next to head dummy node
        Node<Item> remove = headDummy.getNext();

        // New first node will be the node after removed nodes
        Node<Item> first = remove.getNext();

        // Update the pointer of head dummy node
        headDummy.setNext(first);
        first.setPrev(headDummy);

        // Update removed node pointer to null (to avoid loitering)
        remove.setNext(null);
        remove.setPrev(null);
        size--;
        return remove.getData();
    }

    // remove and return the item from the back
    /* *************************************************************************
     * 1) Get the last element before tail dummy nodes
     *               head               remove             tail
     *            ___________		 ____________        ___________
     *           |   |   |   |----->|   |	 |	 |----->|   |	|	|
     * null <----| / | / | / |      | o | {} | o |		| / | / | / |-----> null
     *           |___|___|___|<-----|___|___ |___|<-----|___|___|___|
     * 2) Update the pointer of tail dummy nodes before removed nodes
     *               head                tail
     *            ___________        ___________
     *           |   |   |   |----->|   |	|	|
     * null <----| / | / | / |		| / | / | / |-----> null
     *           |___|___|___|<-----|___|___|___|
     ************************************************************************ */
    public Item removeLast() {
        if (isEmpty())  throw new NoSuchElementException("deque is empty");

        // Remove the node right before to tail dummy node
        Node<Item> remove = tailDummy.getPrev();

        // New last node will be the node before removed nodes
        Node<Item> last = remove.getPrev();

        // Update the pointer of tail dummy node
        tailDummy.setPrev(last);
        last.setNext(tailDummy);

        // Update removed node pointer to null (to avoid loitering)
        remove.setNext(null);
        remove.setPrev(null);
        size--;
        return remove.getData();
    }

    // return an iterator over items in order from front to back
    @Override
    public Iterator<Item> iterator() {
        /* *********************************************************************
         * For better flexibility and maintainability use named inner class.
         * Using anonymous inner class that define inline inside iterator()
         * method is harder to reuse or extend
         * return new Iterator<Item> {
         *      private Node<item> point = headDummy.next;
         *
         *      @Override
         *      public boolean hasNext() {}
         *
         *      @Override
         *      public Item next() {}
         * }
         * First node start after head dummy nodes.
         **********************************************************************/
        return new LinkedIterator(headDummy.getNext());
    }

    // a linked-list iterator
    private class LinkedIterator implements Iterator<Item> {
        private Node<Item> current;

        public LinkedIterator(Node<Item> first) {
            current = first;
        }

        @Override
        public boolean hasNext() {
            // Stop before tailDummy
            return current != tailDummy;
        }

        @Override
        public Item next() {
            if (!hasNext()) throw new NoSuchElementException("No more items");
            Item item = current.getData();
            current = current.getNext();
            return item;
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        // Test deque function
        Deque<Integer> deque = new Deque<>();
        StdOut.println("Deque is empty: " + deque.isEmpty());
        deque.addFirst(3);
        deque.addFirst(4);
        deque.addLast(8);
        StdOut.println("Deque is not empty: " + deque.isEmpty());
        StdOut.println("Deque size of 3: " + deque.size());
        StdOut.println("Removed first element '4': " + deque.removeFirst());
        StdOut.println("Removed first element '8': " + deque.removeLast());

        // Unit test for iterator function
        deque.addFirst(12);
        deque.addLast(15);
        StdOut.println("Deque contain element '12, 3, 15': ");
        for (int i : deque) {
            StdOut.print(i + " ");
        }
        StdOut.println();

        // Test deque for different type
        Deque<String> strDeque = new Deque<>();
        String expected = "hello";
        strDeque.addFirst(expected);
        StdOut.println(expected + " = " + strDeque.removeLast());
    }
}
