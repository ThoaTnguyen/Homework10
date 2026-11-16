package patterns.chat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {
    @Test
    void registersWithTheMediatorUnderItsName() {
        ChatServer server = new ChatServer();
        User alice = new User("Alice", server);

        assertEquals("Alice", alice.getName());
        assertEquals("Alice", alice.toString());
        assertTrue(server.isRegistered(alice));
        assertEquals(alice.hashCode(), new User("Alice", new ChatServer()).hashCode());
    }

    @Test
    void equalityUsesTheName() {
        ChatServer server = new ChatServer();
        User alice = new User("Alice", server);
        server.unregister(alice);
        User sameName = new User("Alice", server);

        assertEquals(alice, sameName);
        assertNotEquals(alice, new User("Bob", server));
    }

    @Test
    void rejectsAMissingNameOrServer() {
        ChatServer server = new ChatServer();

        assertThrows(IllegalArgumentException.class, () -> new User(" ", server));
        assertThrows(IllegalArgumentException.class, () -> new User("Alice", null));
    }

    @Test
    void rejectsAnEmptySendAndANullReceive() {
        ChatServer server = new ChatServer();
        User alice = new User("Alice", server);

        assertThrows(IllegalArgumentException.class, () -> alice.send("Hello"));
        assertThrows(IllegalArgumentException.class, () -> alice.receive(null));
    }
}
