# ST10467162-PART2-PROG
QuickChat

A console-based chat application written in Java as part of my PoE (Part 2: Sending Messages). It builds on my Part 1 registration and login system and adds sending, discarding and storing messages.

Features
Register and log in (from Part 1). Messaging is only available after a successful login.
Welcome message: "Welcome to QuickChat."
Numeric menu:
Send Messages
Show recently sent messages (displays "Coming Soon.")
Quit
The user says how many messages they want to enter, and the app only allows that many.
Each message has:
a random ten-digit Message ID
an auto-incrementing message number (starts at 0)
a recipient cell number with an international code
a message of at most 250 characters
a Message Hash in the format ID(first two digits):NUMBER:FIRSTLAST, in capitals (for example 00:0:HITONIGHT)
After each message the user can send it, disregard it (press 0 to delete) or store it to send later (saved to a JSON file).
The details of a sent message are shown in the order: Message ID, Message Hash, Recipient, Message.
The total number of messages sent is displayed at the end.
Project structure
QuickChat/
├── pom.xml                                  Maven build file
├── .github/workflows/maven.yml              GitHub Actions workflow (runs the tests)
└── src/
    ├── main/java/com/quickchat/
    │   ├── Main.java                        Console program and menu
    │   ├── Login.java                       Registration and login (Part 1)
    │   └── Message.java                     Message logic (hash, send, store, totals)
    └── test/java/com/quickchat/
        └── MessageTest.java                 JUnit tests
The Message class
Method	What it does
checkMessageID()	Returns true if the message ID is not more than ten characters
checkRecipientCell()	Checks the recipient number (reuses the Part 1 cell number check)
createMessageHash()	Builds and returns the message hash
SentMessage(int choice)	Sends (1), disregards (2) or stores (3) the message and returns the matching text
printMessages()	Returns the details of all messages sent while the program is running
returnTotalMessages()	Returns the total number of messages sent
storeMessage()	Saves stored messages to messages.json
Requirements
Java JDK 17 or newer
Apache Maven (NetBeans includes it)
How to run

In NetBeans

Open the project: File > Open Project and choose the QuickChat folder.
Right-click Main.java and choose Run File.
Type your input in the Output window.

From the command line

mvn compile exec:java -Dexec.mainClass=com.quickchat.Main
Example input
Prompt	Example
Username	ab_cd (needs an underscore, 5 characters or fewer)
Password	Pass@1234 (8+ characters, a capital, a number, a special character)
Cell number	+27718693002
Recipient	+27718693002
Message	Hi Mike, can you join us for dinner tonight?
Unit tests

The tests use JUnit 5 and cover:

the 250-character message rule (success and failure)
recipient number format (valid and invalid)
the message hash (test case 1, and all test cases in a loop)
message ID creation
send, disregard and store outcomes
the total number of messages sent, printing sent messages, and the message counter

To run them:

mvn test

In NetBeans: right-click the project and choose Test.

Continuous Integration

A GitHub Actions workflow (.github/workflows/maven.yml) builds the project and runs all the tests automatically every time code is pushed or a pull request is made to main.

Assumptions
The brief says the recipient number must be no more than ten characters, but its own example (+27718693002) is longer. I used my Part 1 rule: a +, a 2-3 digit international code, then up to ten digits.
The message number starts at 0, to match the hash example 00:0:... in the brief.
Stored messages are written to messages.json in the folder the program is run from. The file is rewritten on each store.
Only sent messages have their full details displayed after sending.
Console only

As required, the application is a console application. It does not use any GUI components such as JOptionPane.
