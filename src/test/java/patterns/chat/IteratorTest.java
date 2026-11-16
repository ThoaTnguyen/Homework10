package patterns.chat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IteratorTest {
    private User alice;
    private User bob;
    private User charlie;

    @BeforeEach
    void setUp() {
        ChatServer server = new ChatServer();
        alice = new User("Alice", server);
        bob = new User("Bob", server);
        charlie = new User("Charlie", server);
    }

    @Test
    void iteratesMessagesSentOrReceivedByTheGivenUser() {
        alice.send("Hello Bob", bob);
        alice.send("Hello Charlie", charlie);
        bob.send("Hi Alice", alice);
        alice.send("Hello everyone", bob, charlie);

        assertEquals(List.of("Hello Bob", "Hi Alice", "Hello everyone"), contents(alice.iterator(bob)));
        assertEquals(List.of("Hello Charlie", "Hello everyone"), contents(alice.iterator(charlie)));
    }

    @Test
    void userIteratorDelegatesToChatHistory() {
        alice.send("Hello Bob", bob);
        bob.send("Hi Alice", alice);

        assertEquals(contents(alice.getChatHistory().iterator(bob)), contents(alice.iterator(bob)));
        assertInstanceOf(searchMessagesByUser.class, alice.iterator(bob));
        assertInstanceOf(searchMessagesByUser.class, alice.getChatHistory().iterator(bob));
    }

    @Test
    void skipsMessagesThatDoNotInvolveTheUser() {
        alice.send("Hello Charlie", charlie);

        Iterator messages = alice.iterator(bob);

        assertFalse(messages.hasNext());
        assertThrows(NoSuchElementException.class, messages::next);
    }

    @Test
    void hasNextDoesNotAdvance() {
        alice.send("Hello Bob", bob);
        alice.send("Hello everyone", bob, charlie);
        Iterator messages = bob.iterator(alice);

        assertTrue(messages.hasNext());
        assertTrue(messages.hasNext());
        assertEquals("Hello Bob", ((Message) messages.next()).getContent());
        assertEquals("Hello everyone", ((Message) messages.next()).getContent());
        assertFalse(messages.hasNext());
    }

    @Test
    void historyAndUserAreIterableByUserRatherThanJavaIterable() {
        assertTrue(alice instanceof IterableByUser);
        assertTrue(alice.getChatHistory() instanceof IterableByUser);
        assertFalse(alice instanceof Iterable);
        assertFalse(alice.getChatHistory() instanceof Iterable);
    }

    @SuppressWarnings("rawtypes")
    private static List<String> contents(Iterator messages) {
        List<String> contents = new ArrayList<>();
        while (messages.hasNext()) {
            contents.add(((Message) messages.next()).getContent());
        }
        return contents;
    }
}
