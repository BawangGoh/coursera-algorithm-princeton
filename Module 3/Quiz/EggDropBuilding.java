/* *****************************************************************************
 *  Name:              Dennis Goh Jia Wang
 *  Last modified:     8/10/2025
 *  Interview Question: Analysis of Algorithms (Quiz)
 **************************************************************************** */

/* *****************************************************************************
 * Egg drop:
 * This module separate the data model for egg drop building and strategy into a
 * clean object-oriented design. Because parameters for building and strategy are
 * independent such that e.g.
 * 1) Building parameters
 *      a) number of floors, n
 *      b) threshold floor, k (1 <= k <= n)
 * 2) Strategy parameters
 *      a) number of eggs, N
 *      b) minimum number of tosses/attempts, T
 **************************************************************************** */
import edu.princeton.cs.algs4.StdOut;

public class EggDropBuilding {
    // Define attribute for egg drop building
    private final int n;      // Number of total floors
    private final int k;      // threshold floor

    // Constructor
    public EggDropBuilding(int n, int k) {
        // threshold floor, k is always bounded (1 <= k < = n)
        if (k < 1 || k > n) {
            throw new IllegalArgumentException("Threshold floor should not exceed building");
        }
        this.n = n;
        this.k = k;
    }

    /* *************************************************************************
     * Constructor for Dynamic Programming:
     * It does not depend on threshold floor and return the minimum # of tosses
     * needed in worst case to determine the highest floor from which an egg can
     * be dropped without breaking. Therefore, we can simple equate # of floor
     * to threshold floor (n = k)
     ************************************************************************ */
    public EggDropBuilding(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be positive integer");
        }
        this.n = this.k = n;
    }

    // Getter method
    public int getTotalFloor() {
        return n;
    }

    public int getThreshold() {
        return k;
    }

    // Egg breaks if it is drop floor == k or higher, otherwise does not break
    public boolean eggBreaks(int floor) {
        return floor >= k;
    }
}
