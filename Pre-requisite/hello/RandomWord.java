/* *****************************************************************************
 *  Name:              Dennis Goh Jia Wang
 *  Last modified:     September 9, 2025
 **************************************************************************** */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class RandomWord {
    public static void main(String[] args) {
        // Keep track of number of inputs
        int index = 0;

        // Return champion if bernoulli sampling return true
        String champion = "";

        // Using Knuth's method: Reservoir sampling technique that only required O(1) space complexity
        while (!StdIn.isEmpty()) {
            String words = StdIn.readString();
            index++;
            boolean sample = StdRandom.bernoulli(1.0 / (index));

            // If bernoulli sample return true then champion is return
            if (sample) {
                champion = words;

                // DEBUG
                // StdOut.println("New champion: " + champion);
            }
        }

        // DEBUG
        // StdOut.println("Surviving champion: " + champion);
        StdOut.println(champion);
    }
}
