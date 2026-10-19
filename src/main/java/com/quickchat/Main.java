package com.quickchat;

import java.util.Scanner;

/**
 * Main.java
 *
 * The console program. It first runs Part 1 (register and log in) and only
 * then lets the user use QuickChat (Part 2).
 */
public class Main {

    public static void main(String[] args) {
        // Scanner object that reads what the user types
        Scanner in = new Scanner(System.in);

        // ---------- Part 1: registration ----------
        System.out.println("=== Welcome to the Chat App ===");

        // Holds the registered user; null means nobody has registered yet
        Login registeredUser = null;

        // Keep asking until registration is valid
        while (registeredUser == null) {
            System.out.println("\n--- Registration ---");

            // Variables that store what the user types in
            System.out.print("Enter first name: ");
            String firstName = in.nextLine();
            System.out.print("Enter last name: ");
            String lastName = in.nextLine();
            System.out.print("Enter a username (must contain an underscore and be no more than 5 characters): ");
            String username = in.nextLine();
            System.out.print("Enter a password (min 8 characters, 1 capital letter, 1 number, 1 special character): ");
            String password = in.nextLine();
            System.out.print("Enter your cell phone number (must include the international country code, e.g. +27...): ");
            String cell = in.nextLine();

            // Create a Login object with the details and show the registration result
            Login candidate = new Login(firstName, lastName, username, password, cell);
            System.out.println(candidate.registerUser());

            // Only accept the registration if all three rules pass
            if (candidate.checkUserName() && candidate.checkPasswordComplexity()
                    && candidate.checkCellPhoneNumber()) {
                registeredUser = candidate;
                System.out.println("Registration complete!");
            } else {
                System.out.println("Please try registering again.\n");
            }
        }

        // ---------- Part 1: login (messages are only available after a successful login) ----------

        // Tracks whether the user has logged in successfully
        boolean loggedIn = false;
        while (!loggedIn) {
            System.out.println("\n--- Login ---");
            System.out.print("Enter username: ");
            String u = in.nextLine();
            System.out.print("Enter password: ");
            String p = in.nextLine();

            // Compare what was typed with the registered details
            boolean success = registeredUser.loginUser(u, p);
            System.out.println(registeredUser.returnLoginStatus(success));

            if (success) {
                loggedIn = true;
            } else {
                System.out.print("Would you like to try again? (y/n): ");
                if (!in.nextLine().equalsIgnoreCase("y")) {
                    System.out.println("\nGoodbye!");
                    in.close();
                    return;   // not logged in, so no access to messaging
                }
            }
        }

        // ---------- Part 2: QuickChat ----------
        System.out.println("\nWelcome to QuickChat.");

        // How many messages the user wants to enter, and how many have been handled so far
        int limit = readInt(in, "How many messages do you want to enter? ", 1, Integer.MAX_VALUE);
        int processed = 0;

        // The menu repeats until the user chooses Quit
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            int option = readInt(in, "Choose an option: ", 1, 3);

            switch (option) {
                case 1:
                    // Stop the user from entering more messages than they said they would
                    if (processed >= limit) {
                        System.out.println("You have already entered all " + limit + " messages.");
                        break;
                    }
                    // Loop once for every message the user said they would enter
                    while (processed < limit) {
                        handleOneMessage(in);
                        processed++;
                    }
                    System.out.println();
                    System.out.println("Total messages sent: " + Message.returnTotalMessages());
                    break;
                case 2:
                    // Feature still in development
                    System.out.println("Coming Soon.");
                    break;
                case 3:
                    // Ends the while loop, which ends the program
                    running = false;
                    System.out.println("Goodbye!");
                    break;
            }
        }
        in.close();
    }

    /** Collects, checks and processes ONE message (recipient, text, then send/discard/store). */
    private static void handleOneMessage(Scanner in) {
        // Keep asking for the recipient until the number is valid
        String recipient;
        while (true) {
            System.out.print("Enter recipient number (with international code, e.g. +27718693002): ");
            recipient = in.nextLine().trim();
            // A temporary Message is created only so that checkRecipientCell() can be used
            String result = new Message("0000000000", 0, recipient, "x").checkRecipientCell();
            System.out.println(result);
            if (result.startsWith("Cell phone number successfully")) break;
        }

        // Keep asking for the message until it is not empty and not longer than 250 characters
        String text;
        while (true) {
            System.out.print("Enter your message (max 250 characters): ");
            text = in.nextLine().trim();
            if (text.isEmpty()) {
                System.out.println("A message cannot be empty.");
                continue;
            }
            if (text.length() <= Message.MAX_LENGTH) {
                System.out.println("Message sent");
                break;
            }
            System.out.println("Please enter a message of less than 250 characters.");
        }

        // Create the real message (this generates the ID and the hash)
        Message msg = new Message(recipient, text);

        // Ask what the user wants to do with it
        System.out.println("1) Send Message");
        System.out.println("2) Disregard Message");
        System.out.println("3) Store Message to send later");
        int choice = readInt(in, "Choose an option: ", 1, 3);
        System.out.println(msg.SentMessage(choice));

        // If the message is disregarded, the user must press 0 to delete it
        if (choice == 2) {
            readInt(in, "", 0, 0);   // only accepts 0
            System.out.println("Message deleted.");
        }

        // Show the full details of a sent message (ID, Hash, Recipient, Message)
        if (choice == 1) {
            System.out.println("Message ID: " + msg.getMessageId());
            System.out.println("Message Hash: " + msg.getMessageHash());
            System.out.println("Recipient: " + msg.getRecipient());
            System.out.println("Message: " + msg.getText());
        }
    }

    /**
     * Reads a whole number between min and max (inclusive). If the user types
     * letters or a number outside the range, it asks again instead of crashing.
     */
    private static int readInt(Scanner in, String prompt, int min, int max) {
        while (true) {
            if (!prompt.isEmpty()) System.out.print(prompt);
            String line = in.nextLine().trim();
            try {
                int v = Integer.parseInt(line);   // throws an error if the text is not a number
                if (v >= min && v <= max) return v;
            } catch (NumberFormatException ignored) { }
            System.out.println(min == max
                    ? "Please press " + min + "."
                    : "Please enter a number between " + min + " and " + max + ".");
        }
    }
}
