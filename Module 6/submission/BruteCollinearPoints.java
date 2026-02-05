/* *****************************************************************************
 *  Name:              Dennis Goh Jia Wang
 *  Last modified:     1/2/2026
 **************************************************************************** */

/* *****************************************************************************
 * Brute force collinear points approach:
 * Examines 4 points at a time and checks whether they all lie on the same line
 * segment, returning all such line segments. To check whether the 4 points p,
 * q, r, and s are collinear, check whether the three slopes between p and q,
 * between p and r, and between p and s are all equal.
 *
 * Corner cases. Throw the specified exception for the following corner cases:
 * 1) Throw an IllegalArgumentException if the argument to the constructor is
 * null
 * 2) Throw an IllegalArgumentException if any point in the array is null
 * 3) Throw an IllegalArgumentExceptopm if the argument to the constructor
 * contains a repeated point.
 *
 * Performance requirements:
 * The order of growth of the running time of your program should be n^4 in the
 * worst case and it should use space proportional to n plus the number of line
 * segments returned.
 **************************************************************************** */
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BruteCollinearPoints {
    // Declaring object array as final with List will set the reference to the
    // List object (cannot reassigned to different List or object) but the
    // content is still mutable (added, removed or updated)
    private final List<LineSegment> lineSegment = new ArrayList<>();

    // Constructor: finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
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
        // of Points object (Points [])
        Arrays.sort(copy);
        int n = copy.length;
        for (int i = 1; i < n; i++) {
            if (copy[i].compareTo(copy[i - 1]) == 0) {
                throw new IllegalArgumentException("Duplicate point in points");
            }
        }

        // 1st collinear point
        for (int i = 0; i < n - 3; i++) {
            // 2nd collinear point
            for (int j = i + 1; j < n - 2; j++) {
                double s1 = copy[i].slopeTo(copy[j]);
                // 3rd collinear point
                for (int k = j + 1; k < n - 1; k++) {
                    double s2 = copy[i].slopeTo(copy[k]);

                    // Early prune (non-colinear point)
                    if (Double.compare(s1, s2) != 0)
                        continue;

                    // 4th collinear point
                    for (int l = k + 1; l < n; l++) {
                        double s3 = copy[i].slopeTo(copy[l]);

                        /*
                         4 points are collinear: copy[i], copy[j], copy[k],
                         copy[l]. Because 'copy' is sorted, endpoints are
                         i (min) and l (max)
                         */
                        if (Double.compare(s1, s3) == 0) {
                            Point min = copy[i];
                            Point max = copy[l];
                        }

                        /*
                        Extras: Enforce min-origin rule and extend-furthest rule
                        for get more than 4 collinear points
                         */
                    }
                }
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
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
