package patterns.chat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MediatorMementoTest {
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
    void sendsToOneUserThroughTheServer() {
        alice.send("Hello Bob", bob);

        Message sent = alice.getChatHistory().getLastMessageSent();
        assertEquals("Hello Bob", sent.getContent());
        assertEquals(alice, sent.getSender());
        assertEquals(List.of(bob), sent.getRecipients());
        assertSame(sent, bob.getChatHistory().getMessages().get(0));
        assertTrue(charlie.getChatHistory().getMessages().isEmpty());
    }

    @Test
    void sendsToSeveralUsersThroughTheServer() {
        alice.send("Hello everyone", bob, charlie);

        assertEquals("Alice to Bob, Charlie: Hello everyone",
                alice.getChatHistory().getLastMessageSent().toString());
        assertEquals("Hello everyone", bob.getChatHistory().getMessages().get(0).getContent());
        assertEquals("Hello everyone", charlie.getChatHistory().getMessages().get(0).getContent());
        assertEquals(1, bob.getChatHistory().getMessages().size());
    }

    @Test
    void undoRemovesTheLastSentMessageFromSenderAndRecipients() {
        alice.send("Hello Bob", bob);
        alice.send("Hello everyone", bob, charlie);
        MessageMemento snapshot = alice.getChatHistory().getLastMessageSent().createMemento();

        Message undone = alice.undoLastMessage();

        assertEquals(snapshot, undone.createMemento());
        assertEquals("Hello Bob", alice.getChatHistory().getLastMessageSent().getContent());
        assertTrue(contains(bob, "Hello Bob"));
        assertFalse(contains(bob, "Hello everyone"));
        assertFalse(contains(charlie, "Hello everyone"));
    }

    @Test
    void undoKeepsMessagesTheUserReceived() {
        alice.send("Hello Bob", bob);
        bob.send("Reply", alice);

        alice.undoLastMessage();

        assertNull(alice.getChatHistory().getLastMessageSent());
        assertEquals(List.of("Reply"), contents(alice));
        assertEquals(List.of("Reply"), contents(bob));
    }

    @Test
    void blockStopsMessagesFromThatUserOnly() {
        charlie.block(bob);

        bob.send("Ping", alice, charlie);
        alice.send("Hi Charlie", charlie);

        assertTrue(server.isBlocking(charlie, bob));
        assertFalse(server.isBlocking(bob, charlie));
        assertEquals(List.of("Ping", "Hi Charlie"), contents(alice));
        assertEquals(List.of("Hi Charlie"), contents(charlie));
        assertEquals(List.of("Ping"), contents(bob));
    }

    @Test
    void usersCanViewHistoryWithASpecificUser() {
        alice.send("Hello Bob", bob);
        alice.send("Hello Charlie", charlie);
        bob.send("Hi Alice", alice);

        List<Message> withBob = alice.getMessagesWith(bob);

        assertEquals(List.of("Hello Bob", "Hi Alice"), contents(withBob));
        assertEquals(List.of("Hello Charlie"), contents(alice.getMessagesWith(charlie)));
    }

    @Test
    void undoWithNothingSentFailsAndLeavesHistoryAlone() {
        bob.send("Hi Alice", alice);

        assertThrows(IllegalStateException.class, alice::undoLastMessage);
        assertEquals(List.of("Hi Alice"), contents(alice));
    }

    private static boolean contains(User user, String content) {
        return contents(user).contains(content);
    }

    private static List<String> contents(User user) {
        return contents(user.getChatHistory().getMessages());
    }

    private static List<String> contents(List<Message> messages) {
        return messages.stream().map(Message::getContent).toList();
    }
}
