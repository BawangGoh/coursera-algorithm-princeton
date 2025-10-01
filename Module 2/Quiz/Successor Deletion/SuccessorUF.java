/* *****************************************************************************
 *  Name:              Dennis Goh Jia Wang
 *  Last modified:     1/10/2025
 *  Interview Question: Union-Find (Quiz)
 **************************************************************************** */

/* *****************************************************************************
 * Successor with delete:
 * Given a set of n integers S = {0, 1, ..., n-1} and a sequence of requests  of
 * the following form:
 * 1) Remove x from S
 * 2) Find the successor of x: the smallest y in S such that y >= x
 * Design a data type so that all operations (except construction) take logarithmic
 * time or better in the worst case.
 **************************************************************************** */

/* *****************************************************************************
 * Solution
 * There are 3 methods to solve the successor with delete:
 * 1. Union-Find with Path Compression
 * 2. Binary search trees (BST)
 * 3. Simple Array-based Data Structure
 **************************************************************************** */
