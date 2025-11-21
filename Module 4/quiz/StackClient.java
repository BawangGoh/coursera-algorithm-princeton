import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class StackClient {
    public static void main(String[] args) {
        // Create the instance of stack
        LinkedStackOfStrings linkedlist_stack = new LinkedStackOfStrings();
        FixedCapacityStackOfStrings array_stack = new FixedCapacityStackOfStrings(10);
        NaiveStackOfStrings naive_stack = new NaiveStackOfStrings();
        OptimizedStackOfStrings optimized_stack = new OptimizedStackOfStrings();

        while (!StdIn.isEmpty()) {
            String s = StdIn.readString();
            if (s.equals("-")) {
                StdOut.println("Linked List Stack: " + linkedlist_stack.pop());
                StdOut.println("Fixed Array Stack: " + array_stack.pop());
                StdOut.println("Naive Array Stack: " + naive_stack.pop());
                StdOut.println("Optimized Array Stack: " + optimized_stack.pop());
            }
            else {
                linkedlist_stack.push(s);
                array_stack.push(s);
                naive_stack.push(s);
                optimized_stack.push(s);
            }
        }
    }
}
