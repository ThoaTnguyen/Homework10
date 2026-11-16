package patterns;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {
    @Test
    void mainPrintsTheChatDemo() {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
        try {
            Main.main(new String[0]);
        } finally {
            System.setOut(original);
        }

        String output = buffer.toString();
        assertTrue(output.contains("Behavioral Patterns: Chat App"));
        assertTrue(output.contains("Registered users: Alice, Bob, Charlie"));
        assertTrue(output.contains("Undid last message: Hello everyone @ "));
        assertTrue(output.contains("Charlie has Ping: false"));
        assertTrue(output.contains("Alice history with Bob:"));
        assertTrue(output.contains("  Bob to Alice, Charlie: Ping"));
    }
}
