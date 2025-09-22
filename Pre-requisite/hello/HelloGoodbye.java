/* *****************************************************************************
 *  Name:              Dennis Goh Jia Wang
 *  Last modified:     September 9, 2025
 **************************************************************************** */

public class HelloGoodbye {
    public static void main(String[] args) {
        try {
            String input1 = args[0];
            String input2 = args[1];
            System.out.printf("Hello %s and %s.%n", input1, input2);
            System.out.printf("Goodbye %s and %s.%n", input2, input1);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("You must specify an argument");
        }
        catch (IllegalArgumentException e) {
            System.out.println("Bad argument: " + e.getMessage());
        }
    }
}
