package patterns.chat;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class searchMessagesByUserTest {
    @Test
    void walksSentAndReceivedMessagesInOrder() {
        ChatServer server = new ChatServer();
        User alice = new User("Alice", server);
        User bob = new User("Bob", server);
        User charlie = new User("Charlie", server);
        LocalDateTime time = LocalDateTime.of(2026, 11, 15, 12, 0);
        Message toBob = new Message(alice, List.of(bob), "to bob", time);
        Message toCharlie = new Message(alice, List.of(charlie), "to charlie", time.plusMinutes(1));
        Message fromBob = new Message(bob, List.of(alice), "from bob", time.plusMinutes(2));

        searchMessagesByUser iterator = new searchMessagesByUser(List.of(toBob, toCharlie, fromBob), bob);

        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());
        assertSame(toBob, iterator.next());
        assertSame(fromBob, iterator.next());
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    void rejectsAMissingHistoryOrUser() {
        ChatServer server = new ChatServer();
        User alice = new User("Alice", server);

        assertThrows(IllegalArgumentException.class, () -> new searchMessagesByUser(null, alice));
        assertThrows(IllegalArgumentException.class, () -> new searchMessagesByUser(List.of(), null));
    }

    @Test
    void emptyHistoryHasNoMatches() {
        ChatServer server = new ChatServer();
        User alice = new User("Alice", server);
        searchMessagesByUser iterator = new searchMessagesByUser(List.of(), alice);

        assertFalse(iterator.hasNext());
        assertEquals("Alice", alice.getName());
    }
}
