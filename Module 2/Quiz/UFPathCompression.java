/* *****************************************************************************
 *  Name:              Dennis Goh Jia Wang
 *  Last modified:     1/10/2025
 *  Interview Question: Union-Find (Quiz)
 **************************************************************************** */

/* *****************************************************************************
 * Union-find with specific canonical element:
 * Add a method find() to the union-find data type so that find(i) returns the
 * largest element in the connected component containing i. The operations,
 * union(), connected(), and find() should all take logarithmic time or better
 **************************************************************************** */

import java.util.Arrays;
import edu.princeton.cs.algs4.StdOut;

/* *****************************************************************************
 * Solution:
 * Time complexity can be improved further with Path Compression algorithm.
 * This is the extension of Union Find data structure with Path Compression.
 **************************************************************************** */

// Extend the previous UFMaxCanonical class to add in a new path compression method
public class UFPathCompression extends UFMaxCanonical {
    // Inherit the superclass constructor
    public UFPathCompression(int n) {
        super(n);
    }

    /**
     * Returns the canonical element of the set containing element {@code p}.
     *
     * @param  p an element
     * @return the canonical element of the set containing {@code p}
     * @throws IllegalArgumentException unless {@code 0 <= p < n}
     */
    /* *****************************************************************************
     * Overriding the root method to flatten the tree
     * 2 method can be used to define this method:
     * 1) Two-pass implementation:  add second loop to root() to set the parent[]
     * of each examined node to the root.
     * 2) Simpler one-pass variant:  Make every other node in path point to its
     * grandparent (thereby halving path length).
     **************************************************************************** */
    @Override
    public int root(int p) {
        super.validate(p);

        // Two-pass implementation
        int root = p;
        while (root != parent[root])
            root = parent[root];
        while (p != root) {
            int newp = parent[p];
            parent[p] = root;
            p = newp;
        }
        return root;

        // Single one-pass variant
        /*
        while (p != parent[p]) {
            parent[p] = parent[parent[p]];
            p = parent[p];
        }
        return p;
         */
    }

    /* *****************************************************************************
     * Weighted Quick Union with Path Compression
     *      parent[] = 0 1 2 3 4 5 6 7 8 9
     * -------------------------------------------------------
     * union(parent[4], parent[3]):
     * No Path Compression (normal union) {3 -> 4}
     * max(root(4), root(3)) => max(4, 3) => root(4) = 4
     *      parent[] = 0 1 2 4 4 5 6 7 8 9
     *      max[]    = 0 1 2 3 4 5 6 7 8 9
     * -------------------------------------------------------
     * union(parent[3], parent[8]):
     * No Path Compression (normal union) {3 -> 4 <- 8}
     * max(root(3), root(8)) => max(4, 8) => root(4) = 8
     *      parent[] = 0 1 2 4 4 5 6 7 4 9
     *      max[]    = 0 1 2 3 8 5 6 7 8 9
     * -------------------------------------------------------
     * union(parent[6], parent[5]):
     * No Path Compression (normal union) {5 -> 6}
     * max(root(6), root(5)) => max(6, 5) => root(6) = 6
     *      parent[] = 0 1 2 4 4 6 6 7 4 9
     *      max[]    = 0 1 2 3 8 5 6 7 8 9
     * -------------------------------------------------------
     * union(parent[9], parent[4]):
     * No Path Compression (normal union) {3 -> 4 <- 8 <-- 9}
     * max(root(9), root(4)) => max(8, 9) => root(4) = 9
     *      parent[] = 0 1 2 4 4 6 6 7 4 4
     *      max[]    = 0 1 2 3 9 5 6 7 8 9
     * -------------------------------------------------------
     * union(parent[2], parent[1]):
     * No Path Compression (normal union) {1 -> 2}
     * max(root(2), root(1)) => max(2, 1) => root(2) = 2
     *      parent[] = 0 2 2 4 4 6 6 7 4 4
     *      max[]    = 0 1 2 3 9 5 6 7 8 9
     * -------------------------------------------------------
     * union(parent[5], parent[0]):
     * No Path Compression (normal union) {0 -> 6 <- 5}
     * max(root(5), root(0)) => max(6, 0) => root(6) = 6
     *      parent[] = 6 2 2 4 4 6 6 7 4 4
     *      max[]    = 0 1 2 3 9 5 6 7 8 9
     * -------------------------------------------------------
     * union(parent[7], parent[2]):
     * No Path Compression (normal union) {1 -> 2 <- 7}
     * max(root(7), root(2)) => max(7, 2) => root(2) = 7
     *      parent[] = 6 2 2 4 4 6 6 2 4 4
     *      max[]    = 0 1 7 3 9 5 6 7 8 9
     * -------------------------------------------------------
     * union(parent[6], parent[1]):
     * No Path Compression (normal union) {2 -> 6}
     * max(root(6), root(1)) => max(6, 7) => root(6) = 7
     *      parent[] = 6 2 6 4 4 6 6 2 4 4
     *      max[]    = 0 1 7 3 9 5 7 7 8 9
     * -------------------------------------------------------
     * union(parent[7], parent[3]):
     * Path Compression (root(7)) => {6 -> 7}
     * max(root(7), root(3)) => max(7, 9) => root(6) = 9
     *      parent[] = 6 2 6 4 6 6 6 6 4 4
     *      max[]    = 0 1 7 3 9 5 9 7 8 9
     **************************************************************************** */
    public static void main(String[] args) {
        UFPathCompression uf = new UFPathCompression(10);
        uf.union(4, 3);
        uf.union(3, 8);
        uf.union(6, 5);
        uf.union(9, 4);
        uf.union(2, 1);
        uf.union(5, 0);
        uf.union(7, 2);
        uf.union(6, 1);
        uf.union(7, 3);

        // System standard output union and max array
        StdOut.println("Union parent array: " + Arrays.toString(uf.parent));
        StdOut.println("Maximum element array: " + Arrays.toString(uf.max));
        StdOut.println("Maximum element of the connected component: " + uf.find(7));
    }
}
