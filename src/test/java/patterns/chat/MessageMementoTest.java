package patterns.chat;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MessageMementoTest {
    @Test
    void storesContentAndTimestamp() {
        LocalDateTime timestamp = LocalDateTime.of(2026, 11, 15, 12, 30);
        MessageMemento memento = new MessageMemento("Hello", timestamp);
        MessageMemento same = new MessageMemento("Hello", timestamp);

        assertEquals("Hello", memento.getContent());
        assertEquals(timestamp, memento.getTimestamp());
        assertEquals("Hello @ 2026-11-15T12:30", memento.toString());
        assertEquals(memento, same);
        assertEquals(memento.hashCode(), same.hashCode());
        assertNotEquals(memento, new MessageMemento("Goodbye", timestamp));
    }

    @Test
    void rejectsIncompleteSnapshots() {
        LocalDateTime timestamp = LocalDateTime.of(2026, 11, 15, 12, 30);

        assertThrows(IllegalArgumentException.class, () -> new MessageMemento(" ", timestamp));
        assertThrows(IllegalArgumentException.class, () -> new MessageMemento("Hello", null));
    }
}
