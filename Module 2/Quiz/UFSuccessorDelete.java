/* *****************************************************************************
 *  Name:              Dennis Goh Jia Wang
 *  Last modified:     1/10/2025
 *  Interview Question: Union-Find (Quiz)
 **************************************************************************** */

/* *****************************************************************************
 * Successor with delete:
 * Given a set of n integers S = {0, 1, ..., n-1} and a sequence of requests of
 * the following form:
 * 1) Remove x from S
 * 2) Find the successor of x: the smallest y in S such that y >= x
 * Design a data type so that all operations (except construction) take logarithmic
 * time or better in the worst case.
 **************************************************************************** */

import java.util.Arrays;
import edu.princeton.cs.algs4.StdOut;

/* *****************************************************************************
 * Solution
 * There are 3 methods to solve the successor with delete:
 * 1. Union-Find with Path Compression
 * 2. Binary search trees (BST)
 * 3. Simple Array-based Data Structure
 **************************************************************************** */

// Solving the successor with delete using Union-Find data structure
public class UFSuccessorDelete extends UFPathCompression {
    // Private attribute for union-find deletion
    private int n;
    private boolean[] removed;

    // Inherit the superclass constructor
    public UFSuccessorDelete(int n) {
        super(n);

        // Intialized n and removed array to track the removed node
        this.n = n;
        this.removed = new boolean[n];
    }

    /* *****************************************************************************
     * Delete p and union with neighbors if they are removed. For example,
     *      parent[] = 0 1 2 3 4 5 6 7 8 9
     * -------------------------------------------------------
     * 1) First deletion of element:
     * delete(6) => no union:
     *      parent[]    = 0 1 2 3 4 5 6 7 8 9
     *      max[]       = 0 1 2 3 4 5 6 7 8 9
     *      removed[]   = F F F F F F T F F F
     * find(6) => removed[6] == true => succ(6) => 6 + 1 => 7
     * -------------------------------------------------------
     * 2) Delete the left node:
     * delete(5)
     * => check left element 4 is removed ? => no union(5, 4)
     * => check right element 6 is removed ? => union(5, 6)
     * union(5, 6):
     * max(root(5), root(6)) => max(5, 6) => root(5) = 6
     *      parent[]    = 0 1 2 3 4 5 5 7 8 9
     *      max[]       = 0 1 2 3 4 6 6 7 8 9
     *      removed[]   = F F F F F T T F F F
     * find(5) => removed[5] == true => succ(5) => 6 + 1 => 7
     * -------------------------------------------------------
     * 3) Delete the right node:
     * delete(7)
     * => check left element 6 is removed ? => union(7, 6)
     * => check right element 6 is removed ? => no union(7, 8)
     * union(7, 6):
     * => size(6) > size(7) => union(6, 7)
     * max(root(7), root(6)) => max(7, 6) => root(5) = 7
     *      parent[]    = 0 1 2 3 4 5 5 5 8 9
     *      max[]       = 0 1 2 3 4 7 6 7 8 9
     *      removed[]   = F F F F F T T T F F
     * find(7) => removed[7] == true => succ(7) => 7 + 1 => 8
     **************************************************************************** */
    public void delete(int p) {
        // Return if node value out of bound or already removed
        super.validate(p);
        if (removed[p]) return;

        // Set node to be removed
        removed[p] = true;

        // Check if the left neighbor (x - 1) is removed, union it with x
        if (p > 0 && removed[p - 1]){
            super.union(p, p - 1);
            StdOut.println("After left union: " + Arrays.toString(parent));
        }

        // Check if the right neighbor (x + 1) is removed, union it with x
        if (p < n - 1 && removed[p + 1]) {
            super.union(p, p + 1);
            StdOut.println("After right union: " + Arrays.toString(parent));
        }
    }

    // Override the find method to return the successor y >= x
    @Override
    public int find(int p) {
        super.validate(p);
        if (!removed[p]) return p;

        // Get the successor element which is the element next to the maximum
        // element of the connected component
        int succ = super.find(p) + 1;

        return (succ < n) ? succ : -1;
    }

    public static void main(String[] args) {
        UFSuccessorDelete uf = new UFSuccessorDelete(10);
        StdOut.println("Union array: " + Arrays.toString(uf.parent));
        StdOut.println("Maximum array: " + Arrays.toString(uf.max));
        StdOut.println("Removed array: " + Arrays.toString(uf.removed));
        StdOut.println("Number of connected components: " + uf.getComp());

        // Delete operations
        uf.delete(6);
        uf.delete(5);   // Delete left node of 6
        uf.delete(7);   // Delete right node of 6

        // System standard output union and max array
        StdOut.println("After element deletion");
        StdOut.println("Union array: " + Arrays.toString(uf.parent));
        StdOut.println("Maximum array: " + Arrays.toString(uf.max));
        StdOut.println("Removed array: " + Arrays.toString(uf.removed));
        StdOut.println("Successor of element 6: " + uf.find(6));
        StdOut.println("Successor of element 4: " + uf.find(4));
        StdOut.println("Successor of element 7: " + uf.find(7));
        StdOut.println("Number of connected components: " + uf.getComp());
    }
}
