package patterns.chat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ChatHistoryTest {
    private User alice;
    private User bob;
    private User charlie;
    private LocalDateTime timestamp;

    @BeforeEach
    void setUp() {
        ChatServer server = new ChatServer();
        alice = new User("Alice", server);
        bob = new User("Bob", server);
        charlie = new User("Charlie", server);
        timestamp = LocalDateTime.of(2026, 11, 15, 12, 0);
    }

    @Test
    void addsMessagesAndReturnsTheLastOneThisUserSent() {
        Message received = new Message(bob, List.of(alice), "Hi", timestamp);
        Message sent = new Message(alice, List.of(bob), "Hello", timestamp.plusMinutes(1));
        alice.getChatHistory().addMessage(received);
        alice.getChatHistory().addMessage(sent);

        assertEquals(List.of(received, sent), alice.getChatHistory().getMessages());
        assertSame(sent, alice.getChatHistory().getLastMessageSent());
        assertNull(charlie.getChatHistory().getLastMessageSent());
    }

    @Test
    void removesTheMessageMatchingAMemento() {
        Message first = new Message(alice, List.of(bob), "one", timestamp);
        Message second = new Message(alice, List.of(bob), "two", timestamp.plusMinutes(1));
        alice.getChatHistory().addMessage(first);
        alice.getChatHistory().addMessage(second);

        Message removed = alice.getChatHistory().removeMatching(second.createMemento(), alice);

        assertSame(second, removed);
        assertEquals(List.of(first), alice.getChatHistory().getMessages());
    }

    @Test
    void returnedHistoryCannotBeEdited() {
        Message message = new Message(alice, List.of(bob), "Hello", timestamp);
        alice.getChatHistory().addMessage(message);

        assertThrows(UnsupportedOperationException.class,
                () -> alice.getChatHistory().getMessages().add(message));
    }

    @Test
    void rejectsMissingMessages() {
        assertThrows(IllegalArgumentException.class, () -> new ChatHistory(null));
        assertThrows(IllegalArgumentException.class, () -> alice.getChatHistory().addMessage(null));
    }
}
