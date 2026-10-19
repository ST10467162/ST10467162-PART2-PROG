package com.quickchat;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * MessageTest.java
 *
 * JUnit tests for the Message class. Each method marked @Test is one test.
 * assertEquals(expected, actual) passes only if both values are the same;
 * assertTrue / assertFalse pass only if the condition is true / false.
 */
class MessageTest {

    // Test data from the brief (two test cases)
    private static final String RECIPIENT_1 = "+27718693002";
    private static final String TEXT_1 = "Hi Mike, can you join us for dinner tonight?";
    private static final String RECIPIENT_2 = "08575975889";
    private static final String TEXT_2 = "Hi Keegan, did you receive the payment?";

    // Runs before EVERY test so the counters and lists start empty each time
    @BeforeEach
    void setUp() {
        Message.resetState();
    }

    // ---------- Message length ----------
    // Short message: should say it is ready to send
    @Test
    void messageLength_success() {
        assertEquals("Message ready to send.", Message.checkMessageLength(TEXT_1));
    }

    // 260 characters is 10 over the limit, so the failure message must say 10
    @Test
    void messageLength_failure() {
        String longText = "a".repeat(260);
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                Message.checkMessageLength(longText));
    }

    // ---------- Recipient number ----------
    // Number with an international code (+27...) must be accepted
    @Test
    void recipient_success() {
        Message m = new Message("0012345678", 0, RECIPIENT_1, TEXT_1);
        assertEquals("Cell phone number successfully captured.", m.checkRecipientCell());
    }

    // Test Case 2's number starts with 0, not a code, so it must be rejected
    @Test
    void recipient_failure_noInternationalCode() {
        Message m = new Message("0012345678", 1, RECIPIENT_2, TEXT_2);
        assertEquals("Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.",
                m.checkRecipientCell());
    }

    // ---------- Message hash ----------
    // Test Case 1: first word HI + last word TONIGHT
    @Test
    void messageHash_testCase1() {
        Message m = new Message("0012345678", 0, RECIPIENT_1, TEXT_1);
        assertEquals("00:0:HITONIGHT", m.createMessageHash());
    }

    // Tests every test case in a loop: arrays hold the data, and index i picks one message each time
    @Test
    void messageHash_allTestCasesInLoop() {
        String[] ids = {"0012345678", "0112345678"};
        int[] numbers = {0, 1};
        String[] recipients = {RECIPIENT_1, RECIPIENT_2};
        String[] texts = {TEXT_1, TEXT_2};
        String[] expected = {"00:0:HITONIGHT", "01:1:HIPAYMENT"};

        for (int i = 0; i < expected.length; i++) {
            Message m = new Message(ids[i], numbers[i], recipients[i], texts[i]);
            assertEquals(expected[i], m.createMessageHash(), "Hash mismatch for message " + (i + 1));
        }
    }

    // The example hash printed in the brief
    @Test
    void messageHash_exampleFromBrief() {
        Message m = new Message("0012345678", 0, RECIPIENT_1, "Hi, thanks");
        assertEquals("00:0:HITHANKS", m.createMessageHash());
    }

    // ---------- Message ID ----------
    // A generated ID must be valid, ten digits long and made of digits only
    @Test
    void messageId_isCreatedAndValid() {
        Message m = new Message(RECIPIENT_1, TEXT_1);
        assertTrue(m.checkMessageID());
        assertEquals(10, m.getMessageId().length());
        assertTrue(m.getMessageId().matches("\\d{10}"));
        System.out.println("Message ID generated: " + m.getMessageId());
    }

    // An 11-character ID must fail the ten-character check
    @Test
    void messageId_tooLongFails() {
        Message m = new Message("12345678901", 0, RECIPIENT_1, TEXT_1);
        assertFalse(m.checkMessageID());
    }

    // ---------- Send / Discard / Store ----------
    // The user chose Send
    @Test
    void sentMessage_send() {
        Message m = new Message(RECIPIENT_1, TEXT_1);
        assertEquals("Message successfully sent.", m.SentMessage(1));
    }

    // The user chose Disregard
    @Test
    void sentMessage_disregard() {
        Message m = new Message(RECIPIENT_2, TEXT_2);
        assertEquals("Press 0 to delete the message.", m.SentMessage(2));
    }

    // The user chose Store
    @Test
    void sentMessage_store() {
        Message m = new Message(RECIPIENT_1, TEXT_1);
        assertEquals("Message successfully stored.", m.SentMessage(3));
    }

    // ---------- Totals and printing ----------
    // One message sent and one discarded, so the total must be 1
    @Test
    void totalMessages_testData() {
        Message first = new Message(RECIPIENT_1, TEXT_1);
        first.SentMessage(1);                       // Send
        Message second = new Message(RECIPIENT_2, TEXT_2);
        second.SentMessage(2);                      // Discard
        assertEquals(1, Message.returnTotalMessages());
    }

    // Only the sent message may appear in the printed list, not the discarded one
    @Test
    void printMessages_containsSentMessageOnly() {
        Message first = new Message(RECIPIENT_1, TEXT_1);
        first.SentMessage(1);
        Message second = new Message(RECIPIENT_2, TEXT_2);
        second.SentMessage(2);

        String printed = Message.printMessages();
        assertTrue(printed.contains(first.getMessageId()));
        assertTrue(printed.contains(TEXT_1));
        assertFalse(printed.contains(TEXT_2));
    }

    // First message is number 0, the next one is 1
    @Test
    void messageNumber_autoIncrements() {
        Message a = new Message(RECIPIENT_1, TEXT_1);
        Message b = new Message(RECIPIENT_1, TEXT_1);
        assertEquals(0, a.getMessageNumber());
        assertEquals(1, b.getMessageNumber());
    }
}
