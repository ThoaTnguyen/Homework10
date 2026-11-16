package patterns.chat;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatDriverTest {
    @Test
    void demonstratesThreeUsersMediatorMementoAndIterator() {
        List<String> lines = ChatDriver.run();

        assertTrue(lines.contains("Registered users: Alice, Bob, Charlie"));
        assertTrue(lines.contains("Alice to Bob: Hello Bob"));
        assertTrue(lines.contains("Charlie has Hello Bob: false"));
        assertTrue(lines.contains("Alice to Bob, Charlie: Hello everyone"));
        assertTrue(lines.contains("Bob has Hello everyone: true"));
        assertTrue(lines.contains("Charlie blocks messages from Bob"));
        assertTrue(lines.contains("Alice has Ping: true"));
        assertTrue(lines.contains("Charlie has Ping: false"));
        assertTrue(lines.stream().anyMatch(line -> line.startsWith("Undid last message: Hello everyone @ ")));
        assertTrue(lines.contains("Alice last sent message: Hello Bob"));
        assertTrue(lines.contains("Bob has Hello everyone: false"));
        assertTrue(lines.contains("Charlie has Hello everyone: false"));
        assertTrue(lines.contains("Alice history with Bob:"));
        assertTrue(lines.contains("  Alice to Bob: Hello Bob"));
        assertTrue(lines.contains("  Bob to Alice, Charlie: Ping"));
        assertFalse(lines.contains("  Alice to Bob, Charlie: Hello everyone"));
    }
}
