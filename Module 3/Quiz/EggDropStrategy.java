/* *****************************************************************************
 *  Name:              Dennis Goh Jia Wang
 *  Last modified:     8/10/2025
 *  Interview Question: Analysis of Algorithms (Quiz)
 **************************************************************************** */

/* *****************************************************************************
 * Egg drop:
 * Suppose that you have an n-story building (with floors 1 through n) and plenty
 * of eggs. An egg breaks if it is dropped from floor T or higher and does not
 * break otherwise. Your goal is to devise a strategy to determine the value of
 * T given the following limitations on the number of eggs and tosses.
 * 1) Version 0: 1 egg, <= T tosses.
 * 2) Version 1: ~1log(n) eggs and ~1log(n) tosses.
 * 3) Version 2: ~1log(T) eggs and ~2log(T) tosses.
 * 4) Version 3: 2 eggs and ~2sqrt(n) tosses.
 * 5) Version 4: 2 eggs and <= C*sqrt(T) tosses for some fixed constant C.
 **************************************************************************** */

/* *****************************************************************************
 * Use behavioral design pattern (Strategy Pattern)
 * Component                        |       Roles in Strategy Pattern
 * -----------------------------------------------------------------------------
 * EggDropStrategy                  |       Strategy context and interface
 * Enum constant (Version0,...)     |       Concrete strategy implementing the algorithm
 * findThreshold(int k)             |       Strategy abstract method
 * main()                           |       Simulate a client strategy method
 * -----------------------------------------------------------------------------
 * This design can easily add new strategies as new enum constants (compact and
 * centralized). Each strategy is self-contained and provide clear separation of
 * logic per version. If one need to test the strategy in isolated manner, then
 * use interface-based design instead.
 **************************************************************************** */
import edu.princeton.cs.algs4.StdOut;

public class EggDropStrategy {
    /* *************************************************************************
     * Define a static nested class for data container. It is a good coding
     * practice for data holder classes (like DTO - data transfer object).
     ************************************************************************ */
    public static class Result {
        private int eggCost;
        private int tossCost;

        // Default constructor for computation in each constraint versions
        public Result() {
            this.eggCost = 0;
            this.tossCost = 0;
        }

        // Parameterized constructor for expected value of constraint versions
        public Result(int eggs, int toss) {
            this.eggCost = eggs;
            this.tossCost = toss;
        }
    }

    /* *************************************************************************
     * This egg drop problem can be solved in these 5 constrained version or
     * using generalized approach (dynamic programming). The core differences
     * are as follow:
     * 1) Constraint version (V0-V4)
     *      a) Goal: Find threshold T using minimal tosses under specific
     *      constraints.
     *      b) Constraints: Fixed eggs & tosses per version (# of eggs = 1,
     *      log(n), 2, etc. & # of tosses ~ T, log(n), log(T), sqrt(n), etc
     *      c) Optimization: Strategy tailored to constraints
     *      d) Flexibility: Limited to specific cases
     *      e) Complexity: Varies by version (O(n), O(log(n)), O(sqrt(n)), etc)
     *      f) Use cases: Constraint systems
     * 2) Dynamic Programming (Generalized)
     *      a) Goal: Minimize worst-cases tosses for any N eggs & K floors
     *      b) Constraints: Arbitrary # of eggs & floors.
     *      c) Optimization: Optimal solution for any inputs
     *      d) Flexibility: Fully general
     *      e) Complexity: Linear O(N x K^2) or Binary search O(N x K x log(K))
     *      f) Use cases: Algorithmic optimization, scalable systems
     * In this problem, given constraint on # of eggs & tosses, we can do a
     * reverse-engineering by setting the threshold floor and calculate whether
     * the # of eggs & tosses is lower bound:
     * n_* <= n (# of eggs)
     * t_* <= t (# of tosses)
     ************************************************************************ */
    public enum StrategyVersion {
        /* *********************************************************************
         * Version 0: 1 egg, <= T tosses
         * Constraint: Only an egg, so we must avoid breaking it until it reach
         * the threshold floor
         * Strategy: Brute-force linear search from floor 1 till n
         * Worst case: T tosses (equal to the # of total floor)
         ******************************************************************** */
        VERSION_0 {
            @Override
            public Result findThreshold(EggDropBuilding building) {
                int n = building.getTotalFloor();

                // Directly instantiated result object since it is inside the scope
                // Alternative: EggDropStrategy.Result result = new EggDropStrategy.Result();
                Result result = new Result();
                for (int i = 1; i <= n; i++) {
                    if(toss(i, result, building))
                        break;
                }
                return result;
            }

            @Override
            public Result getWorstCase(EggDropBuilding building) {
                return new Result(1, building.getThreshold());
            }
        },

        /* *********************************************************************
         * Version 1: ~log2(n) egg, ~log2(n) tosses
         * Constraint: Must have enough eggs to allow binary search.
         * Strategy: Binary search by breaking subsequence egg to reduce each
         * search space into halves. (n/2, if break -> n/4, else 3n/4, ...)
         * Worst case: log2(n) which search till the last-th search space
         ******************************************************************** */
        VERSION_1 {
            @Override
            public Result findThreshold(EggDropBuilding building) {
                Result result = new Result();
                int lo = 1;
                int hi = building.getTotalFloor();
                while(lo <= hi) {
                    int mid = lo + (hi - lo) / 2;
                    if (toss(mid, result, building)) {
                        hi = mid - 1;
                    } else {
                        lo = mid + 1;
                    }
                }
                return result;
            }

            @Override
            public Result getWorstCase(EggDropBuilding building) {
                return new Result(log2(building.getTotalFloor()), log2(building.getTotalFloor()));
            }
        },

        /* *********************************************************************
         * Version 2: ~log2(T) eggs and ~2log2(T) tosses.
         * Constraint: T is unknown but small
         * Strategy: Exponential search to find range nearest to T such that
         * T is within [L, R] where R - L < 2T. Drop the egg at k = log2(T) ->
         * T = 2^k from 1, 2, 4, 8, ...
         * If the egg breaks, then binary search within [2^(k - 1) + 1, 2^k]
         * Worst case: ~2log2(T)
         ******************************************************************** */
        VERSION_2 {
            @Override
            public Result findThreshold(EggDropBuilding building) {
                Result result = new Result();
                int n = building.getTotalFloor();
                int pow = 1;    // 2^n floor (1, 2, 4, 8, ...)

                // Exponential search to nearest threshold floor
                while (pow < n) {
                    if (toss(pow, result, building))
                        break;
                    pow <<= 1;  // bitwise left shift 1 bit is equivalent to i *= 2
                }

                // Binary search fallback (When egg break at k, then fallback to k/2 to k)
                int lo = pow / 2;
                int hi = Math.min(pow, n);
                while(lo <= hi) {
                    int mid = lo + (hi - lo) / 2;
                    if (toss(mid, result, building)) {
                        hi = mid - 1;
                    } else {
                        lo = mid + 1;
                    }
                }
                return result;
            }

            @Override
            public Result getWorstCase(EggDropBuilding building) {
                return new Result(log2(building.getThreshold()), 2 * log2(building.getThreshold()));
            }
        },

        /* *********************************************************************
         * Version 3: 2 eggs and ~2sqrt(n) tosses.
         * Constraint: Only 2 eggs, we must minimize the risk
         * Strategy: Drop the first egg in the interval of sqrt(n) e.g. n = 100,
         * -> sqrt(n) = 10, 2 * sqrt(n) = 20, ..., then linear search within
         * [(k - 1) * sqrt(n) + 1, k * sqrt(n) - 1] if egg breaks
         * Worst case: ~2sqrt(n)
         ******************************************************************** */
        VERSION_3 {
            @Override
            public Result findThreshold(EggDropBuilding building) {
                Result result = new Result();
                int n = building.getTotalFloor();
                int sqrtN = sqrt(n);
                int k = 1;

                // Interval search to nearest threshold floor
                while (k * sqrtN < n) {
                    if (toss(k * sqrtN, result, building))
                        break;
                    k++;
                }

                // Brute-force linear search (When egg break, search (k-1) * interval to k * interval)
                for (int i = ((k - 1) * sqrtN) + 1; i < k * sqrtN; i++) {
                    if (toss(i, result, building)) {
                        break;
                    }
                }
                return result;
            }

            @Override
            public Result getWorstCase(EggDropBuilding building) {
                return new Result(2, 2 * sqrt(building.getTotalFloor()));
            }
        },

        /* *********************************************************************
         * Version 4: 2 eggs and <= C * sqrt(T) tosses.
         * Constraint: T is unknown and tosses is bound by C * sqrt(T)
         * Strategy: We need to minimize the worst-case # of tosses. Using
         * triangular number strategy to ensure complete coverage of all floors.
         * Let suppose first drop an egg at t floor
         * 1) If egg breaks, we have to checks 1, 2, ..., (t - 2), (t - 1)
         * floors, so total # of drops is t
         * 2) If it doesn't break, then we jump the interval to [t + (t - 1)]
         * floors
         * 3) If it break on [t + (t - 1)] floor, then we have to checks the
         * floors on (t + 1), (t + 2), ... [t + (t - 1) -1].
         * 4) In 2nd drop break case, the total # of tosses will be
         * [t + (t - 1) - 1] - (t + 1) + 1 + 2 (1st & 2nd drop eggs) = t
         * ---------------------------------------------------------------------
         * Suppose the minimum # of tosses in worst case, then the egg drop t
         * floor covering t floors, then drop at [t + (t - 1)] floor covering
         * (t - 1) floors, then drop at [t + (t - 1) + (t - 2)] floor covering
         * (t - 2) floors, ...
         * This give us the triangular sum:
         * t + (t - 1) + (t - 2) + ... + 2 + 1 = t(t + 1)/2
         * triangular number:
         * t_k = t(t + 1)/2
         * ---------------------------------------------------------------------
         * Since T is an unknown, we can start tossing the eggs on triangular
         * numbers such that 1, 3, 6, 10, ... to get the number nearest to T,
         * then linear search from t_{k-1} + 1 to t_{k} - 1
         * Worst case: <= sqrt(2) * sqrt(T)
         ******************************************************************** */
        VERSION_4 {
            @Override
            public Result findThreshold(EggDropBuilding building) {
                Result result = new Result();
                int curr = 0;
                int interval = 1;
                int n = building.getTotalFloor();

                // Drop first egg on triangular numbers of floor
                while (curr < n) {
                    curr += interval;   // 1, 3, 6, 10,...
                    if (toss(curr, result, building))
                        break;
                    interval++;     // 1, 2, 3, 4, ...
                }

                // Brute-force linear search if egg break
                for (int i = (curr - interval) + 1; i < curr; i++) {
                    if (toss(i, result, building)) {
                        break;
                    }
                }
                return result;
            }

            /* *****************************************************************
             * Let suppose the # of tosses = t(t - 1)/2 >= n in order to cover
             * every floors.
             *      t(t - 1)/2 ~ t^2/2 >= n => t >= sqrt(2n)
             * Let consider the worst case that threshold floor is at the last
             * floor T = n
             *      # of tosses ~ sqrt(2T) ~ sqrt(2) * sqrt(T)
             * Connecting to the constraint,
             *      # of tosses <= C * sqrt(T)
             *      sqrt(2) * sqrt(T) <= C * sqrt(T)
             *      C >= sqrt(2)
             * Including linear search, the worst-case total tosses:
             *      # of tosses <= sqrt(2T) + sqrt(2T) = 2 * sqrt(2T)
             * Connecting to the constraint,
             *      # of tosses <= C * sqrt(T)
             *      2 * sqrt(2) * sqrt(T) <= C * sqrt(T)
             *      C >= 2 * sqrt(2)
             * In conclusion:
             * Triangular sum strategy give worst-case tosses = 2sqrt(2) * sqrt(T)
             * It achieve a tighter bound and more efficient
            ***************************************************************** */
            @Override
            public Result getWorstCase(EggDropBuilding building) {
                return new Result(2, 2 * sqrt(2 * building.getThreshold()));
            }
        };

        /* *********************************************************************
         * Abstract method are defined at enum level because it is part of
         * strategic logic but not Result data model. This method implement its
         * own logic for different version and return the Result object
         ******************************************************************** */
        public abstract Result findThreshold(EggDropBuilding building);
        public abstract Result getWorstCase(EggDropBuilding building);
    }

    // Static method for log2 operation (log2(N) = log(N)/ log(2))
    public static int log2(int N) {
        if (N <= 0)
            throw new IllegalArgumentException("Logarithmic input must be positive values");
        return (int) (Math.log(N) / Math.log(2));
    }

    // Static method for square root operation
    public static int sqrt(int N) {
        if (N <= 0)
            throw new IllegalArgumentException("Square root input must be positive values");
        return (int) Math.ceil(Math.sqrt(N));
    }

    private static boolean toss(int h, Result r, EggDropBuilding building) {
        r.tossCost++;
        boolean res = building.eggBreaks(h);
        if (res) r.eggCost++;
        return res;
    }

    // Test client
    public static void main(String[] args) {
        // Create an EggDropBuilding(total floors, threshold) object
        EggDropBuilding building = new EggDropBuilding(100, 73);

        // Enumerate the EggDropStrategy version
        for (StrategyVersion strategy : StrategyVersion.values()) {
            StdOut.println("Testing strategy: " + strategy.name());

            EggDropStrategy.Result sample_res = strategy.findThreshold(building);
            StdOut.println("Sample computation -> Eggs used: " + sample_res.eggCost + ", Tosses made: " + sample_res.tossCost);

            EggDropStrategy.Result worst_res = strategy.getWorstCase(building);
            StdOut.println("Worst case -> Eggs used: " + worst_res.eggCost + ", Tosses made: " + worst_res.tossCost);
        }
    }
}
