/* *****************************************************************************
 *  Name:              Dennis Goh Jia Wang
 *  Last modified:     1/10/2025
 *  Interview Question: Union-Find (Quiz)
 **************************************************************************** */

/* *****************************************************************************
 * Social Network Connectivity:
 * Given a social network containing n members and a log file containing m
 * timestamps at which times pairs of members formed friendships, design an
 * algorithm to determine the earliest time at which all members are connected
 * (i.e., every member is a friend of a friend of a friend ... of a friend).
 * Assume that the log file is sorted by timestamp and that friendship is an
 * equivalence relation. The running time of the algorithm should be m log n or
 * better and use extra space proportional to n.
 **************************************************************************** */

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

/* *****************************************************************************
 * Solution:
 * Use weighted quick union data structure and for sorted log file (assume that
 * earliest to oldest), union the two people and return the earliest timestamp
 * if union.count 1 (indicate that all friends are connected)
 **************************************************************************** */

public class UFSocialNetwork {
    /* *************************************************************************
    Defining log file format:

    timestamp userA userB
    2023-01-01T10:00:00 0 1
    2023-01-01T10:05:00 2 3
    2023-01-01T10:10:00 1 2
    ...

    * Utilize builder design pattern to separate data model (Log data) and logic
    * container (log loader method) to a cleaner and more modular codebase
    e.g.
    1) The class is both data model and logic container
    public class UFSocialNetwork {
        String timestamp;
        private int userA;
        private int userB;

        public UFSocialNetwork(String timestamp, int userA, int userB) {
            this.timestamp = timestamp;
            this.userA = userA;
            this.userB = userB;
        }

        public List<UFSocialNetwork> logLoader(String filename) throws IOException {
            // Load log events into a list of UFSocialNetwork objects
        }
    }

    2) Log is a static nested class, used purely as data model
    public class UFSocialNetwork {
        public static class Log {
            private final String timestamp;
            private final int peopleA;
            private final int peopleB;

            public Log(String timestamp, int peopleA, int peopleB) {
                this.timestamp = timestamp;
                this.peopleA = peopleA;
                this.peopleB = peopleB;
            }
        }

        public static List<UFSocialNetwork.Log> loadLogFile(String filename) throws IOException {
            // Load log events into a list of Log objects
        }
    }

    ************************************************************************* */
    public static class Log {
        private final String timestamp;
        private final int userA;
        private final int userB;

        /* *********************************************************************
        Defining log file format:

        timestamp userA userB
        2023-01-01T10:00:00 0 1
        2023-01-01T10:05:00 2 3
        2023-01-01T10:10:00 1 2
        ...
        ********************************************************************* */
        public Log(String timestamp, int userA, int userB) {
            this.timestamp = timestamp;
            this.userA = userA;
            this.userB = userB;
        }
    }

    // Return appended list of Object from earliest to oldest friendship event
    public static List<UFSocialNetwork.Log> logLoader(String filename) throws IOException {
        List<UFSocialNetwork.Log> logs = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;

        // Read log file until end of file
        while ((line = reader.readLine()) != null) {
            // Store timestamp, userA and userB into array of strings
            String[] parts = line.trim().split("\\s+");
            if (parts.length != 3) continue;    // Skip malformed format

            String timestamp = parts[0];
            int userA = Integer.parseInt(parts[1]);
            int userB = Integer.parseInt(parts[2]);

            // Append log events into a list of Log objects
            logs.add(new UFSocialNetwork.Log(timestamp, userA, userB));
        }

        // Close file reader
        reader.close();
        return logs;
    }

    // Apply Union-Find to Track Connectivity
    public static String getEarliestTimestamp (int n, List<Log> sortedObj) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be positive value");
        }

        // Use weighted quick union data structure
        WeightedQuickUnionUF uf = new WeightedQuickUnionUF(n);

        // Looping ArrayList iterator
        for (Log log : sortedObj) {
            uf.union(log.userA, log.userB);

            // Check if all friends are connected
            if (uf.count() == 1) return log.timestamp;
        }

        return null;
    }

    public static void main(String[] args) throws IOException {
        int n = Integer.parseInt(args[0]);
        String filename = args[1];
        List<UFSocialNetwork.Log> logEvents = logLoader(filename);
        String earliestTime = UFSocialNetwork.getEarliestTimestamp(n, logEvents);
        StdOut.println("Earliest time all members are connected: " + earliestTime);
    }
}
