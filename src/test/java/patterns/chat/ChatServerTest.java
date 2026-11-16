package patterns.chat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatServerTest {
    private ChatServer server;
    private User alice;
    private User bob;
    private User charlie;

    @BeforeEach
    void setUp() {
        server = new ChatServer();
        alice = new User("Alice", server);
        bob = new User("Bob", server);
        charlie = new User("Charlie", server);
    }

    @Test
    void registersAndUnregistersUsers() {
        assertTrue(server.isRegistered(alice));
        assertThrows(IllegalArgumentException.class, () -> server.register(alice));

        server.unregister(charlie);

        assertFalse(server.isRegistered(charlie));
        assertThrows(IllegalArgumentException.class, () -> alice.send("Hey", charlie));
        assertTrue(alice.getChatHistory().getMessages().isEmpty());

        server.register(charlie);
        alice.send("Hey", charlie);
        assertEquals(List.of("Hey"), contents(charlie));
    }

    @Test
    void doesNotDeliverABlockedMessage() {
        server.block(bob, alice);
        Message message = new Message(alice, List.of(bob, charlie), "Hello");

        server.sendMessage(message);

        assertTrue(bob.getChatHistory().getMessages().isEmpty());
        assertEquals(List.of("Hello"), contents(charlie));
    }

    @Test
    void recallRemovesTheDeliveredMessage() {
        Message message = new Message(alice, List.of(bob, charlie), "Hello");
        server.sendMessage(message);

        server.recall(message);

        assertTrue(bob.getChatHistory().getMessages().isEmpty());
        assertTrue(charlie.getChatHistory().getMessages().isEmpty());
    }

    @Test
    void rejectsUnknownUsersAndSelfBlocks() {
        server.unregister(charlie);

        assertThrows(IllegalArgumentException.class,
                () -> server.sendMessage(new Message(alice, List.of(charlie), "Hello")));
        assertThrows(IllegalArgumentException.class, () -> server.block(alice, alice));
        assertThrows(IllegalArgumentException.class, () -> server.unregister(charlie));
    }

    private static List<String> contents(User user) {
        return user.getChatHistory().getMessages().stream().map(Message::getContent).toList();
    }
}
