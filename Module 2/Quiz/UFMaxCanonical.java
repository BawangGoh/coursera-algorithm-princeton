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
 * Maintain an extra array to the weighted quick union data structure that stores
 * for each root i the largest element in the connected component containing i.
 * Implementation should at least use weighted quick union because quick find has
 * O(N) time complexity in union operation and quick union has O(N) in worst case
 * union operation for its non-optimized tree height. Time complexity can be
 * improved further with Path Compression algorithm
 **************************************************************************** */

// Create weighted quick union data structure
public class UFMaxCanonical {
    protected int[] max;      // max[i] = largest element in connected component i
    protected int[] parent;   // parent[i] = parent of i
    protected int[] size;     // size[i] = number of elements in subtree rooted at i
    protected int count;      // number of components

    /**
     * Initializes an empty union-find data structure with
     * {@code n} elements {@code 0} through {@code n-1}.
     * Initially, each element is in its own set.
     *
     * @param  n the number of elements
     * @throws IllegalArgumentException if {@code n < 0}
     */
    public UFMaxCanonical(int n) {
        count = n;
        parent = new int[n];
        size = new int[n];
        max = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;  // Initially, each node are parent itself
            size[i] = 1;
            max[i] = i;     // Initally, each node are maximum itself
        }
    }

    /**
     * Returns true if the two elements are in the same set.
     *
     * @param  p one element
     * @param  q the other element
     * @return {@code true} if {@code p} and {@code q} are in the same set;
     *         {@code false} otherwise
     * @throws IllegalArgumentException unless
     *         both {@code 0 <= p < n} and {@code 0 <= q < n}
     */
    public boolean connected(int p, int q) {
        return root(p) == root(q);
    }

    /**
     * Returns the canonical element of the set containing element {@code p}.
     *
     * @param  p an element
     * @return the canonical element of the set containing {@code p}
     * @throws IllegalArgumentException unless {@code 0 <= p < n}
     */
    public int root(int p) {
        validate(p);
        while (p != parent[p])
            p = parent[p];
        return p;
    }

    /**
     * Returns the largest canonical element of the connected component
     * containing {@code p}.
     * @param  p an element
     * @return the largest canonical element of the connected component
     * containing {@code p}
     */
    /* *****************************************************************************
     * If one of the connected components is {1, 2, 6, 9}, then the find() method
     * should return 9 for each of the four element in the connected components
     **************************************************************************** */
    public int find(int p) {
        int rootP = root(p);
        return max[rootP];
    }

    /**
     * Merges the set containing element {@code p} with the set
     * containing element {@code q}.
     *
     * @param  p one element
     * @param  q the other element
     * @throws IllegalArgumentException unless
     *         both {@code 0 <= p < n} and {@code 0 <= q < n}
     */
    /* *****************************************************************************
     * Update the largest number in connected components whenever union happened
     * For example, in Union.pdf, weighted quick union demo page 32
     *      parent[] = 0 1 2 3 4 5 6 7 8 9
     * -------------------------------------------------------
     * union(parent[4], parent[3]):
     * max(root(4), root(3)) => max(4, 3) => root(4) = 4
     *      parent[] = 0 1 2 4 4 5 6 7 8 9
     *      max[]    = 0 1 2 3 4 5 6 7 8 9
     * -------------------------------------------------------
     * union(parent[3], parent[8]):
     * max(root(3), root(8)) => max(4, 8) => root(4) = 8
     *      parent[] = 0 1 2 4 4 5 6 7 4 9
     *      max[]    = 0 1 2 3 8 5 6 7 8 9
     * -------------------------------------------------------
     * union(parent[6], parent[5]):
     * max(root(6), root(5)) => max(6, 5) => root(6) = 6
     *      parent[] = 0 1 2 4 4 6 6 7 4 9
     *      max[]    = 0 1 2 3 8 5 6 7 8 9
     * -------------------------------------------------------
     * union(parent[9], parent[4]):
     * max(root(9), root(4)) => max(8, 9) => root(4) = 9
     *      parent[] = 0 1 2 4 4 6 6 7 4 4
     *      max[]    = 0 1 2 3 9 5 6 7 8 9
     * -------------------------------------------------------
     * union(parent[2], parent[1]):
     * max(root(2), root(1)) => max(2, 1) => root(2) = 2
     *      parent[] = 0 2 2 4 4 6 6 7 4 4
     *      max[]    = 0 1 2 3 9 5 6 7 8 9
     * -------------------------------------------------------
     * union(parent[5], parent[0]):
     * max(root(5), root(0)) => max(6, 0) => root(6) = 6
     *      parent[] = 6 2 2 4 4 6 6 7 4 4
     *      max[]    = 0 1 2 3 9 5 6 7 8 9
     * -------------------------------------------------------
     * union(parent[7], parent[2]):
     * max(root(7), root(2)) => max(7, 2) => root(2) = 7
     *      parent[] = 6 2 2 4 4 6 6 2 4 4
     *      max[]    = 0 1 7 3 9 5 6 7 8 9
     * -------------------------------------------------------
     * union(parent[6], parent[1]):
     * max(root(6), root(1)) => max(6, 7) => root(6) = 7
     *      parent[] = 6 2 6 4 4 6 6 2 4 4
     *      max[]    = 0 1 7 3 9 5 7 7 8 9
     * -------------------------------------------------------
     * union(parent[7], parent[3]):
     * max(root(7), root(3)) => max(7, 9) => root(6) = 9
     *      parent[] = 6 2 6 4 6 6 6 2 4 4
     *      max[]    = 0 1 7 3 9 5 9 7 8 9
     **************************************************************************** */
    public void union(int p, int q) {
        int rootP = root(p);
        int rootQ = root(q);

        // If the roots are the same, the nodes already in the same components
        if (rootP == rootQ) return;

        // make smaller root point to larger one
        if (size[rootP] < size[rootQ]) {
            parent[rootP] = rootQ;
            size[rootQ] += size[rootP];

            // Pointed to root Q, therefore update of connected components of
            // root Q with largest element
            max[rootQ] = Math.max(max[rootP], max[rootQ]);
        }
        else {
            parent[rootQ] = rootP;
            size[rootP] += size[rootQ];

            // Pointed to root P, therefore update of connected components of
            // root P with largest element
            max[rootP] = Math.max(max[rootP], max[rootQ]);
        }
        count--;
    }

    // validate that p is a valid index
    protected void validate(int p) {
        int n = parent.length;
        if (p < 0 || p >= n) {
            throw new IllegalArgumentException("index " + p + " is not between 0 and " + (n-1));
        }
    }

    // Count the number of connected components
    public int getComp() {
        return count;
    }

    // test client (optional)
    public static void main(String[] args) {
        UFMaxCanonical uf = new UFMaxCanonical(10);
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
