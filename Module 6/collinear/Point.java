/******************************************************************************
 *  Compilation:  javac Point.java
 *  Execution:    java Point
 *  Dependencies: none
 *
 *  An immutable data type for points in the plane.
 *  For use on Coursera, Algorithms Part I programming assignment.
 *
 ******************************************************************************/

/* *****************************************************************************
 * Collinear Points:
 * Write a program to recognize line patterns in a given set of points. Computer
 * vision involves analyzing patterns in visual images and reconstructing the
 * real-world objects that produced them.
 *
 * The process is often broken up into two phases:
 * 1) Feature detection - involves selecting important features of the image;
 * 2) Pattern recognition - involves discovering patterns in the features.
 *
 * We will investigate a particularly clean pattern recognition problem
 * involving points and line segments. This kind of pattern recognition arises
 * in many other applications such as statistical data analysis.
 *
 * Problems:
 * Given a set of n distinct points in the plane, find every (maximal) line
 * segment that connects a subset of 4 or more of the points.
 *
 * Point data type API:
 * 1) compareTo() - should compare points by their y-coordinates, breaking ties
 * by their x-coordinates. Formally, the invoking point (x0, y0) is less than
 * the argument point (x1, y1) if and only if either y0 < y1 or if y0 = y1 and
 * x0 < x1.
 * 2) slopeTo() - should return the slope between the invoking point (x0, y0)
 * and the argument point (x1, y1), which is given by the formula
 *      Slope = (y1 − y0) / (x1 − x0).
 *      i) Treat the slope of a horizontal line segment as positive zero;
 *      ii) Treat the slope of a vertical line segment as positive infinity;
 *      iii) Treat the slope of a degenerate line segment (between a point and
 *      itself) as negative infinity.
 * 3)  slopeOrder() - should return a comparator that compares its two argument
 * points by the slopes they make with the invoking point (x0, y0). Formally,
 * the point (x1, y1) is less than the point (x2, y2) if and only if the slope
 * (y1 − y0) / (x1 − x0) is less than the slope (y2 − y0) / (x2 − x0). Treat
 * horizontal, vertical, and degenerate line segments as in the slopeTo() method
 **************************************************************************** */
import java.util.Comparator;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class Point implements Comparable<Point> {

    private final int x;     // x-coordinate of this point
    private final int y;     // y-coordinate of this point

    /**
     * Initializes a new point.
     *
     * @param  x the <em>x</em>-coordinate of the point
     * @param  y the <em>y</em>-coordinate of the point
     */
    public Point(int x, int y) {
        /* DO NOT MODIFY */
        this.x = x;
        this.y = y;
    }

    /**
     * Draws this point to standard draw.
     */
    public void draw() {
        /* DO NOT MODIFY */
        StdDraw.point(x, y);
    }

    /**
     * Draws the line segment between this point and the specified point
     * to standard draw.
     *
     * @param that the other point
     */
    public void drawTo(Point that) {
        /* DO NOT MODIFY */
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    /**
     * Returns the slope between this point and the specified point.
     * Formally, if the two points are (x0, y0) and (x1, y1), then the slope
     * is (y1 - y0) / (x1 - x0). For completeness, the slope is defined to be
     * +0.0 if the line segment connecting the two points is horizontal;
     * Double.POSITIVE_INFINITY if the line segment is vertical;
     * and Double.NEGATIVE_INFINITY if (x0, y0) and (x1, y1) are equal.
     *
     * @param  that the other point
     * @return the slope between this point and the specified point
     */
    public double slopeTo(Point that) {
        /* YOUR CODE HERE */
        double dx = that.x - this.x;
        double dy = that.y - this.y;

        // Guard clauses
        if (dx == 0.0 && dy == 0.0) {
            // Degenerate case
            return Double.NEGATIVE_INFINITY;
        }
        if (dx == 0.0) {
            // Vertical slope
            return Double.POSITIVE_INFINITY;
        }
        if (dy == 0.0) {
            // Horizontal slope
            return 0.0;
        }

        return dy/dx;
    }

    /**
     * Compares two points by y-coordinate, breaking ties by x-coordinate.
     * Formally, the invoking point (x0, y0) is less than the argument point
     * (x1, y1) if and only if either y0 < y1 or if y0 = y1 and x0 < x1.
     *
     * @param  that the other point
     * @return the value <tt>0</tt> if this point is equal to the argument
     *         point (x0 = x1 and y0 = y1);
     *         a negative integer if this point is less than the argument
     *         point; and a positive integer if this point is greater than the
     *         argument point
     */
    public int compareTo(Point that) {
        /* YOUR CODE HERE */
        // Compare vertical position (y-coordinate) first, but if same height
        // (y0 == y1) then use horizontal position (x-coordinate)

        // Case 1: y0 < y1 -> (x0, y0) is smaller
        if (this.y < that.y) return -1;

        // Case 2: y0 > y1 -> (x1, y1) is smaller
        if (this.y > that.y) return +1;

        // Case 3: y0 == y1 (Tie) => check x0 and x1
        if (this.x < that.x) return -1;
        if (this.x > that.x) return +1;

        // Return 0 if y0 == y1 & x0 == x1
        return 0;
    }

    /**
     * Compares two points by the slope they make with this point.
     * The slope is defined as in the slopeTo() method.
     *
     * @return the Comparator that defines this ordering on points
     */
    public Comparator<Point> slopeOrder() {
        /* YOUR CODE HERE */
        return new SlopeOrder();
    }

    /*
     Compare other points (x1, y1), (x2, y2) relative to slope they make with
     (x0, y0)
     */
    private class SlopeOrder implements Comparator<Point> {
        @Override
        public int compare(Point p, Point q) {
            double slope1 = slopeTo(p);
            double slope2 = slopeTo(q);
            return Double.compare(slope1, slope2);
        }
    }

    /**
     * Returns a string representation of this point.
     * This method is provide for debugging;
     * your program should not rely on the format of the string representation.
     *
     * @return a string representation of this point
     */
    public String toString() {
        /* DO NOT MODIFY */
        return "(" + x + ", " + y + ")";
    }

    /**
     * Unit tests the Point data type.
     */
    public static void main(String[] args) {
        /* YOUR CODE HERE */
        Point p = new Point(19000, 10000);
        Point q = new Point(18000, 10000);

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        p.draw();
        q.draw();
        StdDraw.show();

        StdOut.println("Coordinate p: " + p);
        StdOut.println("Coordinate q: " + q);
        StdOut.println("Slope of p and q: " + p.slopeTo(q));
    }
}
