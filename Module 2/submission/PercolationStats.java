/* *****************************************************************************
 *  Name:              Dennis Goh Jia Wang
 *  Last modified:     18/9/2025
 **************************************************************************** */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.Stopwatch;

public class PercolationStats {
    // Private attributes
    private double[] estimates;

    /**
     * Constructor:
     * Perform independent trials on an n-by-n grid
     *
     * @param n      dimension of the grid
     * @param trials number of trials
     */
    public PercolationStats(int n, int trials) {
        // Throw an IllegalArgumentException in the constructor if either n ≤ 0 or trials ≤ 0.
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException("n or trials must be positive value");
        }

        // Initialized array of estimated fraction of open sites, x(1), x(2), ..., x(t)
        estimates = new double[trials];

        // Repeat experiment T trials and averaging the results
        for (int k = 0; k < trials; k++) {
            Percolation percolation = new Percolation(n);

            // Choose to open a site uniformly at random until the system percolates
            while (!percolation.percolates()) {
                int i = StdRandom.uniformInt(n) + 1;
                int j = StdRandom.uniformInt(n) + 1;

                // Check if the site already opened and belong to fully connected sites
                /* NOTES: Does not need to check full connected site isFull(row, col) since a site
                 * can be fully connected if it opened in the first place isOpen(row,col)
                 * */
                if (!percolation.isOpen(i, j)) {
                    percolation.open(i, j);
                }
            }

            // System percolates at this point and calculate the fraction of open sites
            /* NOTES: (1/2 in int is 0) result will be 0.0
             * Need to explicitly convert numerator to (double) data type
             *  */
            estimates[k] = (double) percolation.numberOfOpenSites() / (n * n);
        }
    }

    // // Sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(estimates);
    }

    // Sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(estimates);
    }

    // Low endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean() - (1.96 * stddev() / Math.sqrt(estimates.length));
    }

    // High endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean() + (1.96 * stddev() / Math.sqrt(estimates.length));
    }

    // test client (option)
    public static void main(String[] args) {
        int n = StdIn.readInt();
        int trials = StdIn.readInt();

        // Instantiated stopwatch object for calculating elapsed time for Weighted Quick Union
        Stopwatch stopwatch = new Stopwatch();
        PercolationStats obj = new PercolationStats(n, trials);
        double time = stopwatch.elapsedTime();
        StdOut.println("Weighted Quick Union elapsed time: " + time);
        StdOut.printf("%-23s = %f%n", "mean", obj.mean());
        StdOut.printf("%-23s = %f%n", "stddev", obj.stddev());
        StdOut.printf("%-23s = [%f, %f]%n", "95% confidence interval", obj.confidenceLo(),
                      obj.confidenceHi());
    }
}
