/* *****************************************************************************
 *  Name:              Dennis Goh Jia Wang
 *  Last modified:     1/2/2026
 **************************************************************************** */

/* *****************************************************************************
 * Fast collinear points approach:
 * A faster, sorting-based solution to collinear points. Given a point p, the
 * following method determines whether p participates in a set of 4 or more
 * collinear points.
 * 1) Think of p as the origin.
 * 2) For each other point q, determine the slope it makes with p.
 * 3) Sort the points according to the slopes they makes with p.
 * 4) Check if any 3 (or more) adjacent points in the sorted order have equal
 * slopes with respect to p. If so, these points, together with p, are collinear.
 *
 * Applying this method for each of the n points in turn yields an efficient
 * algorithm to the problem. The algorithm solves the problem because points
 * that have equal slopes with respect to p are collinear, and sorting brings
 * such points together. The algorithm is fast because the bottleneck operation
 * is sorting.
 *
 * Corner cases. Throw the specified exception for the following corner cases:
 * 1) Throw an IllegalArgumentException if the argument to the constructor is
 * null
 * 2) Throw an IllegalArgumentException if any point in the array is null
 * 3) Throw an IllegalArgumentExceptopm if the argument to the constructor
 * contains a repeated point.
 *
 * Performance requirements:
 * The order of growth of the running time of your program should be (n^2 log n)
 * in the worst case and it should use space proportional to n plus the number
 * of line segments returned. FastCollinearPoints should work properly even if
 * the input has 5 or more collinear points.
 **************************************************************************** */
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FastCollinearPoints {
    private final List<LineSegment> lineSegment = new ArrayList<>();

    // Constructor: finds all line segments containing 4 points
    public FastCollinearPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException("Points is null");
        }

        // Deep copy the points to prevent mutating internal arrays
        Point[] copy = points.clone();

        // Throw exception if one of the points is null
        for (Point p : copy) {
            if (p == null) {
                throw new IllegalArgumentException("Point is null");
            }
        }

        // Use comparable interface compareTo() natural ordering to sort array
        Arrays.sort(copy);
        int n = copy.length;
        for (int i = 1; i < n; i++) {
            if (copy[i].compareTo(copy[i - 1]) == 0) {
                throw new IllegalArgumentException("Duplicate point in points");
            }
        }

        // Loop till n - 3 (searching for other 3 collinear points)
        for (int i = 0; i < n - 3; i++) {
            Point origin = copy[i];

            // Copy points since every point have different slope order
            Point[] allPoints = points.clone();

            // Sort slope of every points with respect to the origin
            Arrays.sort(allPoints, origin.slopeOrder());

            // Scan through allPoints to find contiguous runs of equal slopes to
            // 'origin', index 0 will be 'origin' itself (slope = -inf), j = 1
            int j = 1;
            while (j < allPoints.length) {
                double currSlope = origin.slopeTo(allPoints[j]);

                // Gather all points with the same slope to 'origin
                int runStart = j;
                while (j < allPoints.length && Double.compare(origin.slopeTo(allPoints[j]), currSlope) == 0) {
                    j++;
                }

                // number of points in this equal-slope run (excluding 'origin')
                int runLen = j - runStart;

                // If runLen >= 3, then with 'origin' we have at least 4
                // collinear point
                if (runLen >= 3) {
                    // Initialize min and max as origin
                    Point min = origin;
                    Point max = origin;
                    for (int k = runStart; k < j; k++) {
                        Point q = allPoints[k];

                        // Determine minimal and maximal points in the collinear
                        // set
                        if (q.compareTo(min) < 0) min = q;
                        if (q.compareTo(max) > 0) max = q;
                    }

                    // Enforce uniqueness: only add if 'origin' is the smallest
                    // point in the set
                    if (origin.compareTo(min) == 0) {
                        lineSegment.add(new LineSegment(min, max));
                    }
                }
                // continue with next slope run (j already advanced
            }
        }
    }

    // Number of line segments
    public int numberOfSegments() {
        return lineSegment.size();
    }

    // Line segments
    public LineSegment[] segments() {
        /*
         Preferred method: Pass an empty array of the Object type to specify the
         runtime type of the resulting array to avoid the need for casting each
         element
         */
        return lineSegment.toArray(new LineSegment[0]);
    }

    // Optional: Client API test
    public static void main(String[] args) {
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
