/* *****************************************************************************
 *  Name:              Dennis Goh Jia Wang
 *  Last modified:     29/1/2026
 *  Interview Question: Mergesort (Quiz)
 **************************************************************************** */

/* *****************************************************************************
 * Shuffling a linked list:
 * Given a singly-linked list containing n items, rearrange the items uniformly
 * at random. Your algorithm should consume a logarithmic (or constant) amount
 * of extra memory and run in time proportional to n log n in the worst case.
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.SplittableRandom;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ThreadLocalRandom;

/* *****************************************************************************
 * Shuffling linked list approach:
 * 1) Merge shuffler
 * 2) Merge shuffler with threading
 *      Merge shuffling achive O(n log n) time, uses optimal O(log n) space on
 *      recursion stack
 * 3) Fisher-Yates shuffler
 * 4) Fisher-Yates value shuffler
 *      Fisher-Yates shuffling use auxiliary array and random access resulting
 *      O(n) time/space
 * 5) Collections shuffler
 *      Use Java built-in collections shuffle on array of nodes, O(n) time/space
 * -----------------------------------------------------------------------------
 * Use final on this class to prevent subclassing (extension)
 * 1) avoids accidental modification of the algorithm
 * 2) improves encapsulation and predictability
 * 3) align with utility classes that aren't meant to be frameworks
 **************************************************************************** */
public final class ShuffleLinkedList {
    // Static utility library with static method not meant to be instantiated.
    private ShuffleLinkedList() {}

    // Singly-linked list node (Initialized once)
    public static final class Node<T> {
        public T val;
        public Node<T> next;
        public Node(T val) {
            this.val = val;
        }
        public Node(T val, Node<T> next) {
            this.val = val;
            this.next = next;
        }
    }

    // Implement interface-based design for different shuffling strategies
    public interface Shuffler<T> {
        // Return head node to print the linked-list in client testing
        Node<T> shuffle(Node<T> head);
    }

    /* *************************************************************************
     * Merge Shuffler Approach: Divide and Conqueur
     * 1) Recursively split linked list into half until a node left
     * 2) Randomly merge the two halves by taking the next node from left vs
     * right with probability proportional to remaining node in that half:
     *      Pr(take from left) = a/(a + b);     Pr(take from right) = b/(a + b)
     * where a and b are remaining size of left and right halves
     * -------------------------------------------------------------------------
     * Time complexity:
     * O(n log n) - log n on splitting linked list + n on merge
     * -------------------------------------------------------------------------
     * Space complexity:
     * O(log n) - no extra array used, all on recursive stack
     ************************************************************************ */
    public static final class MergeShuffler<T> implements Shuffler<T> {
        // Sequential merge shuffler can utilize StdRandom
        public MergeShuffler() {}
        public MergeShuffler(long seed) {
            StdRandom.setSeed(seed);
        }

        // Override interface method
        @Override
        public Node<T> shuffle(Node<T> head) {
            int n = length(head);
            if (n <= 1) return head;
            return shuffleRecursive(head, n);
        }

        private Node<T> shuffleRecursive(Node<T> head, int n) {
            if (n <= 1) return head;

            int leftSize = n / 2;
            int rightSize = n - leftSize;

            // Split the linked list in middle (divide and conquer)
            Node<T> rightHead = splitAfter(head, leftSize);
            Node<T> leftShuffle = shuffleRecursive(head, leftSize);
            Node<T> rightShuffle = shuffleRecursive(rightHead, rightSize);

            return randomMerge(leftShuffle, leftSize, rightShuffle, rightSize);
        }

        // Merge by taking probability from left a/(a+b) and right b/(a+b)
        public Node<T> randomMerge(Node<T> left, int a, Node<T> right, int b) {
            // Initialize sentinel node at the end of linked list
            Node<T> dummy = new Node<>(null);
            Node<T> tail = dummy;
            Node<T> leftNode = left, rightNode = right;
            int leftRemain = a, rightRemain = b;

            // If both sub-linked lists are non-empty
            while (leftRemain > 0 && rightRemain > 0) {
                int choose = StdRandom.uniformInt(leftRemain + rightRemain);

                // Check if chosen node is from left or right
                if (choose < leftRemain) {
                    tail.next = leftNode;
                    leftNode = leftNode.next;
                    leftRemain--;
                } else {
                    tail.next = rightNode;
                    rightNode = rightNode.next;
                    rightRemain--;
                }

                // Transversing tail to current left or right node
                tail = tail.next;
            }

            // Edge case: When one of the sub-linked list is exhausted, then
            // linked the remaining nodes
            tail.next = (leftRemain > 0) ? leftNode : rightNode;

            // Dummy nodes always pointing to the head of shuffled linked list
            return dummy.next;
        }
    }

    /* *************************************************************************
     * Merge Shuffler Parallel Approach: (Non-deterministic)
     * 1) Parallel shuffling the left and right halves in separate tasks.
     * 2) Uniformity preserved on random merge that uses probability
     *      Pr(take from left) = a/(a + b);     Pr(take from right) = b/(a + b)
     * where a and b are remaining size of left and right halves
     * 3) Use ThreadLocalRandom (fast and thread-safe), however if using it in
     * single thread is same as using Random/StdRandom in sequential
     * 4) Random is also thread-safe but uses internal synchronization resulting
     * in slower performance
     * 5) StdRandom is static and shared require external synchronization (e.g
     * synchronized method)
     * 6) ThreadLocalRandom cannot get deterministic reproducible result as it
     * is not seedable
     * 7) Each worker thread RNG is initialized from internal, non-deterministic
     * state that parallel execution in task scheduling lead to different random
     * sequences
     * -------------------------------------------------------------------------
     * Time complexity:
     * O(n log n) - log n on splitting linked list + n on merge
     * -------------------------------------------------------------------------
     * Space complexity:
     * O(log n) - no extra array used, all on recursive stack
     ************************************************************************ */
    public static final class MergeShufflerParallel<T> implements Shuffler<T> {
        private final ForkJoinPool pool;
        private final int parallelThreshold;

        /**
         * @param pool ForkJoinPool to use (commonPool by default).
         * @param parallelThreshold below this size, execute sequentially to reduce overhead.
         */
        public MergeShufflerParallel() {
            this(ForkJoinPool.commonPool(), 1 << 10); // default threshold: 1024
        }

        public MergeShufflerParallel(ForkJoinPool pool, int parallelThreshold) {
            this.pool = (pool != null) ? pool : ForkJoinPool.commonPool();
            this.parallelThreshold = Math.max(2, parallelThreshold);
        }

        // Override interface method
        @Override
        public Node<T> shuffle(Node<T> head) {
            int n = length(head);
            if (n <= 1) return head;

            // Invoke a new thread from thread pool
            return pool.invoke(new ShuffleTask(head, n));
        }

        // Fork/Join task that shuffles a sublist of length n.
        private final class ShuffleTask extends RecursiveTask<Node<T>> {
            private final Node<T> head;
            private final int n;

            ShuffleTask(Node<T> head, int n) {
                this.head = head;
                this.n = n;
            }

            // Must override (protected abstract T compute()) method
            @Override
            protected Node<T> compute() {
                if (n <= 1) return head;

                // Small tasks: do sequential to avoid task overhead
                if (n <= parallelThreshold) {
                    return sequentialShuffle(head, n);
                }

                int leftSize = n / 2;
                int rightSize = n - leftSize;
                Node<T> rightHead = splitAfter(head, leftSize);

                // Parallelize: fork one half, compute the other, then join
                ShuffleTask leftTask = new ShuffleTask(head, leftSize);
                ShuffleTask rightTask = new ShuffleTask(rightHead, rightSize);
                leftTask.fork();

                Node<T> rightShuffled = rightTask.compute();
                Node<T> leftShuffled  = leftTask.join();

                return randomMerge(leftShuffled, leftSize, rightShuffled, rightSize);
            }
        }

        // Sequential fallback used by small tasks
        private Node<T> sequentialShuffle(Node<T> head, int n) {
            if (n <= 1) return head;

            int leftSize = n / 2;
            int rightSize = n - leftSize;

            // Split the linked list in middle (divide and conquer)
            Node<T> rightHead = splitAfter(head, leftSize);
            Node<T> leftShuffled = sequentialShuffle(head, leftSize);
            Node<T> rightShuffled = sequentialShuffle(rightHead, rightSize);

            return randomMerge(leftShuffled, leftSize, rightShuffled, rightSize);
        }

        // Merge by taking probability from left a/(a+b) and right b/(a+b)
        private Node<T> randomMerge(Node<T> left, int a, Node<T> right, int b) {
            Node<T> dummy = new Node<>(null);
            Node<T> tail = dummy;

            Node<T> leftNode = left, rightNode = right;
            int leftRemain = a, rightRemain = b;

            while (leftRemain > 0 && rightRemain > 0) {
                // Thread-local RNG: safe & fast for parallel merges
                int choose = ThreadLocalRandom.current().nextInt(leftRemain + rightRemain);
                if (choose < leftRemain) {
                    tail.next = leftNode;
                    leftNode = leftNode.next;
                    leftRemain--;
                } else {
                    tail.next = rightNode;
                    rightNode = rightNode.next;
                    rightRemain--;
                }
                tail = tail.next;
            }

            // Merge the left or right sub-linked list if either one exhuasted
            tail.next = (leftRemain > 0) ? leftNode : rightNode;
            return dummy.next;
        }
    }

    /* *************************************************************************
     * Merge Shuffler Parallel Approach: (Deterministic)
     * 1) Parallel shuffling the left and right halves in separate tasks.
     * 2) Uniformity preserved on random merge that uses probability
     *      Pr(take from left) = a/(a + b);     Pr(take from right) = b/(a + b)
     * where a and b are remaining size of left and right halves
     * 3) Use SplittableRandom (seedable and splittable RNG) for deterministic
     * reproducible results
     * 4) Each task/child receives own RNG, derived from a single master seed
     * via split() on root task.
     * 5) Even if scheduler interleaves task differently, random stream by each
     * subproblem is fixed by the recursive shape (reproducible for same input
     * and seed)
     * 6) For thread-safe and contention-free, always used ThreadLocalRandom/
     * SplittableRandom for RNG in multithreading
     * -------------------------------------------------------------------------
     * Time complexity:
     * O(n log n) - log n on splitting linked list + n on merge
     * -------------------------------------------------------------------------
     * Space complexity:
     * O(log n) - no extra array used, all on recursive stack
     ************************************************************************ */
    public static final class MergeShufflerSplittable<T> implements Shuffler<T> {
        private final ForkJoinPool pool;
        private final int parallelThreshold;
        private final long seed;

        /**
         * @param pool ForkJoinPool to use (commonPool by default).
         * @param parallelThreshold below this size, execute sequentially to reduce overhead.
         */
        public MergeShufflerSplittable(long seed) {
            this(ForkJoinPool.commonPool(), 1 << 10, seed); // default threshold: 1024
        }

        public MergeShufflerSplittable(ForkJoinPool pool, int parallelThreshold, long seed) {
            this.pool = (pool != null) ? pool : ForkJoinPool.commonPool();
            this.parallelThreshold = Math.max(2, parallelThreshold);
            this.seed = seed;
        }

        // Override interface method
        @Override
        public Node<T> shuffle(Node<T> head) {
            int n = length(head);
            if (n <= 1) return head;

            // Invoke a new thread from thread pool and parse splittable seed
            return pool.invoke(new ShuffleTask(head, n, new SplittableRandom(seed)));
        }

        // Fork/Join subtask that shuffles a sublist of length n.
        private final class ShuffleTask extends RecursiveTask<Node<T>> {
            private final Node<T> head;
            private final int n;
            private final SplittableRandom rng;     // per-task RNG

            ShuffleTask(Node<T> head, int n, SplittableRandom rng) {
                this.head = head;
                this.n = n;
                this.rng = rng;
            }

            // Must override (protected abstract T compute()) method
            @Override
            protected Node<T> compute() {
                if (n <= 1) return head;

                // Small tasks: do sequential to avoid task overhead
                if (n <= parallelThreshold) {
                    return sequentialShuffle(head, n, rng);
                }

                int leftSize = n / 2;
                int rightSize = n - leftSize;
                Node<T> rightHead = splitAfter(head, leftSize);

                // Split current RNG for child tasks (deterministic derivation)
                SplittableRandom leftRng = rng.split();
                SplittableRandom rightRng = rng.split();

                // Parallelize: fork one half, compute the other, then join
                ShuffleTask leftTask = new ShuffleTask(head, leftSize, leftRng);
                ShuffleTask rightTask = new ShuffleTask(rightHead, rightSize, rightRng);
                leftTask.fork();

                Node<T> rightShuffled = rightTask.compute();
                Node<T> leftShuffled  = leftTask.join();

                // Use this task's rng for merge, or rng.split() if you prefer strict separation
                return randomMerge(leftShuffled, leftSize, rightShuffled, rightSize, rng);
            }
        }

        // Sequential fallback used by small tasks
        private Node<T> sequentialShuffle(Node<T> head, int n, SplittableRandom rng) {
            if (n <= 1) return head;

            int leftSize = n / 2;
            int rightSize = n - leftSize;

            // Split current RNG for child tasks (deterministic derivation)
            SplittableRandom leftRng = rng.split();
            SplittableRandom rightRng = rng.split();

            // Split the linked list in middle (divide and conquer)
            Node<T> rightHead = splitAfter(head, leftSize);
            Node<T> leftShuffled = sequentialShuffle(head, leftSize, leftRng);
            Node<T> rightShuffled = sequentialShuffle(rightHead, rightSize, rightRng);

            return randomMerge(leftShuffled, leftSize, rightShuffled, rightSize, rng);
        }

        // Merge by taking probability from left a/(a+b) and right b/(a+b)
        private Node<T> randomMerge(Node<T> left, int a, Node<T> right, int b, SplittableRandom rng) {
            Node<T> dummy = new Node<>(null);
            Node<T> tail = dummy;

            Node<T> leftNode = left, rightNode = right;
            int leftRemain = a, rightRemain = b;

            while (leftRemain > 0 && rightRemain > 0) {
                // Thread-local RNG: safe & fast for parallel merges
                int choose = rng.nextInt(leftRemain + rightRemain);
                if (choose < leftRemain) {
                    tail.next = leftNode;
                    leftNode = leftNode.next;
                    leftRemain--;
                } else {
                    tail.next = rightNode;
                    rightNode = rightNode.next;
                    rightRemain--;
                }
                tail = tail.next;
            }

            // Merge the left or right sub-linked list if either one exhuasted
            tail.next = (leftRemain > 0) ? leftNode : rightNode;
            return dummy.next;
        }
    }

    /* *************************************************************************
     * Fisher-Yates Shuffler Approach (Easier): Using array
     * 1) Must copy the nodes into a dynamic array/list
     * 2) Apply Fisher-Yates/Knuth shuffle to the array, swap i with a random
     * j in [0, i] where i = length(linked list)
     * 3) Rebuild the linked-list pointer from the shuffled array
     * -------------------------------------------------------------------------
     * Time complexity:
     * O(n) - straightforward just shuffle the nodes in array list
     * -------------------------------------------------------------------------
     * Space complexity:
     * O(n) - extra space on using dynamic ArrayList<> to store nodes
     * -------------------------------------------------------------------------
     * Drawback:
     * If do not copy the nodes into ArrayList<>, then Fisher-Yates could not
     * perform key operations (random access + swap) between index i and random
     * index j. It will result in O(n^2) operation.
     * E.g. Linked List
     * for i = n - 1; i > 0; i--:   O(n^2)
     *  int j = random in [0, i]
     *  nodeI = getNodeAt(i) --> transversing list O(n)
     *  nodeJ = getNodeAt(j) --> transversing list O(n)
     *  swap(nodeI, nodeJ)
     ************************************************************************ */
    public static final class FYNodeShuffler<T> implements Shuffler<T> {
        public FYNodeShuffler() {}
        public FYNodeShuffler(long seed) {
            StdRandom.setSeed(seed);
        }

        @Override
        public Node<T> shuffle(Node<T> head) {
            if (head == null || head.next == null) return head;

            // Copy the entire linked list into array list
            List<Node<T>> nodes = new ArrayList<>();
            for (Node<T> curr = head; curr != null; curr = curr.next) {
                nodes.add(curr);
            }

            // Fisher-Yates shuffling: swap i with random j [0, i]
            for (int i = nodes.size() - 1; i > 0; i--) {
                int j = StdRandom.uniformInt(i) + 1;
                Node<T> tmp = nodes.get(i);
                nodes.set(i, nodes.get(j));
                nodes.set(j, tmp);
            }

            // Rebuild the entire link of shuffled array
            for (int i = 0; i < nodes.size() - 1; i++) {
                nodes.get(i).next = nodes.get(i+1);
            }
            nodes.get(nodes.size() - 1).next = null;
            return nodes.get(0);
        }
    }

    /* *************************************************************************
     * Fisher-Yates Values Shuffler Approach: (simplest if identity irrelevant)
     * 1) Must copy node values into a dynamic array/list
     * 2) Apply Fisher-Yates/Knuth shuffle to the array, swap i with a random
     * j in [0, i] where i = length(linked list)
     * 3) Rewrite the values back into existing nodes (pointers unchanged)
     * -------------------------------------------------------------------------
     * Time complexity:
     * O(n) - straightforward just shuffle the values in array list
     * -------------------------------------------------------------------------
     * Space complexity:
     * O(n) - extra space on using dynamic ArrayList<> to store node values
     * -------------------------------------------------------------------------
     * Drawback:
     * Similar drawback to classic Fisher-Yates nodes shuffling
     ************************************************************************ */
    public static final class FYValueShuffler<T> implements Shuffler<T> {
        public FYValueShuffler() {}
        public FYValueShuffler(long seed) {
            StdRandom.setSeed(seed);
        }

        @Override
        public Node<T> shuffle(Node<T> head) {
            if (head == null | head.next == null) return head;

            // Copy the entire linked list values into array list
            List<T> values = new ArrayList<>();
            for (Node<T> curr = head; curr != null; curr = curr.next) {
                values.add(curr.val);
            }

            // Fisher-Yates shuffling values: swap i with random j [0, i]
            for (int i = values.size() - 1; i > 0; i--) {
                int j = StdRandom.uniformInt(i) + 1;
                T tmp = values.get(i);
                values.set(i, values.get(j));
                values.set(j, tmp);
            }

            // Rewrite values back into existing nodes without changing pointers
            int k = 0;
            for (Node<T> curr = head; curr != null; curr = curr.next) {
                curr.val = values.get(k++);
            }
            return head;
        }
    }

    /* *************************************************************************
     * Collection built-in shuffler Approach:
     * 1) Must copy node values into a dynamic array/list
     * 2) Using collections.shuffle (Fisher-Yates shuffling internally)
     * 3) Rebuild the linked-list pointer from the shuffled array
     * -------------------------------------------------------------------------
     * Time complexity:
     * O(n) - straightforward just shuffle in collections ArrayList
     * -------------------------------------------------------------------------
     * Space complexity:
     * O(n) - extra space on using dynamic ArrayList<> to store node
     ************************************************************************ */
    public static final class CollectionsShuffler<T> implements Shuffler<T> {
        // Collections shuffle require Random object as random number generator
        private final Random rng;

        public CollectionsShuffler() {
            this.rng = new Random();
        }
        public CollectionsShuffler(long seed) {
            this.rng = new Random(seed);
        }

        @Override
        public Node<T> shuffle(Node<T> head) {
            if (head == null || head.next == null) return head;

            List<Node<T>> nodes = new ArrayList<>();
            for (Node<T> curr = head; curr != null; curr = curr.next) {
                nodes.add(curr);
            }

            // Using collections built-in shuffler (require Random object)
            Collections.shuffle(nodes, rng);

            // Rebuild the entire link of shuffled array
            for (int i = 0; i < nodes.size() - 1; i++) {
                nodes.get(i).next = nodes.get(i+1);
            }
            nodes.get(nodes.size() - 1).next = null;
            return nodes.get(0);
        }
    }

    /* *************************************************************************
     * Helper utlities function
     ************************************************************************ */
    public static <T> int length(Node<T> head) {
        int n = 0;
        for (Node<T> curr = head; curr != null; curr = curr.next) n++;
        return n;
    }

    public static <T> void printList(Node<T> head) {
        for (Node<T> curr = head; curr != null; curr = curr.next) {
            StdOut.print(curr.val + (curr.next != null ? " -> " : ""));
        }
        StdOut.println();
    }

    // Split and cut linked list after k nodes return (k + 1)th node as head
    private static <T> Node<T> splitAfter(Node<T> head, int k) {
        Node<T> curr = head;
        for (int i = 1; i < k; i++) {
            curr = curr.next;
        }
        Node<T> second = curr.next;
        curr.next = null;
        return second;
    }

    // Clone list helper to prevent mutating original list
    public static <T> Node<T> cloneList(Node<T> head) {
        if (head == null) return null;
        Node<T> dummy = new Node<>(null);
        Node<T> tail = dummy;
        for (Node<T> curr = head; curr != null; curr = curr.next) {
            tail.next = new Node<>(curr.val);
            tail = tail.next;
        }

        // Return sentinel node which before tail or head node
        return dummy.next;
    }

    public static void main(String[] args) {
        // Build list: 1 -> 2 -> ... -> 10
        Node<Integer> head = null;
        for (int i = 10; i >= 1; i--)
            head = new Node<>(i, head);
        StdOut.println("Original:");
        printList(head);

        // Choose and run any strategy:
        Shuffler<Integer> merge = new MergeShuffler<>(42L);
        Shuffler<Integer> mergeParallel = new MergeShufflerParallel<>();
        Shuffler<Integer> mergeSplittable = new MergeShufflerSplittable<>(42L);
        Shuffler<Integer> fyNodes = new FYNodeShuffler<>(42L);
        Shuffler<Integer> fyValues = new FYValueShuffler<>(42L);
        Shuffler<Integer> collNodes = new CollectionsShuffler<>(42L);

        // clone if you want to compare
        Node<Integer> shuffled1 = merge.shuffle(cloneList(head));
        Node<Integer> shuffled2 = mergeParallel.shuffle(cloneList(head));
        Node<Integer> shuffled3 = mergeSplittable.shuffle(cloneList(head));
        Node<Integer> shuffled4 = fyNodes.shuffle(cloneList(head));
        Node<Integer> shuffled5 = fyValues.shuffle(cloneList(head));
        Node<Integer> shuffled6 = collNodes.shuffle(cloneList(head));

        StdOut.println("Merge-shuffle:");
        printList(shuffled1);

        StdOut.println("Merge-shuffle (non-deterministic):");
        printList(shuffled2);

        StdOut.println("Merge-shuffle (deterministic with splittable seed):");
        printList(shuffled3);

        StdOut.println("Fisher–Yates (nodes):");
        printList(shuffled4);

        StdOut.println("Fisher–Yates (values):");
        printList(shuffled5);

        StdOut.println("Collections.shuffle (nodes):");
        printList(shuffled6);
    }
}
