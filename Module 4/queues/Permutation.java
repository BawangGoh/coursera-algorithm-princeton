/* *****************************************************************************
 *  Name:              Dennis Goh Jia Wang
 *  Last modified:     22/11/2025
 **************************************************************************** */

/* *****************************************************************************
 * Permutation client program:
 * A program takes an integer k as a command-line argument; reads a sequence of
 * strings from standard input using StdIn.readString(); and prints exactly k of
 * them, uniformly at random. Print each item from the sequence at most once.
 *
 * Command-line argument:
 * Assume that 0 ≤ k ≤ n, where n is the number of string on standard input.
 * Note that n is not given.
 *
 * Performance requirements:
 * The running time of Permutation must be linear in the size of the input. You
 * may use only a constant amount of memory plus either one Deque or
 * RandomizedQueue object of maximum size at most n. (For an extra challenge and
 * a small amount of extra credit, use only one Deque or RandomizedQueue object
 * of maximum size at most k.)
 **************************************************************************** */
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class Permutation {
    public static void main(String[] args) {
        RandomizedQueue<String> queue = new RandomizedQueue<>();
        int k = Integer.parseInt(args[0]);

        /* *********************************************************************
         * Approach 1: Object size at most n
         * 1) Read all string into a queue
         * 2) Dequeue k items randomly
         * ---------------------------------------------------------------------
         * Time complexity:
         * Read items -> O(n) for all standard input
         * Enqueue -> O(1) amortized per item
         * Dequeue -> O(1) amortized per item
         * ---------------------------------------------------------------------
         * Space complexity:
         * Queue -> O(n) hold n items
         ******************************************************************** */
//        // Read standard input and store into random queue
//        while (!StdIn.isEmpty()) {
//            queue.enqueue(StdIn.readString());
//        }
//
//        // Print k items randomly (print using iterator will give false result
//        // since it utilize full queue)
//        for (int i = 0; i < k; i++) {
//            StdOut.println(queue.dequeue());
//        }


        /* *********************************************************************
         * Approach 2: Object size at most k (Reservoir sampling/Knuth sampling)
         * 1) Maintain a randomized queue of size k
         * 2) When a new item arrives and queue is full
         *      a) Replace the item with probability of k/i (where i is the
         *         count of item)
         *      b) Otherwise fewer item than k, enqueue item
         * ---------------------------------------------------------------------
         * Time complexity:
         * Read items -> O(n) for all standard input
         * Calculate probability -> O(1) constant time operation
         * Enqueue -> O(1) amortized per item
         * Dequeue -> O(1) amortized per item
         * ---------------------------------------------------------------------
         * Space complexity:
         * Queue -> O(k) hold k items
         * ---------------------------------------------------------------------
         * Reservoir sampling algorithm:
         * Select k items uniformly at random from a stream of n items, without
         * knowing n items in advance.
         * 1) Fill the reservoir with k items.
         * 2) Each subsequent item (where i > k)
         *      a) Generate a random integer r from [0, i]
         *      b) if r < k, replace the item at index r in the reservoir with
         *         new items
         * This ensure every item has equal probability of being in final sample
         * ---------------------------------------------------------------------
         * Example:
         * Let suppose k = 3, stream = [A, B, C, D, E]
         * 1) Add A, B, C -> reservoir queue = [A, B, C]
         * 2) Item D (i = 4), sample the stream index with probability of
         * (k/i = 3/4) for each item
         * 3) If r < 3, replace random item in reservoir with D
         * 4) Item E (i = 5), sample the stream index with probability of
         * (k/i = 3/5) for each item
         * 5) If r < 3, replace random item in reservoir with E
         ******************************************************************** */
        int count = 0;
        while (!StdIn.isEmpty()) {
            String item = StdIn.readString();
            count++;

            // Filling up intial reservoir with k items
            if (queue.size() < k) {
                queue.enqueue(item);
            } else {
                // Sample the stream item with probability of 1/count
                int randIdx = StdRandom.uniformInt(count);

                // Probability of replacement = k / i (because there are k
                // favorable outcomes out of i possible values).
                if (randIdx < k) {
                    queue.dequeue();
                    queue.enqueue(item);
                }
            }
        }

        // Print queue with iterator since it already specified with only k item
        for (String a : queue) {
            StdOut.println(a);
        }
    }
}
