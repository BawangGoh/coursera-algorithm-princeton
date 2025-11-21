// Basic Java implementation for Linked List Queue data structure
import java.util.NoSuchElementException;

public class LinkedQueueOfStrings {
    // Required two pointer for both end of queue
    private Node first = null;
    private Node last = null;

    // Static mested class for Node for better data encapsulation, memory
    // allocation and prevent potential memory leaks if non-static inner class
    // outlive the outer class
    private static class Node {
        String item;    // Data container
        Node next;      // Pointer for next node
    }

    public boolean isEmpty() {
        return first == null;
    }

    /* *************************************************************************
     * Queue enqueue & dequeue operation:
     * First-In-First-Out (FIFO) that first element go in will be the first to
     * remove. Use 3 pointer which are current, first and last pointer to insert
     * new element.
     ************************************************************************ */
    public void enqueue(String item) {
        // Initialize new current pointer to last element
        Node current = last;

        // Use last pointer to insert new node
        last = new Node();
        last.item = item;
        last.next = null;

        // Check if it empty queue, else point current to last element
        if (isEmpty()) first = last;
        else current.next = last;
    }

    public String dequeue() {
        if (isEmpty()) throw new NoSuchElementException("Stack underflow");

        // Get the first element from queue
        String item = first.item;

        // Point the first pointer to next element
        first = first.next;

        // Check if empty queue after removing first element
        if (isEmpty()) last = null;
        return item;
    }
}
