/* *****************************************************************************
 *  Name:              Dennis Goh Jia Wang
 *  Last modified:     20/10/2025
 *  Interview Question: Analysis of Algorithms (Quiz)
 **************************************************************************** */
/* *****************************************************************************
 * Egg drop DP:
 * From previous egg drop dynamic programming approach. The problem are resolved
 * around the minimax of Bellman Equation on minimizing number of tosses:
 *      Tosses(n, h) = 1 (current floor toss) + min(max(Tosses(n - 1, i - 1),
 *                      Tosses(n, h - i)))
 *      where i = 1, 2, 3, ..., h
 * Tosses(n - 1, i - 1) => egg breaks (check below ith floor)
 * Tosses(n, h - i) => egg doesn't break (check above ith floor till h floor)
 * max(...) => worst-case scenario of tosses
 * min(...) => best among all worst cases
 **************************************************************************** */

/* *****************************************************************************
 * Egg drop recurrence:
 * Now we reframe the problem instead of minimizing the number of trials for a
 * given number of floors, we flip the problem such that:
 * Given n eggs and x trials, what is the maximum number of floors we can test
 * in worst case ?
 * This is a dual formulation of the problem. Let define the maximum number of
 * floors that can be covered with x trials and n eggs:
 *              f(x, n)
 * Let suppose we can cover y floors and we drop the first egg from floor z.
 * There are two outcome:
 * 1) Egg breaks => we need to cover floors below z (z - 1) floors {1, 2, ...,
 * z - 1}:
 *      a) lose an egg => n - 1
 *      b) lose a trial => x - 1
 *      c) cover up to f(x - 1, n - 1) floors below
 *              f(x - 1, n - 1) >= z - 1
 * 2) Egg survive => we need to cover floors above z (y - z) floors {z + 1,
 * z + 2, ..., y}.
 *      a) keep all eggs => n
 *      b) lose a trial => x - 1
 *      c) cover up to f(x - 1, n) floors above
 *              f(x - 1, n) >= y-z
 * NOTES: Covering {z + 1, z + 2, ..., y} floors is equivalent to {1, 2, ...,
 * y-z} floors by using change of refrence frame:
 * 1) Checking floors above: {z + 1, z + 2, ..., y - z} is continguous block of
 * floors of size (y - z)
 * 2) Renumbering this block from 1:
 *      a) (z + 1)th floor -> 1st floor
 *      b) (z + 2)th floor -> 2nd floor
 *          ...
 *      c) yth floor -> (y - z)th floor
 * 3) Instead of thinking in term of absolute floor numbers, think in terms of
 * relative positions above z.
 * 4) These renumbering is valid because:
 *      a) egg survive at z, so threshold must be above z
 *      b) the problem is structurally identical to solving egg drop on a
 *         building of (y - z) floors
 * 5) Maximizing y, we choose:
 *      a) f(x - 1, n - 1) = z - 1 => z = f(x - 1, n - 1) + 1
 *      b) f(x - 1, n) = y - z => y = f(x - 1, n) + z
 *      c) y = f(x - 1, n - 1) + f(x - 1, n) + 1
 * Total number of floor can be represented by the recurrence function:
 *      f(x, n) = f(x - 1, n - 1) + f(x - 1, n) + 1
 **************************************************************************** */
import edu.princeton.cs.algs4.StdOut;
import java.util.Arrays;

public class EggDropBinomial {
    // Implement interface-based design for different recurrence approach
    public interface StrategyRecurrence {
        int solveEggDrop(int eggs, EggDropBuilding building);
    }

    /* *********************************************************************
     * Recurrence approach:
     * Find minimum trials needed in order to cover up to threshold floor
     * -------------------------------------------------------------------------
     * Time complexity:
     * O(n * f) since it involve computation of floors * eggs 2D arrays
     * -------------------------------------------------------------------------
     * Space complexity:
     * O(n * f) since it involve creation of floors * eggs 2D arrays
     ******************************************************************** */
    public static class MaxFloorRecurrence implements StrategyRecurrence {
        @Override
        public int solveEggDrop(int eggs, EggDropBuilding building) {
            int floors = building.getTotalFloor();

            // Create floors * eggs table (as floors is the maximum possible
            // trials required)
            int[][] dp = new int[floors + 1][eggs + 1];

            // Initialize the number of trials to store trials
            int trials = 0;

            // Run a loop until if maximum floor coverage is less than total
            // number of floors of building (dp[trials][eggs] < floors)
            while (dp[trials][eggs] < floors) {
                // Increase the trials until it is possible to cover up the
                // total number of floor of the building
                trials++;

                // For each egg, how many floor can be covered
                for (int i = 1; i <= eggs; i++) {
                    dp[trials][i] = 1 + dp[trials - 1][i] + dp[trials - 1][i - 1];
                }
            }
            return trials;
        }
    }

    /* *********************************************************************
     * Space-optimzed approach:
     * In previous approach, it involve the creation and computation of 2D array.
     * However, calculating the current trials in dp[][] table only require
     * previous row so we can optimize space using 1D array
     * -------------------------------------------------------------------------
     * Time complexity:
     * O(n * f) since it involve computation for different # of eggs on each
     * floors k = 1, 2, 3, ..., h
     * -------------------------------------------------------------------------
     * Space complexity:
     * O(n) since it involve creation of 1D arrays
     ******************************************************************** */
    public static class MaxFloorOptimized implements StrategyRecurrence {
        @Override
        public int solveEggDrop(int eggs, EggDropBuilding building) {
            int floors = building.getTotalFloor();

            // Create 1D array to store the previous trials result
            int[] dp = new int[eggs + 1];

            // Initialize trial to store number of trials
            int trials = 0;

            // Run a loop until if maximum floor coverage is less than total
            // number of floors of building (dp[trials][eggs] < floors)
            while (dp[eggs] < floors) {
                trials++;

                /* *************************************************************
                 * Number of attempts are the same, therefore can reduce the
                 * equation to depend only on # of eggs
                 *      f(x, n) = f(x - 1, n - 1) + f(x - 1, n) + 1
                 *      f(n) = f(n - 1) + f(n) + 1
                 * e.g. f(5) = f(4) + f(5) + 1
                 ************************************************************ */
                for (int i = eggs; i > 0 ; i--) {
                    dp[i] += 1 + dp[i - 1];
                }
            }
            return trials;
        }
    }

    /* *********************************************************************
     * Binomial recurrence approach:
     * Links: https://brilliant.org/wiki/egg-dropping/#working-with-binomials
     * In mathematical relations to binomials, we know that binomials coefficient
     * can be expressed as follows:
     *      nCk = C(n, k) = coeff(n, k) = n!/[k! * (n - k)!]
     * In Pascal triangle,
     *                      1
     *                     1 1
     *                    1 2 1
     *                   1 3 3 1
     *                  1 4 6 4 1
     * Fro the mathematical proof, we can express the recurrence as:
     *      C(n, k) = C(n - 1, k) + C(n - 1, k - 1)
     * e.g  C(2, 1) = 2C1
     *              = C(1, 1) + C(1, 0)
     *              = 1 + 1 = 2
     * Base cases:
     * 1)     C(n, 0) = n!/[0! * (n - 0)!] = 1
     * 2)     C(n, n) = n!/[n! * (n - n)!] = 1
     * From previous recurrence equation, maximize floor function:
     *      f(x, n) = f(x - 1, n - 1) + f(x - 1, n) + 1
     * Let define an auxiliary function g(x, n) for incremental gain in testable
     * floor:
     *      g(x, n) = f(x, n + 1) - f(x, n)
     * Plug f(x, n) into auxiliary equation of g(x, n):
     *      g(x, n) = f(x, n + 1) - f(x, n)
     *              = f(x - 1, n) + f(x - 1, n + 1) + 1 - f(x - 1, n - 1) -
     *                f(x - 1, n) - 1
     *              = [f(x - 1, n + 1) - f(x - 1, n)] +
     *                [f(x - 1, n) - f(x - 1, n - 1)]
     *              = g(x - 1, n) + g(x - 1, n - 1)
     *              = coef(x, n)
     * Considering the base cases for trials (x = 0), eggs (n = 0):
     * 1)       f(0, n) = 0 => g(0, n) = 0 (for all n because no trials left to
     *                                      test the floors)
     * 2)       f(x, 0) = 0 => g(x, 0) = 0 (for all x because no eggs left to
     *                                      test the floors)
     * Contradiction when n = 0 such that:
     * g(0, 0) = coeff(0, 0) = 1 where previously state that g(0, n) = 0 for
     * every n!.
     * Therefore, we modified the definition of g(x, n)
     *      g(x, n) = coeff(x, n + 1)
     * Now, using telescopic sum for f(x, n):
     *      f(x, n) = [f(x, n) - f(x, n - 1)] + [f(x, n - 1) - f(x, n - 2)] +
     *                ... + [f(x, 1) - f(x, 0)] + f(x, 0)
     *              = g(x, n - 1) + g(x, n - 2) + ... + g(x,0)
     * Substituting g(x, n) = coeff(x, n + 1):
     *      g(x, n - 1) + g(x, n - 2) + ... + g(x,0) = coeff(x, n) +
     *                                                 coeff(x, n - 1) + ... +
     *                                                 coeff(x, 1)
     * Finally, we got the following f(x, n) in term of binomial coefficient:
     *      f(x, n) = Sum_{i = 1, ..., n} coeff(x, i)
     * Let suppose we cover the building for k floor using N eggs and no more
     * than x attempts in worst cases:
     *      f(x, N) >= y
     *      Sum_{i = 1, ..., N} coeff(x, i) >= f
     * Reduce further binomial coefficient to efficient computation without
     * computing the full factorials.
     * 1) Let suppose we want to compute coeff(x, i) iteratively from (x, i - 1):
     *      coeff(x, i) = x!/[i! * (x - i)!]
     *      coeff(x, i - 1) = x!/[(i - 1)! * (x - i + 1)!]
     * 2) Take the ratio:
     *      coeff(x, i)/coeff(x, i - 1) = {x!/[i! * (x - i)!]}/x!/[(i - 1)! * (x - i + 1)!]
     *                                  = [(i - 1)! * (x - i + 1)!]/[i! * (x - i)!]
     *                                  = ((i - 1)!/i!) * ((x - i + 1)!/(x - i)!)
     *                                  = (x - i + 1)/i
     * 3) Computing iteratively:
     *      coeff(x, i) = coeff(x, i - 1) * [(x - i + 1)/i]
     * 4) Summation from 1, ... N:
     *      Sum_{i = 1, ..., N} coeff(x, i) = Sum_{i = 1, ..., N} coeff(x, i - 1) * [(x - i + 1)/i]
     * -------------------------------------------------------------------------
     * Time complexity:
     * O(n * log(f)) if uses binary search to find value of trials x till f floors
     * O(n * f) if uses linear search to find value of trials x till f floors
     * -------------------------------------------------------------------------
     * Space complexity:
     * O(1) since we do not use any array but variable itself
     ******************************************************************** */
    public static class MaxFloorBinomial implements StrategyRecurrence {
        @Override
        public int solveEggDrop(int eggs, EggDropBuilding building) {
            int lo = 1;
            int hi = building.getTotalFloor();
            int trials = 0;

            // Binary search for every mid, find the sum of binomial
            // coefficients and check if the sum is greater than k floors or not
            while (lo <= hi) {
                int mid = lo + (hi - lo) / 2;
                if (binomialCoeff(mid, eggs) < building.getTotalFloor()) {
                    lo = mid + 1;   // need more trials
                } else {
                    trials = mid;   // Temporary store previous trials
                    hi = mid - 1;   // try fewer trials
                }
            }
            return trials;
        }

        // Find sum of binomial coefficients xCi (where i varies from 1 to N).
        // till sum <= k floors
        public int binomialCoeff(int trials, int eggs) {
            int sum = 0;    // summation of eggs (n = 1, ..., N)
            int term = 1;   // multiplier terms (x - i + 1)/i
            for (int i = 1; i <= eggs; i++) {
                term *= (trials - i + 1);
                term /= i;
                sum += term;
            }
            return sum;
        }
    }

    // Binomial Coefficient with linear approach
    public static class MaxFloorLinear implements StrategyRecurrence {
        @Override
        public int solveEggDrop(int eggs, EggDropBuilding building) {
            int floors = building.getTotalFloor();
            int trials = 0;
            EggDropBinomial.MaxFloorBinomial BinomialObj = new MaxFloorBinomial();

            // Linear search for every trials on each floors, find the sum of
            // binomial coefficients and check if the sum is greater than k
            // floors or not
            while (trials <= floors) {
                trials++;
                if (BinomialObj.binomialCoeff(trials, eggs) >= floors) {
                    break;
                }
            }
            return trials;
        }
    }

    public static void main(String[] args) {
        // Create an EggDropBuilding(total floors) object
        EggDropBuilding building = new EggDropBuilding(10);

        // Create list to store all algorithm objects
        EggDropBinomial.StrategyRecurrence[] strategies = {
                new MaxFloorRecurrence(),
                new MaxFloorOptimized(),
                new MaxFloorBinomial(),
                new MaxFloorLinear()
        };

        // Create list of string names for algorithms
        String[] names = {
                "Maximize Floor Recurrences",
                "Maximize Floor 1D Space Optimized",
                "Maximize Floor Binomial Binary",
                "Maximize Floor Binomial Linear",
        };

        // Enumerate egg dropping algorithms
        int eggs = 4;
        for (int i = 0; i < strategies.length; i++) {
            int result = strategies[i].solveEggDrop(eggs, building);
            StdOut.println(names[i] + ": " + result + " tosses");
        }
    }
}
