package patterns.chat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Demonstrates the chat server with three users: send, block, undo, and iterate.
 */
@SuppressWarnings("rawtypes")
public final class ChatDriver {
    private ChatDriver() {
    }

    public static void main(String[] args) {
        run().forEach(System.out::println);
    }

    public static List<String> run() {
        ChatServer server = new ChatServer();
        User alice = new User("Alice", server);
        User bob = new User("Bob", server);
        User charlie = new User("Charlie", server);

        List<String> lines = new ArrayList<>();
        lines.add("Registered users: " + alice.getName() + ", " + bob.getName() + ", " + charlie.getName());

        alice.send("Hello Bob", bob);
        lines.add(alice.getChatHistory().getLastMessageSent().toString());
        lines.add("Charlie has Hello Bob: " + contains(charlie, "Hello Bob"));

        alice.send("Hello everyone", bob, charlie);
        lines.add(alice.getChatHistory().getLastMessageSent().toString());
        lines.add("Bob has Hello everyone: " + contains(bob, "Hello everyone"));
        lines.add("Charlie has Hello everyone: " + contains(charlie, "Hello everyone"));

        charlie.block(bob);
        lines.add("Charlie blocks messages from Bob");
        bob.send("Ping", alice, charlie);
        lines.add("Alice has Ping: " + contains(alice, "Ping"));
        lines.add("Charlie has Ping: " + contains(charlie, "Ping"));

        Message undone = alice.undoLastMessage();
        lines.add("Undid last message: " + undone.createMemento());
        lines.add("Alice last sent message: " + alice.getChatHistory().getLastMessageSent().getContent());
        lines.add("Bob has Hello everyone: " + contains(bob, "Hello everyone"));
        lines.add("Charlie has Hello everyone: " + contains(charlie, "Hello everyone"));

        lines.add("Alice history with Bob:");
        Iterator messagesWithBob = alice.iterator(bob);
        while (messagesWithBob.hasNext()) {
            lines.add("  " + messagesWithBob.next());
        }
        return lines;
    }

    private static boolean contains(User user, String content) {
        for (Message message : user.getChatHistory().getMessages()) {
            if (message.getContent().equals(content)) {
                return true;
            }
        }
        return false;
    }
}
