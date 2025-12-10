/* *****************************************************************************
 *  Name:              Dennis Goh Jia Wang
 *  Last modified:     6/12/2025
 *  Interview Question: Stacks and Queues (Quiz)
 **************************************************************************** */

/* *****************************************************************************
 * Stack with max:
 * Create a data structure that efficiently supports the stack operations (push
 * and pop) and also a return-the-maximum operation. Assume the elements are
 * real numbers so that you can compare them.
 **************************************************************************** */
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;
import java.util.NoSuchElementException;

// Assume element are real numbers, therefore no generic class needed
public class MaxStack {
    /* *************************************************************************
     * Auxiliary stack approach:
     * Main stack -> store all elements
     * Auxiliary stack -> store the maximum element at each level
     * 1) Push element into main stacks
     * 2) Update the auxiliary stack for max
     *      a) If auxiliary stack is empty or current element > Top stack element
     *      b) Otherwise push the top stack to keep max unchanged
     * 3) Pop both stack (main & auxiliary stack) to make sure it stay in sync
     * -------------------------------------------------------------------------
     * e.g.
     * 1) Operation: push(18), push(19), push(29), push(15), push(16)
     *      a) main stack: [16, 15, 29, 19, 18]
     *      b) auxiliary stack: [29, 29, 29, 19, 18]
     * 2) Operation: pop(), pop()
     *      a) main stack: [29, 19, 18]
     *      b) auxiliary stack: [29, 19, 18]
     * -------------------------------------------------------------------------
     * Time Complexity:
     * push, pop, max: O(1)
     * -------------------------------------------------------------------------
     * Space complexity: O(n) (two stacks of size n)
     ************************************************************************ */
    public static class AuxiliaryStack {
        private Stack<Integer> mainStack = new Stack<>();
        private Stack<Integer> maxStack = new Stack<>();

        public boolean isEmpty() {
            return mainStack.isEmpty();
        }

        public void push(int x) {
            mainStack.push(x);
            if (maxStack.isEmpty() || x > maxStack.peek()) {
                maxStack.push(x);
            } else {
                maxStack.push(maxStack.peek());
            }
        }

        public int pop() {
            if (isEmpty()) throw new NoSuchElementException("Stack underflow");
            maxStack.pop();
            return mainStack.pop();
        }

        public int max() {
            if (maxStack.isEmpty()) throw new NoSuchElementException("Stack underflow");
            return maxStack.peek();
        }

        // Return top element of main stack
        public int peek() {
            if (isEmpty()) throw new NoSuchElementException("Stack underflow");
            return mainStack.peek();
        }
    }

    /* *************************************************************************
     * Single stack with node pair integer approach:
     * Single stack -> store each entry as node pair containing (current, max)
     * 1) If stack is empty
     *      a) Push Node(element, element) into stack since itself is maximum
     * 2) Otherwise, compare the new element with max (second value of top stack)
     *      a) Push Node(element, max(element, currentMax))
     * Alternative, instead of using Node data type can replace with integer
     * array (int []{element, currentMax})
     * -------------------------------------------------------------------------
     * e.g.
     * 1) Operation: push(18), push(19), push(29), push(15), push(16)
     *    Stack: [(16, 29), (15, 29), (29, 29), (19, 19), (18, 18)]
     * 2) Operation: pop(), pop()
     *    Stack: [(29, 29), (19, 19), (18, 18)]
     * -------------------------------------------------------------------------
     * Time Complexity:
     * push, pop, max: O(1)
     * -------------------------------------------------------------------------
     * Space complexity: O(n) (one stack storing n pairs)
     ************************************************************************ */
    public static class PairStack {
        // Store Node(current element, max element) into stack
        private static class Node {
            int current, currentMax;

            /* *****************************************************************
             * If no modifier, it's package-private (a.k.a. “default”
             * visibility). But since the class itself is private, only the
             * enclosing outer class can see Node anyway. With a private nested
             * class, public vs package-private on the constructor doesn’t
             * change anything for other classes.
             **************************************************************** */
            // If no modifier, it is package-private (a.k.a. “default” visibility)
            Node(int element, int max) {
                this.current = element;
                this.currentMax = max;
            }
        }

        private Stack<Node> stack = new Stack<>();

        public boolean isEmpty() {
            return stack.isEmpty();
        }

        public void push(int x) {
            // Push Node(element, element) if stack is empty
            int max = isEmpty() ? x : Math.max(x, stack.peek().currentMax);
            stack.push(new Node(x, max));
        }

        public int pop() {
            if (isEmpty()) throw new NoSuchElementException("Stack underflow");
            return stack.pop().current;
        }

        public int max() {
            if (isEmpty()) throw new NoSuchElementException("Stack underflow");
            return stack.peek().currentMax;
        }

        // Return top element of main stack
        public int peek() {
            if (isEmpty()) throw new NoSuchElementException("Stack underflow");
            return stack.peek().current;
        }
    }

    /* *************************************************************************
     * Encoding-based stack approach:
     * Stack -> store normal and encoded elements
     * Maintain single variable for current maximum and encoded value
     * 1) Stack is empty (x since itself is maximum)
     *      a) stack push(x)
     *      b) currMax = x
     * 2) Stack push(new x)
     *      a) if x <= currMax
     *          i) push(x) normally
     *      b) else, encode x
     *          i) encoded = (2*x - currMax)
     *          ii) push(encoded)
     *          iii) currMax = x
     * 2) Stack pop()
     *      a) if pop() <= max
     *          i) return pop() normally
     *      b) else, decode prevMax
     *          i) prevMax = (2*currMax - pop())
     *          ii) currMax = prevMax
     * -------------------------------------------------------------------------
     * Encoding/Decoding logic
     * 1) Encoding logic:
     *      a) If new x > currMax, currMax = prevMax
     *      b) encoded = 2*x - currMax
     *                 = 2*x - prevMax
     *         where x is new max, currMax become prevMax.
     *      c) currMax = x (new max)
     * 2) Decoding logic:
     *      a) if pop() > currMax, it is encoded value, pop() = encoded
     *      b) currMax = x (new max)
     *      c) rearrange the equation,
     *         encoded = 2*x - prevMax
     *         prevMax = 2*x - encoded
     *                 = 2*currMax - pop()
     *      d) currMax = prevMax
     * -------------------------------------------------------------------------
     * e.g.
     * 1) push(3) -> currMax = 3
     *    stack: [3]
     * 2) push(5) -> encoded = 2*5 - 3 = 7, currMax = 5
     *    stack: [7, 3]
     * 3) push(8) -> encoded = 2*8 - 5 = 11, currMax = 8
     *    stack: [11, 7, 3]
     * 4) pop() -> 11 > currMax, prevMax = 2*8 - 11 = 5, currMax = 5
     *    stack: [7, 3]
     * -------------------------------------------------------------------------
     * Time Complexity:
     * push, pop, max: O(1)
     * -------------------------------------------------------------------------
     * Space complexity: O(n) (one stack, no extra pair storage)
     ************************************************************************ */
    public static class EncodedStack {
        private Stack<Integer> stack = new Stack<>();
        private int currMax;

        public boolean isEmpty() {
            return stack.isEmpty();
        }

        public void push(int x) {
            // Initial stack is empty
            if (isEmpty()) {
                stack.push(x);
                currMax = x;
            } else if (x > currMax) {
                // Encode the value
                stack.push(2*x - currMax);
                currMax = x;
            } else {
                stack.push(x);
            }
        }

        public int pop() {
            if (isEmpty()) throw new NoSuchElementException("Stack underflow");

            // If top > currMax, it means it is an encoded value
            int top = stack.pop();
            if (top > currMax) {
                int actualVal = currMax;
                currMax = 2*currMax - top;
                return actualVal;
            } else {
                return top;
            }
        }

        public int max() {
            if (isEmpty()) throw new NoSuchElementException("Stack underflow");
            return currMax;
        }

        // Return top element of main stack
        public int peek() {
            if (isEmpty()) throw new NoSuchElementException("Stack underflow");

            // Initialize variable for pop() to avoid multiple pop()
            int top = stack.pop();
            return (top > currMax) ? currMax : top;
        }
    }


    // Test client
    public static void main(String[] args) {
        StdOut.println("Auxiliary Stack:");
        AuxiliaryStack aux = new AuxiliaryStack();
        aux.push(3);
        aux.push(5);
        aux.push(2);
        aux.push(8);
        StdOut.println("Max: " + aux.max()); // 8
        aux.pop();
        StdOut.println("Max after pop: " + aux.max()); // 5

        StdOut.println("\nPair Stack:");
        PairStack pair = new PairStack();
        pair.push(3);
        pair.push(5);
        pair.push(2);
        pair.push(8);
        StdOut.println("Max: " + pair.max()); // 8
        pair.pop();
        StdOut.println("Max after pop: " + pair.max()); // 5

        StdOut.println("\nEncoded Stack:");
        EncodedStack enc = new EncodedStack();
        enc.push(3);
        enc.push(5);
        enc.push(2);
        enc.push(8);
        StdOut.println("Max: " + enc.max()); // 8
        enc.pop();
        StdOut.println("Max after pop: " + enc.max()); // 5
    }

}
