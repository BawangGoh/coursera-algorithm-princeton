/* *****************************************************************************
 *  Name:              Dennis Goh Jia Wang
 *  Last modified:     20/10/2025
 *  Interview Question: Analysis of Algorithms (Quiz)
 **************************************************************************** */

/* *****************************************************************************
 * Egg drop:
 * Suppose you have N eggs and you want to determine from which floors in a
 * h-floor building you can drop an egg such that it doesn't break. You are to
 * determine the minimum number of attempts you need in order to find the
 * critical floor in the worst-case while using the best strategy without any
 * constraints. This is more complicated because it is generalized for any
 * number of eggs and floors.
 **************************************************************************** */
import edu.princeton.cs.algs4.StdOut;
import java.util.Arrays;

/* *****************************************************************************
 * Dynamic Programming Approach:
 * Let suppose n eggs and h consecutive floors yet to be tested, drop an egg at
 * floor i in this sequence of h consecutive floors
 * 1) If egg breaks:
 *      Problem reduce to (n - 1) eggs and (i - 1) remaining floors.
 * 2) If egg doesn't break:
 *      Problem reduce to n eggs and (h - i) remaining floors
 * We define a Bellman function W(n, h) to compute the minimum number of tosses
 * required to find the critical floor in worst case scenario
 *      Tosses(n, h) = 1 (current floor toss) + min(max(Tosses(n - 1, i - 1),
 *                      Tosses(n, h - i)))
 *      where i = 1, 2, 3, ..., h
 * Base cases:
 * 1) When only 1 egg remain (brute-force linear search)
 *      Tosses(1, h) = h
 * 2) When only one floor, the egg always equal to 1 regardless
 *      Tosses(n, 1) = 1
 * 3) When 0 floors required no drops
 *      Tosses(n, 0) = 0
 * -----------------------------------------------------------------------------
 * Objective:
 * Compare the time complexity needed for different dynamic programming approach
 * that all obtain the same minimum number of drops
 * -----------------------------------------------------------------------------
 * Drawbacks:
 * This recurrence relation is a minimax problem and computationally expensive
 **************************************************************************** */
public class EggDropDP {
    // Implement interface-based design for different DP approach on egg drop
    public interface StrategyDP {
        int solveEggDrop(int eggs, EggDropBuilding building);
    }

    /* *************************************************************************
     * Naive Approach: Recursive (Depth First Search)
     * Simulating dropping an egg and recursively compute:
     * 1) If egg breaks, check below floor i
     * 2) Otherwise, check above floor i
     * 3) Initialize the maximum integer to always get the first minimum number
     * of tosses
     * 4) Maximize which egg drop scenario solveEggDrop(n - i, i - 1) or
     * solveEggDrop(n, h - i) give the worst case
     * 5) Minimize to find the best among all worst cases
     * -------------------------------------------------------------------------
     * Time complexity:
     * Exponential O(2^h), h = # of floors
     * -------------------------------------------------------------------------
     * Drawback:
     * Very slow for large inputs due to repeated subproblem evaluations
     ************************************************************************ */
    public static class RecursiveDFS implements StrategyDP {
        @Override
        public int solveEggDrop(int eggs, EggDropBuilding building) {
            int floors = building.getTotalFloor();

            // Base cases:
            if (eggs == 1) return floors;
            if (floors == 0 || floors == 1) return floors;

            // Initialize maximum integer 2^31 - 1
            int min = Integer.MAX_VALUE;
            for (int i = 1; i <= floors; i++) {
                int eggBreaks = solveEggDrop(eggs - 1, new EggDropBuilding(i - 1));
                int eggRemains = solveEggDrop(eggs, new EggDropBuilding(floors - i));

                // Getting the worst case for # of tosses
                int worst = 1 + Math.max(eggBreaks, eggRemains);

                // Minimize for worst case # of tosses for subsequnce floors
                min = Math.min(min, worst);
            }
            return min;
        }
    }

    /* *************************************************************************
     * Optimize Approach: Recursive Memoization
     * Memoization (top-down approach) avoid repeated computation of overlapping
     * subproblems that faster than naive recursive approach
     * -------------------------------------------------------------------------
     * Time complexity:
     * O(n * h^2) due to nested loops
     *      n = # of eggs, h = # of floors
     * -------------------------------------------------------------------------
     * Drawback:
     * Polynomial time complexity not good for large inputs
     ************************************************************************ */
    public static class RecursiveMemo implements StrategyDP {
        @Override
        public int solveEggDrop(int eggs, EggDropBuilding building) {
            int floors = building.getTotalFloor();

            /* *****************************************************************
             * Initialize 2D memo at class-level or top of function to avoid
             * incorrect memoization (Reinitialize memo in recursive call) will
             * start with a fresh new memo table and recompute the subproblems
             * multiple times which does exactly like plain recursive calls.
             **************************************************************** */
            int[][] memo = new int[eggs + 1][floors + 1];
            for (int i = 0; i <= eggs; i++) {
                Arrays.fill(memo[i], -1);
            }
            return dfs(eggs, floors, memo);
        }

        public int dfs(int eggs, int floors, int[][] memo) {
            // Base cases:
            if (eggs == 1) return floors;
            if (floors == 0 || floors == 1) return floors;

            // If subproblem found in memo table
            if (memo[eggs][floors] != -1) return memo[eggs][floors];

            // Dynamic programming (top-down approach)
            int min = Integer.MAX_VALUE;
            for (int i = 1; i <= floors; i++) {
                int worst = 1 + Math.max(dfs(eggs - 1, i - 1, memo), dfs(eggs, floors - i, memo));
                min = Math.min(min, worst);
            }
            memo[eggs][floors] = min;
            return min;
        }
    }

    /* *************************************************************************
     * Optimize Approach: Iterative Tabulation (Breadth First Search)
     * Simulating recursive logic using loop:
     * 1) Using 2D array dp[eggs][floors]
     * 2) Tabulation (bottom-up approach) that solve smallest subproblems in 2D
     * array dp[eggs][floors] start from [0][0] to build up to the larger
     * subproblems.
     * 3) Store the worst case of minimum # of tosses for every possible drops
     * -------------------------------------------------------------------------
     * Time complexity:
     * Same as recursive O(n * h^2) due to nested loops
     *      n = # of eggs, h = # of floors
     * -------------------------------------------------------------------------
     * Drawback:
     * Polynomial time complexity not good for large inputs
     ************************************************************************ */
    public static class IterativeTabu implements StrategyDP {
        @Override
        public int solveEggDrop(int eggs, EggDropBuilding building) {
            int floors = building.getTotalFloor();

            // Create a 2D array
            int[][] dp = new int[eggs + 1][floors + 1];

            // Base cases for floors = 0 or 1
            for (int i = 1; i <= eggs; i++) {
                dp[i][0] = 0;
                dp[i][1] = 1;
            }
            for (int j = 1; j <= floors; j++) {
                dp[1][j] = j;
            }

            // Dynamic programming (bottom-up approach)
            for (int i = 2; i <= eggs; i++) {
                for (int j = 2; j <= floors; j++) {
                    dp[i][j] = Integer.MAX_VALUE;

                    // Minimize for k = 1, 2, ..., j
                    for (int k = 1; k <= j; k++) {
                        int res = 1 + Math.max(dp[i - 1][k - 1], dp[i][j - k]);
                        dp[i][j] = Math.min(dp[i][j], res);
                    }
                }
            }
            return dp[eggs][floors];
        }
    }

    /* *************************************************************************
     * Optimize Approach: Space-Optimized Table (1D Dynamic Programming)
     * Instead of using a full 2D table, we use a rolling 1D array to save
     * space. Each iteration updates the current state based on previous. It is
     * a similar bottom-up approach by building layer by layer for each egg.
     * Let suppose:
     * 1) dp[i] = minimum tosses needed for h floors with current egg count
     * 2) Update dp using temporary array temp[j] for next egg layer
     * 3) temp[k] = 1 + min(max(dp[j - 1], temp[k - j])) where j = 1, 2, ..., h
     * -------------------------------------------------------------------------
     * Time complexity:
     * Same as tabulation O(n * h^2) due to nested loops
     *      n = # of eggs, h = # of floors
     * -------------------------------------------------------------------------
     * Space complexity:
     * Reduces space from O(n * h) to O(h)
     * -------------------------------------------------------------------------
     * Drawback:
     * Polynomial time complexity not good for large inputs
     ************************************************************************ */
    public static class SpaceOptimized implements StrategyDP {
        @Override
        public int solveEggDrop(int eggs, EggDropBuilding building) {
            int floors = building.getTotalFloor();
            int[] dp = new int[floors + 1];

            // Base case: 1 egg -> brute-force linear search
            for (int i = 1; i <= floors; i++) {
                dp[i] = i;
            }

            // Dynamic programming for eggs > 1
            for (int i = 2; i <= eggs; i++) {
                // Create 1D rolling temp array
                int[] temp = new int[floors + 1];
                for (int j = 1; j <= floors; j++) {
                    temp[j] = Integer.MAX_VALUE;

                    // Minimize for k = 1, 2, ..., j
                    for (int k = 1; k <= j; k++) {
                        int res = 1 + Math.max(dp[k - 1], temp[j - k]);
                        temp[j] = Math.min(temp[j], res);
                    }
                }
                dp = temp; // roll forward
            }
            return dp[floors];
        }
    }

    public static void main(String[] args) {
        // Create an EggDropBuilding(total floors) object
        EggDropBuilding building = new EggDropBuilding(10);

        // Create list to store all algorithm objects
        StrategyDP[] strategies = {
                new RecursiveDFS(),
                new RecursiveMemo(),
                new IterativeTabu(),
                new SpaceOptimized(),
        };

        // Create list of string names for algorithms
        String[] names = {
            "Recursive (DFS)",
            "Recursive (DFS) Memoization",
            "Iterative (BFS) Tabulation",
            "Space-optimized Table",
        };

        // Enumerate egg dropping algorithms
        int eggs = 4;
        for (int i = 0; i < strategies.length; i++) {
            int result = strategies[i].solveEggDrop(eggs, building);
            StdOut.println(names[i] + ": " + result + " tosses");
        }
    }
}
