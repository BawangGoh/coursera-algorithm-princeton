/* *****************************************************************************
 *  Name:              Dennis Goh Jia Wang
 *  Last modified:     18/9/2025
 **************************************************************************** */

/* *****************************************************************************
 * Percolation. Given a composite systems comprised of randomly distributed
 * insulating and metallic materials: what fraction of the materials need to be
 * metallic so that the composite system is an electrical conductor? Given a
 * porous landscape with water on the surface (or oil below), under what
 * conditions will the water be able to drain through to the bottom (or the oil
 * to gush through to the surface)? Scientists have defined an abstract process
 * known as percolation to model such situations.
 **************************************************************************** */

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

// Percolation using weighted quick union algorithm
public class Percolation {
    // Private attribute for initial state
    private WeightedQuickUnionUF uf;
    private boolean[][] grid;
    private int virtualTop;
    private int virtualBtm;
    private final int size;
    private int count;

    /**
     * Constructor:
     * Create n-by-n grid, with all sites blocked
     *
     * @param n dimension of the grid
     */
    public Percolation(int n) {
        // Throw an IllegalArgumentException in the constructor if n ≤ 0.
        if (n <= 0) {
            throw new IllegalArgumentException("n must be positive value");
        }

        // Initialized open sites to all false
        grid = new boolean[n][n];

        // Initialized (N * N) + 2 virtual sites union-find data structure
        uf = new WeightedQuickUnionUF(n * n + 2);

        // Initialized virtual top and bottom object
        virtualTop = 0;
        virtualBtm = (n * n) + 1;

        // Size of grid is declare as final since it does not change
        size = n;
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        checkGrid(row, col);

        // If it is already opened, then return
        if (isOpen(row, col)) return;

        // Open a site (i & j resemble the true grid coordinate values)
        int i = row - 1;
        int j = col - 1;
        grid[i][j] = true;
        count++;

        // When open a top or bottom site, always union to the virtual sites.
        // Use row & col for union data structures
        if (row == 1) {
            uf.union(get1DIndex(row, col), virtualTop);
        }
        if (row == size) {
            uf.union(get1DIndex(row, col), virtualBtm);
        }

        // Union neighbouring sites other than top and bottom
        // Top sites
        if (row > 1 && isOpen(row - 1, col)) {
            uf.union(get1DIndex(row, col), get1DIndex(row - 1, col));
        }

        // Bottom sites
        if (row < size && isOpen(row + 1, col)) {
            uf.union(get1DIndex(row, col), get1DIndex(row + 1, col));
        }

        // Left sites
        if (col > 1 && isOpen(row, col - 1)) {
            uf.union(get1DIndex(row, col), get1DIndex(row, col - 1));
        }

        // Right sites
        if (col < size && isOpen(row, col + 1)) {
            uf.union(get1DIndex(row, col), get1DIndex(row, col + 1));
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        checkGrid(row, col);
        return grid[row - 1][col - 1];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        checkGrid(row, col);

        // Return true if (row, col) belong to the set of root (top)
        return uf.find(get1DIndex(row, col)) == uf.find(virtualTop);
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return count;
    }

    // does the system percolate?
    public boolean percolates() {
        return uf.find(virtualBtm) == uf.find(virtualTop);
    }

    /* *****************************************************************************
    Private method for transforming 2D grid indexes into 1D index
    index = (row * size of grid) + column
    Let suppose 3 x 3 grid setting,
    (0, 0)  (0, 1)  (0, 2)
    (1, 0)  (1, 1)  (1, 2)  => [0, 1, 2, 3, 4, 5, 6, 7, 8]
    (2, 0)  (2, 1)  (2, 2)
    (1, 1) = (1 * 3) + 1 = 4
    (2, 2) = (2 * 3) + 2 = 8
    ***************************************************************************** */
    private int get1DIndex(int row, int col) {
        checkGrid(row, col);
        int index = (row - 1) * size + col;
        return index;
    }

    /* *****************************************************************************
    Private method for illegal argument
    row and column indices are integers between 1 and n, where (1, 1) is the upper-left site
    ***************************************************************************** */
    private void checkGrid(int row, int col) {
        if (row < 1 || col < 1 || row > size || col > size) {
            throw new IllegalArgumentException("Argument is outside its prescribed range");
        }
    }

    // test client (optional)
    public static void main(String[] args) {
        Percolation obj = new Percolation(5);
        obj.open(1, 3);
        obj.open(2, 3);
        obj.open(3, 1);
        obj.open(3, 2);
        obj.open(3, 3);
        obj.open(5, 2);
        obj.open(5, 3);
        obj.open(4, 2);
        obj.open(4, 1);
        for (int i = 0; i < obj.grid.length; i++) {
            for (int j = 0; j < obj.grid[i].length; j++) {
                System.out.print(obj.grid[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("Total number of open sites: " + obj.numberOfOpenSites());
        System.out.println("Total union set: " + obj.uf.count());
        System.out.println("Does the system percolates: " + obj.percolates());
    }
}
