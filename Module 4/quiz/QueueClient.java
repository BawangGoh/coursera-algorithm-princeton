import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class QueueClient {
    public static void main(String[] args) {
        // Create the instance of queue
        LinkedQueueOfStrings linkedlist_queue = new LinkedQueueOfStrings();
        OptimizedQueueOfStrings optimized_queue = new OptimizedQueueOfStrings();

        while (!StdIn.isEmpty()) {
            String s = StdIn.readString();
            if (s.equals("-")) {
                StdOut.println("Linked List Queue: " + linkedlist_queue.dequeue());
                StdOut.println("Optimized Array Queue: " + optimized_queue.dequeue());
            }
            else {
                linkedlist_queue.enqueue(s);
                optimized_queue.enqueue(s);
            }
        }
    }
}
