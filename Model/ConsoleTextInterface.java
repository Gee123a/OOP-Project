package Model;

import java.util.Scanner;

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
                if (choice < min || choice > max) {
                    System.out.println("Please enter a valid number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
        return choice;
    }

    public boolean askYesNo(String question) {
        while (true) {
            System.out.print(question + " (y/n): ");
            String answer = scanner.nextLine().trim().toLowerCase();
            if (answer.equals("y") || answer.equals("n")) {
                return answer.equals("y");
            } else {
                System.out.println("Input is not valid. Please enter 'y' for yes or 'n' for no.");
            }
        }
    }

    private void pause(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void shortPause() {
        pause(800);
    } // 0.8 seconds

    public void mediumPause() {
        pause(1500);
    } // 1.5 seconds

    public void longPause() {
        pause(2500);
    } // 2.5 seconds

    public void dramaticPause() {
        pause(3000);
    } // 3 seconds

    public void displayWithDelay(String message, int delayMs) {
        System.out.println(message);
        pause(delayMs);
    }

    public void displayDramatic(String message) {
        System.out.println(message);
        dramaticPause(); // 3 second pause for drama
    }

    public void displayStory(String message) {
        System.out.println(message);
        mediumPause(); // 1.5 second pause
    }

    public void displayQuick(String message) {
        System.out.println(message);
        shortPause(); // 0.8 second pause
    }

    public void displayTypewriter(String message, int charDelayMs) {
        for (char c : message.toCharArray()) {
            System.out.print(c);
            pause(charDelayMs);
        }
        System.out.println(); // New line after complete message
        shortPause(); // Brief pause after completion
    }

    public void displayStorySequence(String[] messages, boolean waitForUser) {
        for (int i = 0; i < messages.length; i++) {
            display(messages[i]);
            
            if (waitForUser && i < messages.length - 1) {
                waitForEnter("");
            } else if (i < messages.length - 1) {
                mediumPause(); // Auto-advance with 1.5s delay
            }
        }
    }

     public void waitForEnter(String prompt) {
        if (prompt.isEmpty()) {
            System.out.print("(Press ENTER to continue...)");
        } else {
            System.out.print(prompt + " (Press ENTER to continue...)");
        }
        scanner.nextLine();
    }

    public void displayHeader(String header) {
        display("\n" + "=".repeat(header.length() + 8));
        display("    " + header.toUpperCase());
        display("=".repeat(header.length() + 8));
        shortPause();
    }

    // Display character dialogue with appropriate pacing
    public void displayDialogue(String speaker, String dialogue) {
        display(speaker + ": \"" + dialogue + "\"");
        mediumPause(); // Give time to read dialogue
    }

    // Display system message with quick pause
    public void displaySystem(String message) {
        display("[SYSTEM] " + message);
        shortPause();
    }

    // Display important notifications
    public void displayNotification(String message) {
        display("\n*** " + message + " ***");
        mediumPause();
    }
}