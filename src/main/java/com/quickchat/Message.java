package com.quickchat;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Message.java
 *
 * Represents one chat message. It also keeps track of information that is
 * shared by ALL messages while the program runs (the list of sent messages,
 * the list of stored messages and the totals). That shared information is
 * declared "static", which means it belongs to the class, not to one message.
 */
public class Message {

    // Maximum number of characters allowed in a message (a constant, so it can never change)
    public static final int MAX_LENGTH = 250;

    // Name of the file that stored messages are saved to
    public static final String STORE_FILE = "messages.json";

    // ---- static variables: shared by every Message object ----

    // List that holds every message the user chose to SEND
    private static final List<Message> sentMessages = new ArrayList<>();

    // List that holds every message the user chose to STORE for later
    private static final List<Message> storedMessages = new ArrayList<>();

    // Counts how many messages have actually been sent
    private static int totalSent = 0;

    // Auto-incrementing message number: the first message is 0, then 1, 2 and so on
    private static int numMessages = 0;

    // ---- instance variables: each Message object has its own copy ----

    // The ten-digit random ID used to track the message
    private final String messageId;

    // This message's number (taken from numMessages when it was created)
    private final int messageNumber;

    // The cell phone number the message is being sent to
    private final String recipient;

    // The actual message text
    private final String text;

    // The hash made from the ID, message number and first and last words
    private final String messageHash;

    /**
     * Normal constructor, used by the running program.
     * It generates a random ID and uses the next message number
     * (numMessages++ gives the current value and then adds 1).
     */
    public Message(String recipient, String text) {
        this(generateMessageId(), numMessages++, recipient, text);
    }

    /**
     * Second constructor, which lets the unit tests choose the ID and the
     * message number so that the results are predictable.
     * The hash is created as soon as the message is built.
     */
    public Message(String messageId, int messageNumber, String recipient, String text) {
        this.messageId = messageId;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.text = text;
        this.messageHash = createMessageHash();
    }

    // ------------------------------------------------------------------
    // Methods required by the brief
    // ------------------------------------------------------------------

    /** Checks that the message ID is not more than ten characters long. Returns true or false. */
    public boolean checkMessageID() {
        return messageId != null && messageId.length() <= 10;
    }

    /**
     * Checks the recipient's number. It reuses checkCellPhoneNumber() from the
     * Login class (Part 1), as the brief's hint says, and returns the success
     * or failure message that the brief asks for.
     */
    public String checkRecipientCell() {
        if (new Login().checkCellPhoneNumber(recipient)) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.";
    }

    /**
     * Creates the message hash in the format  ID:NUMBER:FIRSTLAST  in capitals.
     * Example: ID 0012345678, number 0, message "Hi Mike ... tonight?" gives 00:0:HITONIGHT
     */
    public String createMessageHash() {
        // First two characters of the message ID
        String idPart = messageId.length() >= 2 ? messageId.substring(0, 2) : messageId;

        // Split the message into words (\\s+ means "one or more spaces")
        String[] words = text.trim().split("\\s+");

        // First word and last word, with punctuation such as , and ? removed
        String first = stripPunctuation(words[0]);
        String last = stripPunctuation(words[words.length - 1]);

        // Join the parts together and change everything to capital letters
        return (idPart + ":" + messageNumber + ":" + first + last).toUpperCase();
    }

    /**
     * Carries out what the user chose to do with the message:
     * 1 = send it, 2 = disregard it, 3 = store it to send later.
     * Returns the message that must be shown on the screen.
     */
    public String SentMessage(int choice) {
        switch (choice) {
            case 1:
                // Add to the sent list and increase the total
                sentMessages.add(this);
                totalSent++;
                return "Message successfully sent.";
            case 2:
                // Nothing is saved; the user must press 0 to delete (handled in Main)
                return "Press 0 to delete the message.";
            case 3:
                // Add to the stored list and save the list to the JSON file
                storedMessages.add(this);
                storeMessage();
                return "Message successfully stored.";
            default:
                return "Invalid option.";
        }
    }

    /** Returns the details of all the messages that have been sent while the program is running. */
    public static String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent yet.";
        }
        // StringBuilder is used to join many pieces of text efficiently
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append(m.getDetails()).append(System.lineSeparator());
        }
        return sb.toString();
    }

    /** Returns the total number of messages that have been sent. */
    public static int returnTotalMessages() {
        return totalSent;
    }

    /**
     * Saves every stored message to messages.json.
     * JSON is a text format made of "name": "value" pairs. The whole file is
     * rewritten each time so that it is always valid JSON.
     */
    public void storeMessage() {
        StringBuilder json = new StringBuilder("[\n");
        // Loop through the stored messages and write each one as a JSON object
        for (int i = 0; i < storedMessages.size(); i++) {
            Message m = storedMessages.get(i);
            json.append("  {\n")
                .append("    \"messageID\": \"").append(escape(m.messageId)).append("\",\n")
                .append("    \"messageHash\": \"").append(escape(m.messageHash)).append("\",\n")
                .append("    \"recipient\": \"").append(escape(m.recipient)).append("\",\n")
                .append("    \"message\": \"").append(escape(m.text)).append("\"\n")
                // No comma after the last object, otherwise the JSON would be invalid
                .append("  }").append(i < storedMessages.size() - 1 ? "," : "").append("\n");
        }
        json.append("]\n");
        // try-with-resources closes the file automatically, even if an error happens
        try (FileWriter fw = new FileWriter(STORE_FILE)) {
            fw.write(json.toString());
        } catch (IOException e) {
            System.out.println("Could not write to " + STORE_FILE + ": " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Helper methods
    // ------------------------------------------------------------------

    /** Checks the 250-character rule and returns the success or failure message. */
    public static String checkMessageLength(String text) {
        if (text != null && text.length() <= MAX_LENGTH) {
            return "Message ready to send.";
        }
        // Work out by how many characters the message is too long
        int over = (text == null ? 0 : text.length()) - MAX_LENGTH;
        return "Message exceeds 250 characters by " + over + "; please reduce the size.";
    }

    /** Returns the details in the order the brief asks for: ID, Hash, Recipient, Message. */
    public String getDetails() {
        return "Message ID: " + messageId
                + " | Message Hash: " + messageHash
                + " | Recipient: " + recipient
                + " | Message: " + text;
    }

    /** Generates a random ten-digit ID. %010d pads with zeros so it is always ten digits. */
    public static String generateMessageId() {
        long n = ThreadLocalRandom.current().nextLong(10_000_000_000L);
        return String.format("%010d", n);
    }

    /** Clears all the shared information. Only the unit tests use this, so each test starts fresh. */
    public static void resetState() {
        sentMessages.clear();
        storedMessages.clear();
        totalSent = 0;
        numMessages = 0;
    }

    // Getters: let other classes read the private variables
    public String getMessageId() { return messageId; }
    public String getMessageHash() { return messageHash; }
    public String getRecipient() { return recipient; }
    public String getText() { return text; }
    public int getMessageNumber() { return messageNumber; }

    /** Removes everything that is not a letter or a number (e.g. the comma in "Mike,"). */
    private static String stripPunctuation(String word) {
        return word.replaceAll("[^A-Za-z0-9]", "");
    }

    /** Makes text safe to place inside JSON by escaping quotes, backslashes and new lines. */
    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r");
    }
}
