package patterns.chat;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageTest {
    @Test
    void storesSenderRecipientsTimestampAndContent() {
        ChatServer server = new ChatServer();
        User alice = new User("Alice", server);
        User bob = new User("Bob", server);
        User charlie = new User("Charlie", server);
        LocalDateTime timestamp = LocalDateTime.of(2026, 11, 15, 12, 30);

        Message message = new Message(alice, List.of(bob, charlie, bob), "Hello", timestamp);

        assertEquals(alice, message.getSender());
        assertEquals(List.of(bob, charlie), message.getRecipients());
        assertEquals(timestamp, message.getTimestamp());
        assertEquals("Hello", message.getContent());
        assertEquals("Alice to Bob, Charlie: Hello", message.toString());
        assertTrue(message.involves(bob));
        assertTrue(message.involves(alice));
    }

    @Test
    void mementoCapturesContentAndTimestamp() {
        ChatServer server = new ChatServer();
        User alice = new User("Alice", server);
        User bob = new User("Bob", server);
        LocalDateTime timestamp = LocalDateTime.of(2026, 11, 15, 12, 30);
        Message message = new Message(alice, List.of(bob), "Hello", timestamp);

        MessageMemento memento = message.createMemento();

        assertEquals("Hello", memento.getContent());
        assertEquals(timestamp, memento.getTimestamp());
        assertTrue(message.matches(memento));
    }

    @Test
    void equalityUsesEveryField() {
        ChatServer server = new ChatServer();
        User alice = new User("Alice", server);
        User bob = new User("Bob", server);
        LocalDateTime timestamp = LocalDateTime.of(2026, 11, 15, 12, 30);
        Message message = new Message(alice, List.of(bob), "Hello", timestamp);
        Message same = new Message(alice, List.of(bob), "Hello", timestamp);
        Message differentText = new Message(alice, List.of(bob), "Goodbye", timestamp);

        assertEquals(message, same);
        assertEquals(message.hashCode(), same.hashCode());
        assertNotEquals(message, differentText);
    }

    @Test
    void rejectsIncompleteMessages() {
        ChatServer server = new ChatServer();
        User alice = new User("Alice", server);
        User bob = new User("Bob", server);

        assertThrows(IllegalArgumentException.class, () -> new Message(null, List.of(bob), "Hello"));
        assertThrows(IllegalArgumentException.class, () -> new Message(alice, List.of(), "Hello"));
        assertThrows(IllegalArgumentException.class, () -> new Message(alice, List.of(bob), " "));
        assertThrows(IllegalArgumentException.class, () -> new Message(alice, List.of(alice), "Hello"));
    }
}
