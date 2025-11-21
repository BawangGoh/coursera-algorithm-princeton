// Basic Java implementation for Linked List Stack data structure
import java.util.NoSuchElementException;

public class LinkedStackOfStrings {
    // Only required one pointer for top of the stack
    private Node current = null;

    // Static mested class for Node for better data encapsulation, memory
    // allocation and prevent potential memory leaks if non-static inner class
    // outlive the outer class
    private static class Node {
        String item;    // Data container
        Node next;      // Pointer for next node
    }

    public boolean isEmpty() {
        return current == null;
    }

    /* *************************************************************************
     * Stack push & pop operation:
     * Last-In-First-Out (LIFO) that last element go in will be the first to
     * remove. Use 2 pointer which are current and previous pointer to insert
     * new element.
     ************************************************************************ */
    public void push(String item) {
        // Initialize new previous pointer to current element
        Node previous = current;

        // Use current pointer to insert new node
        current = new Node();
        current.item = item;

        // Use current pointer to point to previous element
        current.next = previous;
    }

    public String pop() {
        if (isEmpty()) throw new NoSuchElementException("Stack underflow");

        // Get the top of stack element
        String item = current.item;

        // Point the current pointer to next element
        current = current.next;
        return item;
    }
}
