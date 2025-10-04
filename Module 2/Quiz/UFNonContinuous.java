/* *****************************************************************************
 *  Name:              Dennis Goh Jia Wang
 *  Last modified:     1/10/2025
 *  Interview Question: Union-Find (Quiz)
 **************************************************************************** */

/* *****************************************************************************
 * This is the general case for successor with delete data structure:
 * Given a non-continuous set of n integers S = {0, 2, 4, 6, 9, 11} and a
 * sequence of requests of the following form:
 * 1) Remove x from S
 * 2) Find the successor of x: the smallest y in S such that y >= x
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;

/* *****************************************************************************
 * Solution
 * Use the Union-Find data structure with HashMap and HashSet to manage the
 * delete and successor tracking operations
 **************************************************************************** */

public class UFNonContinuous {
    // Use HashMap to track disjoint sets
    private Map<Integer, Integer> parent = new HashMap<>();
    private Map<Integer, Integer> max = new HashMap<>();
    private Map<Integer, Integer> size = new HashMap<>();

    // Use set to track removed elements
    private Set<Integer> removed = new HashSet<>();

    // Intialized TreeSet that always sorted the element in ascending order for
    // successor lookup
    private TreeSet<Integer> originalSet;

    // Constructor for non-continuous set
    public UFNonContinuous(Set<Integer> elements) {
        originalSet = new TreeSet<>(elements);

        // Initialized parent and max HashMap for union operation
        for (int x : elements) {
            parent.put(x, x);
            max.put(x, x);
            size.put(x, 1); // Each element starts as its own tree of size 1
        }
    }

    // Does not validate if x is 0 < x < n since it is non-continuous, need to
    // validate if x exists in the originalSet instead
    public void validate(int x) {
        if (!originalSet.contains(x)) {
            throw new IllegalArgumentException("Integer " + x + " does not exist in the set");
        }
    }

    // Find root operation for object HashMap
    public int root(int x) {
        validate(x); // Optional: ensure x is in the original set

        // Single one-pass variant
        while (!parent.get(x).equals(x)) {
            parent.put(x, parent.get(parent.get(x))); // Path compression
            x = parent.get(x);
        }
        return x;
    }

    // Union operation for object HashMap
    public void union(int x, int y) {
        if (!parent.containsKey(x) || !parent.containsKey(y)) return;

        // Get the root for x and y
        int rootX = root(x);
        int rootY = root(y);

        // If the roots are the same, the nodes already in the same components
        if (rootX == rootY) return;

        // Weighted union: attach smaller tree to larger
        if (size.get(rootX) < size.get(rootY)) {
            parent.put(rootX, rootY);
            size.put(rootY, size.get(rootY) + size.get(rootX));
            max.put(rootY, Math.max(max.get(rootX), max.get(rootY)));
        } else {
            parent.put(rootY, rootX);
            size.put(rootX, size.get(rootX) + size.get(rootY));
            max.put(rootX, Math.max(max.get(rootX), max.get(rootY)));
        }
    }

    // Delete operation for object HashMap
    public void delete(int x) {
        // Return if node value does not exist or already removed
        validate(x);
        if (removed.contains(x)) return;

        // Add the deleted node into the set
        removed.add(x);

        /* For non-continuous sets, logic for (x + 1) and (x - 1) may not exist
        in the set, therefore need TreeSet to get originalSet.lower(x) and
        originalSet.higher(x)
         */
        Integer lower = originalSet.lower(x);
        Integer higher = originalSet.higher(x);

        // Union only with valid neighbours
        if (lower != null && removed.contains(lower)) {
            union(x, lower);
        }
        if (higher != null && removed.contains(higher)) {
            union(x, higher);
        }
    }

    // Find operation for successor objects (need to specific as Integer Object)
    // Otherwise could not return NULL pointer
    public Integer find(int x) {
        validate(x);
        if (!removed.contains(x)) return x;

        // Get the maximum element of the connected components
        int rootX = root(x);
        int maxElem = max.get(rootX);

        // Find next available element in originalSet of max element
        Integer next = originalSet.higher(maxElem);

        // Continue to loop and get next available element if next element is
        // already removed
        while (next != null && removed.contains(next)) {
            next = originalSet.higher(next);
        }
        return next; // Maybe null if no successor exits
    }

    // Test client
    public static void main(String[] args) {
        Set<Integer> S = new HashSet<>(Arrays.asList(0, 2, 4, 6, 9, 11));
        UFNonContinuous uf = new UFNonContinuous(S);
        StdOut.println("Input set: " + S);
        StdOut.println("Union HashMap: " + uf.parent);
        StdOut.println("Maximum HashMap: " + uf.max);
        StdOut.println("Removed HashSet: " + uf.removed);

        // Delete operations
        uf.delete(6);
        uf.delete(4);   // Delete left node of 6
        uf.delete(9);   // Delete right node of 6

        // System standard output
        StdOut.println("After element deletion");
        StdOut.println("Union HashMap: " + uf.parent);
        StdOut.println("Maximum HashMap: " + uf.max);
        StdOut.println("Removed HashSet: " + uf.removed);
        StdOut.println("Successor of element 6: " + uf.find(6));
        StdOut.println("Successor of element 4: " + uf.find(4));
        StdOut.println("Successor of element 9: " + uf.find(2));
    }
}
