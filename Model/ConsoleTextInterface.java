package Model;

import java.util.Scanner;

/**
 * Console-based text interaction utility for the Plague Doctor game
 */
public class ConsoleTextInterface {
    private Scanner scanner;
    
    public ConsoleTextInterface() {
        this.scanner = new Scanner(System.in);
    }
    
    public void display(String message) {
        System.out.println(message);
    }
    
    public String getInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
    
    public int getChoice(String prompt, int min, int max) {
        int choice = 0;
        while (choice < min || choice > max) {
            System.out.print(prompt + " (" + min + "-" + max + "): ");
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
        return choice;
    }
    
    public boolean askYesNo(String question) {
        System.out.print(question + " (y/n): ");
        String answer = scanner.nextLine().trim().toLowerCase();
        return answer.startsWith("y");
    }
}